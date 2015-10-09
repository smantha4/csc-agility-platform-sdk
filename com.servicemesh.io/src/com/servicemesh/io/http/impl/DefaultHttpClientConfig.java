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

import javax.net.ssl.KeyManager;

import com.servicemesh.io.http.Credentials;
import com.servicemesh.io.http.IHttpClientConfig;
import com.servicemesh.io.proxy.Proxy;

public class DefaultHttpClientConfig
    implements IHttpClientConfig
{
    private Integer connectionTimeout = null;
    private Integer socketTimeout = null;
    private Integer idleTimeout = null;
    private Integer retryValue = null;
    private Integer busyRetriesValue = null;
    private Long busyRetryInterval = null;
    private Integer maxConnections = null;
    private KeyManager[] keyManagers = null;
    private Credentials credentials = null;
    private Proxy proxy = null;

    @Override
    public Object clone()
        throws CloneNotSupportedException
    {
        DefaultHttpClientConfig clone = new DefaultHttpClientConfig();

        clone.setConnectionTimeout(connectionTimeout);
        clone.setSocketTimeout(socketTimeout);
        clone.setIdleTimeout(idleTimeout);
        clone.setRetries(retryValue);
        clone.setMaxConnections(maxConnections);
        clone.setKeyManagers(keyManagers);
        clone.setCredentials(credentials);

        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getConnectionTimeout()
    {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final Integer timeout)
    {
        connectionTimeout = timeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getSocketTimeout()
    {
        return socketTimeout;
    }

    public void setSocketTimeout(final Integer timeout)
    {
        socketTimeout = timeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getIdleTimeout()
    {
        return idleTimeout;
    }

    public void setIdleTimeout(final Integer timeout)
    {
        idleTimeout = timeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getRetries()
    {
    	return retryValue;
    }

    public void setRetries(final Integer retries)
    {
    	retryValue = retries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getBusyRetries()
    {
        return busyRetriesValue;
    }

    public void setBusyRetries(final Integer busyRetries)
    {
        busyRetriesValue = busyRetries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getBusyRetryInterval()
    {
        return busyRetryInterval;
    }

    public void setBusyRetryInterval(final long interval)
    {
        busyRetryInterval = interval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeyManager[] getKeyManagers()
    {
        return keyManagers;
    }

    public void setKeyManagers(final KeyManager[] keyManagers)
    {
        this.keyManagers = keyManagers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Credentials getCredentials()
    {
        return credentials;
    }

    public void setCredentials(Credentials credentials)
    {
        this.credentials = credentials;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getMaxConnections()
    {
        return maxConnections;
    }

    public void setMaxConnections(final Integer maxConnections)
    {
        this.maxConnections = maxConnections;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Proxy getProxy()
    {
        return proxy;
    }

    public void setProxy(Proxy proxy)
    {
        this.proxy = proxy;
    }
}
