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

public class HttpStatus
{
    final private HttpVersion _version;
    final private int _statusCode;
    final private String _reason;
    private String _toString = null;

    public HttpStatus(HttpVersion version, int statusCode, String reason)
    {
        if (version == null)
        {
            throw new IllegalArgumentException("Missing version");
        }

        if (statusCode < 0)
        {
            throw new IllegalArgumentException("Invalid status code");
        }

        _version = version;
        _statusCode = statusCode;
        _reason = reason;
    }

    public HttpVersion getHttpVersion()
    {
        return _version;
    }

    public int getStatusCode()
    {
        return _statusCode;
    }

    public String getReason()
    {
        return _reason;
    }

    @Override
    public String toString()
    {
        if (_toString == null)
        {
            StringBuilder sb = new StringBuilder("HttpStatus: version=");

            sb.append(_version);
            sb.append(", status code = ");
            sb.append(_statusCode);

            if (_reason != null)
            {
                sb.append(", reason = ");
                sb.append(_reason);
            }

            _toString = sb.toString();
        }

        return _toString;
    }
}
