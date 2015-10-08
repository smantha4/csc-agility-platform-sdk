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

public enum ProxyType
{
    HTTP_PROXY(0, "http"),
    HTTPS_PROXY(1, "https"),
    SOCKS5_PROXY(2, "socks5");

    private final int _id;
    private final String _scheme;

    private ProxyType(final int id, final String scheme)
    {
        _id = id;
        _scheme = scheme;
    }

    public int getId()
    {
        return _id;
    }

    public String getScheme()
    {
        return _scheme;
    }

    public static ProxyType match(final String scheme)
    {
        ProxyType proxyType = null;

        for (ProxyType nextProxyType : ProxyType.values()) {
            if (nextProxyType.getScheme().equalsIgnoreCase(scheme)) {
                proxyType = nextProxyType;
                break;
            }
        }

        return proxyType;
    }
}
