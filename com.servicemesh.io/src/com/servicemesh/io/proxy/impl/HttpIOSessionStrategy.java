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

package com.servicemesh.io.proxy.impl;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.reactor.IOSession;

import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.ProxySetupHandler;

public class HttpIOSessionStrategy
    implements SchemeIOSessionStrategy
{
    private NHttpClientConnectionManager _connectionMgr;

    public void setConnectionManager(NHttpClientConnectionManager connectionMgr)
    {
        _connectionMgr = connectionMgr;
    }

    @Override
    public boolean isLayeringRequired()
    {
        return true;
    }

    @Override
    public IOSession upgrade(final HttpHost host, final IOSession ioSession)
        throws IOException
    {
        IOSession rv = ioSession;

        if (host instanceof ProxyHost) {
            ProxyHost proxyHost = (ProxyHost)host;
            PipelinedChannel firstChannel = null;
            PipelinedChannel channel = null;

            while (proxyHost != null) {
                ProxySetupHandler handler;

                switch (proxyHost.getProxyType()) {
                    case HTTP_PROXY:
                        handler = new HttpProxySetupHandler(proxyHost, ioSession, firstChannel);
                        break;
                    case HTTPS_PROXY:
                        handler = new HttpsProxySetupHandler(proxyHost, ioSession, firstChannel);
                        break;
                    case SOCKS5_PROXY:
                        handler = new Socks5ProxySetupHandler(proxyHost, ioSession, firstChannel);
                        break;
                    default:
                        throw new RuntimeException("Unsupported proxy type: " + proxyHost.getProxyType().getScheme());
                }

                try {
                    PipelinedChannel newChannel = handler.initialize();

                    if (firstChannel == null) {
                        firstChannel = newChannel;
                    }

                    if (channel != null) {
                        channel.setDownstream(newChannel);
                    }

                    channel = newChannel;
                    proxyHost = (proxyHost.getTargetHost() instanceof ProxyHost) ? (ProxyHost)proxyHost.getTargetHost() : null;
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getLocalizedMessage(), ex);
                }
            }

            rv = new HttpProxyIOSession(ioSession, firstChannel);
        }

        return rv;
    }
}
