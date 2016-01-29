/**
 *              COPYRIGHT (C) 2008-2014 SERVICEMESH, INC.
 *                        ALL RIGHTS RESERVED.
 *                   CONFIDENTIAL AND PROPRIETARY.
 *
 *  ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS
 *  (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT
 *  ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.
 *  ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE
 *  AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED
 *  IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY
 *  OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY
 *  SERVICEMESH IS STRICTLY PROHIBITED.
 */

package com.servicemesh.io.http.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.AbstractFuture;
import com.servicemesh.io.http.IHttpCallback;
import com.servicemesh.io.http.IHttpResponseFuture;

public class DefaultHttpResponseFuture<T> extends AbstractFuture<T> implements IHttpResponseFuture<T>
{
    private static final Logger logger = Logger.getLogger(DefaultHttpResponseFuture.class);

    private final IHttpCallback<T> firstListener;
    private final List<IHttpCallback<T>> listeners = new ArrayList<IHttpCallback<T>>();

    public DefaultHttpResponseFuture(final IHttpCallback<T> callback)
    {
        super();
        firstListener = callback;
    }

    @Override
    public boolean set(final T result)
    {
        boolean rv = super.set(result);

        if (rv)
        {
            invokeListenerSuccess(firstListener, result);

            for (IHttpCallback<T> listener : listeners)
            {
                invokeListenerSuccess(listener, result);
            }
        }

        return rv;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        boolean rv = super.cancel(mayInterruptIfRunning);

        if (rv)
        {
            invokeListenerCancel(firstListener);

            for (IHttpCallback<T> listener : listeners)
            {
                invokeListenerCancel(listener);
            }
        }

        return rv;
    }

    @Override
    public boolean setException(final Throwable th)
    {
        boolean rv = super.setException(th);

        if (rv)
        {
            invokeListenerFailed(firstListener, th);

            for (IHttpCallback<T> listener : listeners)
            {
                invokeListenerFailed(listener, th);
            }
        }

        return rv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final IHttpCallback<T> listener) throws IllegalArgumentException
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("Emtpy listener");
        }

        if (listeners.contains(listener))
        {
            throw new IllegalArgumentException("Listener already registered");
        }

        listeners.add(listener);

        if (isCancelled())
        {
            invokeListenerCancel(listener);
        }
        else if (isDone())
        {
            try
            {
                T value = super.get();

                invokeListenerSuccess(listener, value);
            }
            catch (ExecutionException ex)
            {
                invokeListenerFailed(listener, ex.getCause());
            }
            catch (InterruptedException ex)
            {
                logger.error("Error retrieving result: " + ex.getMessage());
            }
        }
    }

    protected IHttpCallback<T> getFirstListener()
    {
        return firstListener;
    }

    private void invokeListenerSuccess(final IHttpCallback<T> listener, final T result)
    {
        try
        {
            listener.onCompletion(result);
        }
        catch (RuntimeException ex)
        {
            ex.printStackTrace();
            logger.error("Error executing onSuccess callback: " + ex.getMessage());
        }
    }

    private void invokeListenerCancel(final IHttpCallback<T> listener)
    {
        try
        {
            listener.onCancel();
        }
        catch (RuntimeException ex)
        {
            logger.error("Error executing onCancel callback: " + ex.getMessage());
        }
    }

    private void invokeListenerFailed(final IHttpCallback<T> listener, final Throwable failure)
    {
        try
        {
            listener.onFailure(failure);
        }
        catch (RuntimeException ex)
        {
            logger.error("Error executing onFailure callback: " + ex.getMessage());
        }
    }
}
