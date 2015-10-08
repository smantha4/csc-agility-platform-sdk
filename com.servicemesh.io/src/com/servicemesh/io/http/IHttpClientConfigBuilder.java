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

package com.servicemesh.io.http;

import javax.net.ssl.KeyManager;

import com.servicemesh.io.proxy.Proxy;

public interface IHttpClientConfigBuilder
{
    /**
     * Sets connect timeout value in milliseconds.
     * @param timeout Connect timeout value in milliseconds.
     * @return IHttpClientConfigBuilder with connect timeout set.
     */
    public IHttpClientConfigBuilder setConnectionTimeout(int timeout);

    /**
     * Sets socket timeout value in milliseconds.
     * @param timeout Socket timeout value in milliseconds.
     * @return IHttpClientConfigBuilder with socket timeout set.
     */
    public IHttpClientConfigBuilder setSocketTimeout(int timeout);

    /**
     * Set the maximum idle time for a session in milliseconds.
     * @param timeout Idle timeout value in milliseconds.
     * @return IHttpClientConfigBuilder with idle timeout set.
     */
    public IHttpClientConfigBuilder setIdleTimeout(int timeout);

    /**
     * Set the request retry count. 
     * @param retries The number of retries to attempt.
     * @return IHttpClientConfigBuilder with retries set.
     */
    public IHttpClientConfigBuilder setRetries(int retries);

    /**
     * Set the server busy retry count. By default, this will apply to HTTP response
     * codes 429 and 503.
     * 
     * @param retries The number of times to retry the request on server busy
     *                status codes.
     * @return IHttpClientConfigBuilder with retries set.
     */
    public IHttpClientConfigBuilder setServerBusyRetries(final int retries);

    /**
     * Set the server busy retry interval on HTTP response codes 429 and 503.
     * 
     * @param interval The retry interval in milliseconds.
     * @return IHttpClientConfigBuilder with retries set.
     */
    public IHttpClientConfigBuilder setServerBusyRetryInterval(final long interval);

    /**
     * Set the maximum connections for the client.
     * @param max The maximum connections value.
     * @return IHttpClientConfigBuilder with maximum connections set.
     */
    public IHttpClientConfigBuilder setMaxConnections(int max);

    /**
     * Set the key managers to be used for SSL/TLS communications.
     * @param keyManagers Sources for authentication keys.
     * @return IHttpClientConfigBuilder with key managers set.
     */
    public IHttpClientConfigBuilder setKeyManagers(final KeyManager[] keyManagers);

    /**
     * Set credentials for connections that require multi-request negotiation.
     * @param credentials Credentials to be set.
     * @return IHttpClientConfigBuilder with credentials set.
     */
    public IHttpClientConfigBuilder setCredentials(final Credentials credentials);

    /**
     * Set proxy configuration for the client.
     * @param proxy Proxy host configuration.
     * @return IHttpClientConfigBuilder with proxy configuration set.
     */
    public IHttpClientConfigBuilder setProxy(final Proxy proxy);

    /**
     * Clone the IHttpClientConfigBuilder object.
     * @return The cloned IHttpClientConfigBuilder object.
     */
    public IHttpClientConfigBuilder adapt();

    /**
     * Construct the IHttpClientConfig with the set values.
     * @return The constructed IHttpClientConfig object.
     */
    public IHttpClientConfig build();
}
