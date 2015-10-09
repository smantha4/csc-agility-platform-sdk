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

public class Host
{
    private final String _hostname;
    private final int _port;
    private final String _scheme;

    public Host(final String hostname, final int port)
    {
        this(hostname, port, (String)null);
    }

    public Host(final String hostname, final int port, final String scheme)
    {
        if ((hostname == null) || hostname.isEmpty()) {
            throw new IllegalArgumentException("Missing hostname");
        }

        _hostname = hostname;
        _port = port;
        _scheme = scheme;
    }

    public String getHostname()
    {
        return _hostname;
    }

    public int getPort()
    {
        return _port;
    }

    public String getScheme()
    {
        return _scheme;
    }

    @Override
    public String toString()
    {
       StringBuilder sb = new StringBuilder();

       if ((_scheme != null) && !_scheme.isEmpty()) {
           sb.append(_scheme);
           sb.append("//");
       }

       sb.append(getHostname());

       if (getPort() > 0) {
           sb.append(":");
           sb.append(getPort());
       }

       return sb.toString();
    }
}
