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

public enum HttpVersion
{
    HTTP_1_0("HTTP/1.0", "HTTP", 1, 0), HTTP_1_1("HTTP/1.1", "HTTP", 1, 1);

    final private String _name;
    final private String _protocol;
    final private int _majorVersion;
    final private int _minorVersion;
    final private String _value;

    private HttpVersion(String name, String protocol, int majorVersion, int minorVersion)
    {
        _name = name;
        _protocol = protocol;
        _majorVersion = majorVersion;
        _minorVersion = minorVersion;
        _value = protocol.toUpperCase() + "/" + _majorVersion + "." + _minorVersion;
    }

    public static HttpVersion find(String protocol, int major, int minor)
    {
        HttpVersion match = null;

        for (HttpVersion version : HttpVersion.values())
        {
            if (version.getProtocol().equalsIgnoreCase(protocol) && (version.getMajorVersion() == major)
                    && (version.getMinorVersion() == minor))
            {
                match = version;
                break;
            }
        }

        return match;
    }

    public String getName()
    {
        return _name;
    }

    public String getProtocol()
    {
        return _protocol;
    }

    public int getMajorVersion()
    {
        return _majorVersion;
    }

    public int getMinorVersion()
    {
        return _minorVersion;
    }

    public String value()
    {
        return _value;
    }

    @Override
    public String toString()
    {
        return _value;
    }
}
