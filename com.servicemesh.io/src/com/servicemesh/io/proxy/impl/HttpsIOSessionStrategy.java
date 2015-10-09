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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.ssl.SSLIOSession;
import org.apache.http.nio.reactor.ssl.SSLMode;
import org.apache.http.nio.reactor.ssl.SSLSetupHandler;

import com.google.common.base.Preconditions;

import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.ProxySetupHandler;

public class HttpsIOSessionStrategy
    extends SSLIOSessionStrategy
{
    private final SSLContext _sslContext;

    public HttpsIOSessionStrategy(final SSLContext sslContext)
    {
        this(sslContext, ALLOW_ALL_HOSTNAME_VERIFIER);
    }

    public HttpsIOSessionStrategy(final SSLContext sslContext, final X509HostnameVerifier hostnameVerifier)
    {
        super(sslContext, hostnameVerifier);
        _sslContext = sslContext;
    }

    @Override
    public SSLIOSession upgrade(final HttpHost host, final IOSession ioSession)
        throws IOException
    {
        Preconditions.checkArgument(!(ioSession instanceof SSLIOSession), "I/O session is already upgraded to TLS/SSL");

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

            SSLSetupHandler setupHandler = new SSLSetupHandler() {
                @Override
                public void initalize(final SSLEngine sslengine)
                    throws SSLException
                {
                    initializeEngine(sslengine);
                }

                @Override
                public void verify(final IOSession ioSession, final SSLSession sslSession)
                    throws SSLException
                {
                    verifySession(((ProxyHost)host).getEndpoint(), ioSession, sslSession);
                }
            };

            final SSLIOSession sslIOSession = new HttpsProxyIOSession(ioSession, SSLMode.CLIENT,
                                                                      _sslContext, setupHandler,
                                                                      firstChannel);

            ioSession.setAttribute(SSLIOSession.SESSION_KEY, sslIOSession);
            sslIOSession.initialize();
            return sslIOSession;
        } else {
            return super.upgrade(host, ioSession);
        }
    }
}
