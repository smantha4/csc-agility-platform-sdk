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

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.concurrent.FutureCallback;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerHandler;
import com.servicemesh.io.http.HttpStatus;
import com.servicemesh.io.http.HttpVersion;
import com.servicemesh.io.http.IHttpCallback;
import com.servicemesh.io.http.IHttpClientConfig;
import com.servicemesh.io.http.IHttpRequest;
import com.servicemesh.io.http.IHttpResponse;
import com.servicemesh.io.util.IOUtil;

public class DefaultHttpCallbackHandler<T> implements FutureCallback<HttpResponse>
{
    private static final Logger logger = Logger.getLogger(DefaultHttpCallbackHandler.class);

    private final DefaultHttpResponseFuture<T> future;
    private final DefaultHttpClient _client;
    private final DefaultServerBusyRetryStrategy _busyStrategy;
    private int _retries;
    private IHttpRequest _request;

    /*
     * I left this constructor in here for the unit tests - it shouldn't be called otherwise
     * as far as I know.
     */
    protected DefaultHttpCallbackHandler(final DefaultHttpResponseFuture<T> future)
    {
        this(future, (DefaultHttpClient) null, (IHttpRequest) null, (IHttpClientConfig) null);
    }

    public DefaultHttpCallbackHandler(final DefaultHttpResponseFuture<T> future, final DefaultHttpClient client,
            final IHttpRequest request, final IHttpClientConfig config)
    {
        this.future = Preconditions.checkNotNull(future, "Missing callback");
        _client = client;
        _request = request;

        if (config != null)
        {
            if (config.getRetries() != null)
            {
                _retries = (config.getRetries() > 0) ? config.getRetries() : 0;
            }

            if (config.getBusyRetries() != null)
            {
                final int busyRetries = (config.getBusyRetries() > 0) ? config.getBusyRetries() : 0;
                long busyRetryInterval;

                if (busyRetries >= 0)
                {
                    busyRetryInterval = (config.getBusyRetryInterval() != null) ? config.getBusyRetryInterval()
                            : DefaultServerBusyRetryStrategy.DEFAULT_BUSY_RETRY_INTERVAL;
                    _busyStrategy = new DefaultServerBusyRetryStrategy(busyRetries, busyRetryInterval);
                }
                else
                {
                    _busyStrategy = new DefaultServerBusyRetryStrategy();
                }
            }
            else
            {
                _busyStrategy = new DefaultServerBusyRetryStrategy();
            }
        }
        else
        {
            _retries = 0;
            _busyStrategy = new DefaultServerBusyRetryStrategy();
        }
    }

    @Deprecated
    public DefaultHttpCallbackHandler(final DefaultHttpResponseFuture<T> future, final DefaultHttpClient client,
            final IHttpRequest request, final int retries)
    {
        if (future == null)
        {
            throw new IllegalArgumentException("Missing callback");
        }

        this.future = future;
        _client = client;
        _request = request;
        _retries = retries;
        _busyStrategy = new DefaultServerBusyRetryStrategy();
    }

    @Override
    public void completed(final HttpResponse response)
    {
        try
        {
            final IHttpResponse httpResponse = convertResponse(response);

            if (_busyStrategy.retry(httpResponse))
            {
                logger.error("Service busy status=" + httpResponse.getStatusCode() + " - queuing request for retry: "
                        + _request.getUri().toString());
                httpResponse.close();

                final Reactor reactor = IOUtil.getTimerReactor();
                final TimerHandler timerHandler = new TimerHandler() {
                    @Override
                    public long timerFire(final long scheduledTime, final long actualTime)
                    {
                        logger.error("Retrying request: " + _request.getUri().toString());
                        _client.execute(_request, DefaultHttpCallbackHandler.this);
                        return 0;
                    }
                };

                reactor.timerCreateRel(_busyStrategy.retryInterval(), timerHandler);
            }
            else
            {
                final IHttpCallback<T> callback = future.getFirstListener();

                future.set(callback.decoder(httpResponse));
            }
        }
        catch (final Throwable th)
        {
            logger.error("Error while completing IO request: " + th.getLocalizedMessage(), th);

            future.setException(th);
        }
    }

    @Override
    public void cancelled()
    {
        future.cancel(true);
    }

    @Override
    public void failed(Exception ex)
    {
        if (_retries-- > 0)
        {
            logger.error("HTTP Error: " + ex.getMessage() + " - Retrying request: " + _request.getUri().toString());
            _client.execute(_request, (FutureCallback<HttpResponse>) this);
        }
        else
        {
            future.setException(ex);
        }
    }

    private IHttpResponse convertResponse(HttpResponse response)
    {
        DefaultHttpResponse httpResponse = new DefaultHttpResponse();
        Header[] headers = response.getAllHeaders();
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();

        if (headers != null)
        {
            for (Header header : headers)
            {
                httpResponse.addHeader(new BaseHttpHeader(header.getName(), header.getValue()));
            }
        }

        if (statusLine != null)
        {
            HttpVersion httpVersion;
            ProtocolVersion version = statusLine.getProtocolVersion();

            if (version != null)
            {
                httpVersion = HttpVersion.find(version.getProtocol(), version.getMajor(), version.getMinor());

                if (httpVersion == null)
                {
                    logger.warn("Unmatched protocol version: " + version.toString());
                    httpVersion = HttpVersion.HTTP_1_1;
                }
            }
            else
            {
                httpVersion = HttpVersion.HTTP_1_1;
                logger.warn("Response with null protocol version");
            }

            HttpStatus status = new HttpStatus(httpVersion, statusLine.getStatusCode(), statusLine.getReasonPhrase());
            httpResponse.setStatus(status);
        }

        if (entity != null)
        {
            try
            {
                InputStream stream = entity.getContent();
                httpResponse.setContent(stream); //  Could be null
            }
            catch (IOException ex)
            {
                logger.error("Error reading content: " + ex.getMessage(), ex);
            }
        }

        return httpResponse;
    }
}
