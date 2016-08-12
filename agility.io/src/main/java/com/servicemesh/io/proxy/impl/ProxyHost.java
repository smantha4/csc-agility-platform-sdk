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

import org.apache.http.HttpHost;

import com.servicemesh.io.proxy.Host;
import com.servicemesh.io.proxy.Proxy;
import com.servicemesh.io.proxy.ProxyType;

public class ProxyHost extends HttpHost
{
    private static final long serialVersionUID = -6644556465101928281L;

    private final ProxyType _type;
    private final String _principal;
    private final String _credentials;
    private HttpHost _targetHost;

    public ProxyHost(final String hostname, final int port, final String scheme, final HttpHost targetHost)
    {
        this(hostname, port, scheme, (String) null, (String) null, targetHost);
    }

    public ProxyHost(final String hostname, final int port, final String scheme, final String principal, final String credentials,
            final HttpHost targetHost)
    {
        super(hostname, port, scheme);
        _targetHost = targetHost;
        _principal = principal;
        _credentials = credentials;
        _type = ProxyType.match(scheme);
    }

    public ProxyHost(final Proxy proxy)
    {
        super(proxy.getHostname(), proxy.getPort(), proxy.getType().getScheme());
        _principal = proxy.getAdmin();
        _credentials = proxy.getPassword();
        _type = proxy.getType();

        Host targetHost = proxy.getTargetHost();
        if (targetHost != null)
        {
            if (targetHost instanceof Proxy)
            {
                _targetHost = new ProxyHost((Proxy) targetHost);
            }
            else
            {
                _targetHost = new HttpHost(targetHost.getHostname(), targetHost.getPort(), targetHost.getScheme());
            }
        }
    }

    public HttpHost getTargetHost()
    {
        return _targetHost;
    }

    public String getPrincipal()
    {
        return _principal;
    }

    public String getCredentials()
    {
        return _credentials;
    }

    public void setEndpoint(final HttpHost host)
    {
        if (_targetHost instanceof ProxyHost)
        {
            ((ProxyHost) _targetHost).setEndpoint(host);
        }
        else
        {
            _targetHost = host;
        }
    }

    public HttpHost getEndpoint()
    {
        return (_targetHost instanceof ProxyHost) ? ((ProxyHost) _targetHost).getEndpoint() : _targetHost;
    }

    public ProxyType getProxyType()
    {
        return _type;
    }

    @Override
    public String getSchemeName()
    {
        HttpHost endpoint = _targetHost;

        while (endpoint instanceof ProxyHost)
        {
            endpoint = ((ProxyHost) endpoint).getTargetHost();
        }

        return endpoint.getSchemeName();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(_type.getScheme());
        sb.append("//");
        sb.append(getHostName());

        if (getPort() > 0)
        {
            sb.append(":");
            sb.append(getPort());
        }

        if (_targetHost != null)
        {
            sb.append("->");
            sb.append(_targetHost.toString());
        }

        return sb.toString();
    }
}
