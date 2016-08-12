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
import java.util.List;

public interface IHttpResponse
{
    /**
     * Returns all the headers for the response.
     *
     * @return The list of headers in the response.
     */
    public List<IHttpHeader> getHeaders();

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

    public HttpStatus getStatus();

    public int getStatusCode();

    public String getContent();

    public byte[] getContentAsByteArray();

    public InputStream getContentAsStream();

    public long getContentLength();

    public void close();
}
