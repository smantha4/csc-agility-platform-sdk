/**
 *              COPYRIGHT (C) 2008-2015 SERVICEMESH, INC.
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

package com.servicemesh.io.http.impl;

import java.net.URI;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class SMRedirectStrategy extends DefaultRedirectStrategy
{
    /**
     * Redirectable methods.
     */
    private static final String[] REDIRECT_METHODS =
            new String[] { HttpGet.METHOD_NAME, HttpPost.METHOD_NAME, HttpHead.METHOD_NAME, HttpDelete.METHOD_NAME };

    @Override
    protected boolean isRedirectable(final String method)
    {
        boolean rv = false;

        for (final String methodName : REDIRECT_METHODS)
        {
            if (methodName.equalsIgnoreCase(method))
            {
                rv = true;
                break;
            }
        }

        return rv;
    }

    @Override
    public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response, final HttpContext context)
            throws ProtocolException
    {
        final URI uri = getLocationURI(request, response, context);
        final String method = request.getRequestLine().getMethod();
        final int status = response.getStatusLine().getStatusCode();
        HttpUriRequest newRequest = null;

        if (status == HttpStatus.SC_TEMPORARY_REDIRECT)
        {
            newRequest = RequestBuilder.copy(request).setUri(uri).build();

            newRequest.removeHeaders(HTTP.TRANSFER_ENCODING);
            newRequest.removeHeaders(HTTP.CONTENT_LEN);
        }
        else if (method.equalsIgnoreCase(HttpGet.METHOD_NAME))
        {
            newRequest = new HttpGet(uri);
        }
        else if (method.equalsIgnoreCase(HttpHead.METHOD_NAME))
        {
            newRequest = new HttpHead(uri);
        }
        else if (method.equalsIgnoreCase(HttpPost.METHOD_NAME))
        {
            newRequest = new HttpPost(uri);
        }
        else if (method.equalsIgnoreCase(HttpDelete.METHOD_NAME))
        {
            newRequest = new HttpDelete(uri);
        }
        else
        {
            newRequest = new HttpGet(uri);
        }

        return newRequest;
    }
}
