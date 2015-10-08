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

import java.net.URI;
import java.util.List;

import com.servicemesh.io.http.impl.DefaultHttpClient;
import com.servicemesh.io.http.impl.DefaultHttpClientConfigBuilder;
import com.servicemesh.io.http.impl.BaseHttpHeader;
import com.servicemesh.io.http.impl.DefaultHttpRequest;

public class HttpClientFactory
{
    private HttpClientFactory() {}

    private static class Holder
    {
        private static final HttpClientFactory _instance = new HttpClientFactory();
    }

    public static HttpClientFactory getInstance()
    {
        return Holder._instance;
    }

    public IHttpClientConfigBuilder getConfigBuilder()
    {
        return new DefaultHttpClientConfigBuilder();
    }

    public IHttpClient getClient(final IHttpClientConfig config)
    {
        return new DefaultHttpClient(config);
    }

    public IHttpRequest createRequest(HttpMethod method)
    {
        return new DefaultHttpRequest(method);
    }

    public IHttpRequest createRequest(HttpMethod method, URI uri)
    {
        return new DefaultHttpRequest(method, uri);
    }

    public IHttpHeader createHeader(String name, String value)
    {
        return new BaseHttpHeader(name, value);
    }

    public IHttpHeader createHeader(String name, List<String> values)
    {
        return new BaseHttpHeader(name, values);
    }
}
