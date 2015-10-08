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

package com.servicemesh.io.proxy;

public class Proxy
    extends Host
{
    private final ProxyType _type;
    private final String _admin;
    private final String _password;
    private Host _targetHost;

    public Proxy(final String hostname, final int port, final ProxyType type, final Host targetHost)
    {
        this(hostname, port, type, targetHost, (String)null, (String)null);
    }

    public Proxy(final String hostname, final int port, final ProxyType type, final Host targetHost, final String admin, final String password)
    {
        super(hostname, port);

        if (type == null) {
            throw new IllegalArgumentException("Missing proxy type");
        }

        _targetHost = targetHost;
        _type = type;
        _admin = admin;
        _password = password;
    }

    public ProxyType getType()
    {
        return _type;
    }

    public String getAdmin()
    {
        return _admin;
    }

    public String getPassword()
    {
        return _password;
    }

    public void setTargetHost(Host targetHost)
    {
        _targetHost = targetHost;
    }

    public Host getTargetHost()
    {
        return _targetHost;
    }

    @Override
    public String toString()
    {
       StringBuilder sb = new StringBuilder();

       sb.append(_type.getScheme());
       sb.append("//");
       sb.append(getHostname());

       if (getPort() > 0) {
           sb.append(":");
           sb.append(getPort());
       }

       if (_targetHost != null) {
           sb.append("->");
           sb.append(_targetHost.toString());
       }

       return sb.toString();
    }
}
