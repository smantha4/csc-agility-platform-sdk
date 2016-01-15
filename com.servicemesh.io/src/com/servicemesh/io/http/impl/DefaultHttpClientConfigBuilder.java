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
import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.proxy.Proxy;

public class DefaultHttpClientConfigBuilder implements IHttpClientConfigBuilder
{
    final DefaultHttpClientConfig _config;

    public DefaultHttpClientConfigBuilder()
    {
        this(new DefaultHttpClientConfig());
    }

    private DefaultHttpClientConfigBuilder(DefaultHttpClientConfig config)
    {
        _config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setConnectionTimeout(int timeout)
    {
        _config.setConnectionTimeout(timeout);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setSocketTimeout(int timeout)
    {
        _config.setSocketTimeout(timeout);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setIdleTimeout(int timeout)
    {
        _config.setIdleTimeout(timeout);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setRetries(int retries)
    {
        _config.setRetries(retries);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setServerBusyRetries(final int retries)
    {
        _config.setBusyRetries(retries);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setServerBusyRetryInterval(final long interval)
    {
        _config.setBusyRetryInterval(interval);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setKeyManagers(final KeyManager[] keyManagers)
    {
        _config.setKeyManagers(keyManagers);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfig build()
    {
        return _config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setCredentials(final Credentials credentials)
    {
        _config.setCredentials(credentials);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setMaxConnections(int max)
    {
        _config.setMaxConnections(max);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder setProxy(final Proxy proxy)
    {
        _config.setProxy(proxy);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHttpClientConfigBuilder adapt()
    {
        IHttpClientConfigBuilder adapted = null;

        try
        {
            adapted = new DefaultHttpClientConfigBuilder((DefaultHttpClientConfig) _config.clone());
        }
        catch (CloneNotSupportedException ex)
        {
            // Ignore, clone is supported
        }

        return adapted;
    }
}
