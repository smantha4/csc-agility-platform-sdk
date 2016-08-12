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

import org.junit.Assert;
import org.junit.Test;

import com.servicemesh.io.proxy.Proxy;
import com.servicemesh.io.proxy.ProxyType;

public class HttpClientConfigTest
{
    @Test
    public void testHttpClientConfig() throws Exception
    {
        KeyManager keyMgr = new KeyManager() {
        };
        KeyManager[] keyMgrs = new KeyManager[] { keyMgr };
        Credentials creds = new Credentials(Credentials.CredentialsType.CREDENTIALS_TYPE_USERNAMEPASSORD);
        Proxy proxy = new Proxy("localhost", 1080, ProxyType.HTTP_PROXY, null);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClientConfig config = builder.build();

        Assert.assertNull(config.getConnectionTimeout());
        Assert.assertNull(config.getSocketTimeout());
        Assert.assertNull(config.getIdleTimeout());
        Assert.assertNull(config.getRetries());
        Assert.assertNull(config.getMaxConnections());
        Assert.assertNull(config.getKeyManagers());
        Assert.assertNull(config.getKeyManagers());
        Assert.assertNull(config.getCredentials());
        Assert.assertNull(config.getProxy());

        builder.setConnectionTimeout(300);
        builder.setSocketTimeout(500);
        builder.setIdleTimeout(444);
        builder.setRetries(4);
        builder.setMaxConnections(23);
        builder.setKeyManagers(keyMgrs);
        builder.setCredentials(creds);
        builder.setProxy(proxy);
        config = builder.build();
        Assert.assertNotNull(config.getConnectionTimeout());
        Assert.assertEquals(300, config.getConnectionTimeout().intValue());
        Assert.assertNotNull(config.getSocketTimeout());
        Assert.assertEquals(500, config.getSocketTimeout().intValue());
        Assert.assertNotNull(config.getIdleTimeout());
        Assert.assertEquals(444, config.getIdleTimeout().intValue());
        Assert.assertNotNull(config.getRetries());
        Assert.assertEquals(4, config.getRetries().intValue());
        Assert.assertNotNull(config.getMaxConnections());
        Assert.assertEquals(23, config.getMaxConnections().intValue());
        Assert.assertNotNull(config.getKeyManagers());
        Assert.assertEquals(1, config.getKeyManagers().length);
        Assert.assertNotNull(config.getCredentials());
        Assert.assertEquals(Credentials.CredentialsType.CREDENTIALS_TYPE_USERNAMEPASSORD, config.getCredentials().getType());
        Assert.assertNotNull(config.getProxy());
        Assert.assertEquals("localhost", config.getProxy().getHostname());
    }
}
