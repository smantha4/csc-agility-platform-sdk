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

public interface IHttpClientConfig
{
    /**
     * Returns the set connection timeout value.
     * 
     * @return The connection timeout in milliseconds if set, otherwise null.
     */
    public Integer getConnectionTimeout();

    /**
     * Returns the set socket timeout value.
     * 
     * @return The socket timeout in milliseconds if set, otherwise null.
     */
    public Integer getSocketTimeout();

    /**
     * Returns the set idle timeout value.
     * 
     * @return The idle timeout in milliseconds if set, otherwise null.
     */
    public Integer getIdleTimeout();

    /**
     * Returns the set retry value.
     * 
     * @return The retry value if set, otherwise null.
     */
    public Integer getRetries();

    /**
     * Returns the set busy retry value.
     * 
     * @return The busy retry value if set, otherwise null.
     */
    public Integer getBusyRetries();

    /**
     * Returns the set busy retry interval in milliseconds.
     * 
     * @return The busy retry interval if set, otherwise null.
     */
    public Long getBusyRetryInterval();

    /**
     * Returns the max connection value.
     * 
     * @return The max connection value if set, otherwise null.
     */
    public Integer getMaxConnections();

    /**
     * Returns the configured key managers.
     * 
     * @return The configured key managers if set, otherwise null.
     */
    public KeyManager[] getKeyManagers();

    /**
     * Returns the configured credentials.
     * 
     * @return The configure credentials if set, otherwise null.
     */
    public Credentials getCredentials();

    /**
     * Returns the proxy configuration.
     * 
     * @return The proxy host configuration if set, otherwise null.
     */
    public Proxy getProxy();

    /**
     * Returns the setting for manual cookie configuration.
     * 
     * @return The boolean value for manual cookie configuration.
     */
    public Boolean getManualCookieManagement();
}
