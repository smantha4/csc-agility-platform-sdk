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

import java.io.InputStream;
import java.net.URI;
import java.util.List;

public interface IHttpRequest
{
    public HttpVersion getHttpVersion();

    public HttpMethod getMethod();

    public void setUri(final String uri);

    public void setUri(final URI uri);

    public URI getUri();

    /**
     * Adds the specified header to the request. No checks for duplicate header names or duplicate values are performed.
     *
     * @param header
     *            The header to be added to the request.
     */
    public void addHeader(final IHttpHeader header);

    /**
     * Adds the specified header to the request. All pre-existing headers with the matching header name will be removed before
     * adding the current header.
     *
     * @param header
     *            The header to be added to the request.
     */
    public void setHeader(final IHttpHeader header);

    /**
     * Adds the specified headers to the request. All pre-existing headers with the matching header names will be removed before
     * adding the current headers.
     *
     * @param headers
     *            The list of headers to be added to the request.
     */
    public void setHeaders(final List<IHttpHeader> headers);

    /**
     * Returns the first header with the matching name or null if a matching name is not found.
     *
     * @param name
     *            The name of the header to search for.
     * @return The first header with the matching name.
     */
    public IHttpHeader getHeader(final String name);

    /**
     * Returns all the headers with the matching name or null if a matching name is not found.
     *
     * @param name
     *            The name of the headers to search for.
     * @return The list of headers with matching names.
     */
    public List<IHttpHeader> getHeaders(final String name);

    /**
     * Returns all the headers for the request.
     *
     * @return The list of headers in the request.
     */
    public List<IHttpHeader> getHeaders();

    /**
     * Removes the first header with the matching name.
     *
     * @param name
     *            The name of the header to remove.
     * @return The header that was removed or null if none was removed.
     */
    public IHttpHeader removeHeader(final String name);

    /**
     * Removes all headers with the matching name.
     *
     * @param name
     *            The name of the headers to remove.
     * @return The list of headers that were removed. Returns an empty list if no headers were removed.
     */
    public List<IHttpHeader> removeHeaders(final String name);

    public void setContent(final String content);

    public void setContent(final byte[] content);

    public void setContent(final InputStream content);

    public void setContent(final InputStream content, long size);

    public long getContentLength();

    public String getContent();

    public byte[] getContentAsByteArray();

    public InputStream getContentAsStream();

    /**
     * Sets request timeout value in milliseconds.
     * 
     * @param timeout
     *            Connect timeout value in milliseconds.
     */
    public void setRequestTimeout(int timeout);

    /**
     * Returns the request timeout value.
     * 
     * @return The request timeout in milliseconds if set, otherwise null.
     */
    public Integer getRequestTimeout();
}
