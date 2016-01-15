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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.servicemesh.io.http.impl.DefaultHttpRequest;

public class HttpRequestTest
{
    @Test
    public void testHttpRequest() throws Exception
    {
        String stringUri = "https://localhost:8443/agility/login.html";
        URI uri = new URI(stringUri);

        IHttpRequest request = new DefaultHttpRequest(HttpMethod.CONNECT, stringUri);
        Assert.assertEquals(HttpVersion.HTTP_1_0, request.getHttpVersion());
        Assert.assertEquals(HttpMethod.CONNECT, request.getMethod());
        Assert.assertEquals(stringUri, request.getUri().toString());

        request = new DefaultHttpRequest(HttpMethod.POST, uri);
        Assert.assertEquals(HttpVersion.HTTP_1_1, request.getHttpVersion());
        Assert.assertEquals(HttpMethod.POST, request.getMethod());
        Assert.assertTrue(uri.equals(request.getUri()));

        request = HttpClientFactory.getInstance().createRequest(HttpMethod.POST);
        Assert.assertEquals(HttpVersion.HTTP_1_1, request.getHttpVersion());
        Assert.assertEquals(HttpMethod.POST, request.getMethod());
        Assert.assertNull(request.getUri());

        request = HttpClientFactory.getInstance().createRequest(HttpMethod.POST, uri);
        Assert.assertEquals(HttpVersion.HTTP_1_1, request.getHttpVersion());
        Assert.assertEquals(HttpMethod.POST, request.getMethod());
        Assert.assertTrue(uri.equals(request.getUri()));
    }

    @Test
    public void testHttpRequestNegative() throws Exception
    {
        String stringUri = "https://localhost:8443/agility/login.html";
        URI uri = new URI(stringUri);

        try
        {
            new DefaultHttpRequest(null, stringUri);
            Assert.fail("Constructed with missing HttpMethod");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Missing HttpMethod", ex.getMessage());
        }

        try
        {
            new DefaultHttpRequest(HttpMethod.GET, (URI) null);
            Assert.fail("Constructed with missing URI");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Missing URI", ex.getMessage());
        }

        try
        {
            new DefaultHttpRequest(HttpMethod.GET, (String) null);
            Assert.fail("Constructed with invalid URI");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertTrue(ex.getMessage().contains("Invalid uri"));
        }

        try
        {
            new DefaultHttpRequest(HttpMethod.GET, "");
            Assert.fail("Constructed with invalid URI");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertTrue(ex.getMessage().contains("Invalid uri"));
        }

        try
        {
            HttpClientFactory.getInstance().createRequest(null);
            Assert.fail("Constructed with missing HttpMethod");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Missing HttpMethod", ex.getMessage());
        }

        try
        {
            HttpClientFactory.getInstance().createRequest(null, uri);
            Assert.fail("Constructed with missing HttpMethod");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Missing HttpMethod", ex.getMessage());
        }

        try
        {
            HttpClientFactory.getInstance().createRequest(HttpMethod.GET, null);
            Assert.fail("Constructed with missing URI");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Missing URI", ex.getMessage());
        }
    }

    @Test
    public void testHeaders() throws Exception
    {
        final String nonHeaderName = "Author";
        String stringUri = "https://localhost:8443/agility/login.html";
        URI uri = new URI(stringUri);
        String headerName = "Authorization";
        String headerValue = "Basic YWRtaW46TTNzaEBkbWluIQ==";
        IHttpHeader header1 = HttpClientFactory.getInstance().createHeader(headerName, headerValue);
        IHttpRequest request = new DefaultHttpRequest(HttpMethod.GET, uri);
        List<IHttpHeader> returnedHeaders = request.getHeaders();
        Assert.assertEquals(0, returnedHeaders.size());
        IHttpHeader matchingHeader = request.getHeader(headerName);
        List<IHttpHeader> matchingHeaders = request.getHeaders(headerName);
        IHttpHeader nullHeader = request.getHeader(nonHeaderName);
        List<IHttpHeader> nullHeaders = request.getHeaders(nonHeaderName);
        Assert.assertNull(matchingHeader);
        Assert.assertNull(nullHeader);
        Assert.assertNotNull(matchingHeaders);
        Assert.assertEquals(0, matchingHeaders.size());
        Assert.assertNotNull(nullHeaders);
        Assert.assertEquals(0, nullHeaders.size());

        // Test basic request
        request.setHeader(header1);
        returnedHeaders = request.getHeaders();
        Assert.assertEquals(1, returnedHeaders.size());
        Assert.assertEquals(headerName, returnedHeaders.get(0).getName());
        Assert.assertEquals(headerValue, returnedHeaders.get(0).getValue());
        matchingHeader = request.getHeader(headerName);
        Assert.assertNotNull(matchingHeader);
        matchingHeaders = request.getHeaders(headerName);
        Assert.assertNotNull(matchingHeaders);
        Assert.assertEquals(1, matchingHeaders.size());
        Assert.assertEquals(headerName, matchingHeaders.get(0).getName());
        Assert.assertEquals(headerValue, matchingHeaders.get(0).getValue());
        nullHeader = request.getHeader(nonHeaderName);
        Assert.assertNull(nullHeader);
        nullHeaders = request.getHeaders(nonHeaderName);
        Assert.assertNotNull(nullHeaders);
        Assert.assertEquals(0, nullHeaders.size());

        String headerName2 = headerName;
        String headerValue2 = "Basic cnNhbmNoZXo6TTNzaEBkbWluIQ==";
        IHttpHeader header2 = HttpClientFactory.getInstance().createHeader(headerName2, headerValue2);
        request.setHeader(header2);
        returnedHeaders = request.getHeaders();
        Assert.assertEquals(1, returnedHeaders.size());
        Assert.assertEquals(headerName2, returnedHeaders.get(0).getName());
        Assert.assertEquals(headerValue2, returnedHeaders.get(0).getValue());

        String headerName3 = "Connection";
        String headerValue3 = "keep-alive";
        IHttpHeader header3 = HttpClientFactory.getInstance().createHeader(headerName3, headerValue3);
        request.setHeader(header3);
        returnedHeaders = request.getHeaders();
        Assert.assertEquals(2, returnedHeaders.size());

        boolean header1Found = false;
        boolean header2Found = false;
        boolean header3Found = false;

        for (IHttpHeader nextHeader : returnedHeaders)
        {
            if (headerName.equals(nextHeader.getName()) && headerValue.equals(nextHeader.getValue()))
            {
                header1Found = true;
            }
            else if (headerName2.equals(nextHeader.getName()) && headerValue2.equals(nextHeader.getValue()))
            {
                header2Found = true;
            }
            else if (headerName3.equals(nextHeader.getName()) && headerValue3.equals(nextHeader.getValue()))
            {
                header3Found = true;
            }
        }
        Assert.assertFalse("HttpHeader1 found, shouldn't have", header1Found);
        Assert.assertTrue("HttpHeader2 not found", header2Found);
        Assert.assertTrue("HttpHeader3 not found", header3Found);

        // Test setHeaders()
        List<IHttpHeader> headersList = new ArrayList<IHttpHeader>();
        request = new DefaultHttpRequest(HttpMethod.GET, uri);
        returnedHeaders = request.getHeaders();
        Assert.assertEquals(0, returnedHeaders.size());

        request.setHeaders(headersList);
        returnedHeaders = request.getHeaders();
        Assert.assertEquals(0, returnedHeaders.size());

        headersList.add(header1);
        headersList.add(header2);
        headersList.add(header3);
        request.setHeaders(headersList);
        returnedHeaders = request.getHeaders();
        Assert.assertEquals(2, returnedHeaders.size());

        header1Found = false;
        header2Found = false;
        header3Found = false;

        for (IHttpHeader nextHeader : returnedHeaders)
        {
            if (headerName.equals(nextHeader.getName()) && headerValue.equals(nextHeader.getValue()))
            {
                header1Found = true;
            }
            else if (headerName2.equals(nextHeader.getName()) && headerValue2.equals(nextHeader.getValue()))
            {
                header2Found = true;
            }
            else if (headerName3.equals(nextHeader.getName()) && headerValue3.equals(nextHeader.getValue()))
            {
                header3Found = true;
            }
        }
        Assert.assertFalse("HttpHeader1 found, shouldn't have", header1Found);
        Assert.assertTrue("HttpHeader2 not found", header2Found);
        Assert.assertTrue("HttpHeader3 not found", header3Found);
    }

    @Test
    public void testAddRemoveHeaders() throws Exception
    {
        final String nonHeaderName = "Author";
        final String stringUri = "https://localhost:8443/agility/login.html";
        final URI uri = new URI(stringUri);
        final String headerName = "Authorization";
        final String headerValue = "Basic YWRtaW46TTNzaEBkbWluIQ==";
        final IHttpHeader header1 = HttpClientFactory.getInstance().createHeader(headerName, headerValue);
        final IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, uri);

        // Test basic add
        request.addHeader(header1);
        List<IHttpHeader> returnedHeaders = request.getHeaders();
        Assert.assertEquals(1, returnedHeaders.size());
        Assert.assertEquals(headerName, returnedHeaders.get(0).getName());
        Assert.assertEquals(headerValue, returnedHeaders.get(0).getValue());
        IHttpHeader matchingHeader = request.getHeader(headerName);
        Assert.assertNotNull(matchingHeader);
        List<IHttpHeader> matchingHeaders = request.getHeaders(headerName);
        Assert.assertNotNull(matchingHeaders);
        Assert.assertEquals(1, matchingHeaders.size());
        Assert.assertEquals(headerName, matchingHeaders.get(0).getName());
        Assert.assertEquals(headerValue, matchingHeaders.get(0).getValue());
        IHttpHeader nullHeader = request.getHeader(nonHeaderName);
        Assert.assertNull(nullHeader);
        List<IHttpHeader> nullHeaders = request.getHeaders(nonHeaderName);
        Assert.assertNotNull(nullHeaders);
        Assert.assertEquals(0, nullHeaders.size());

        // Test remove non-existent header
        IHttpHeader removedHeader = request.removeHeader(nonHeaderName);
        Assert.assertNull(removedHeader);

        List<IHttpHeader> removedHeaders = request.removeHeaders(nonHeaderName);
        Assert.assertNotNull(removedHeaders);
        Assert.assertEquals(0, removedHeaders.size());

        // Remove existing header
        removedHeader = request.removeHeader(headerName.toLowerCase());
        Assert.assertNotNull(removedHeader);
        Assert.assertEquals(headerName, removedHeader.getName());
        Assert.assertEquals(headerValue, removedHeader.getValue());
    }

    @Test
    public void testAddRemoveMultiHeaders() throws Exception
    {
        final String stringUri = "https://localhost:8443/agility/login.html";
        final URI uri = new URI(stringUri);
        final String headerName = "Accept";
        final String headerValue1 = "text/html";
        final String headerValue2 = "text/json";
        final String headerValue3 = "text/plain";
        final IHttpHeader header1 = HttpClientFactory.getInstance().createHeader(headerName, headerValue1);
        final IHttpHeader header2 = HttpClientFactory.getInstance().createHeader(headerName, headerValue2);
        final IHttpHeader header3 = HttpClientFactory.getInstance().createHeader(headerName, headerValue3);
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, uri);

        request.addHeader(header1);
        request.addHeader(header2);
        IHttpHeader returnedHeader = request.getHeader(headerName);
        Assert.assertNotNull(returnedHeader);
        Assert.assertEquals(headerName, returnedHeader.getName());
        Assert.assertEquals(headerValue1, returnedHeader.getValue());
        List<IHttpHeader> returnedHeaders = request.getHeaders(headerName);
        Assert.assertNotNull(returnedHeaders);
        Assert.assertEquals(2, returnedHeaders.size());
        Assert.assertEquals(headerName, returnedHeaders.get(0).getName());
        Assert.assertEquals(headerValue1, returnedHeaders.get(0).getValue());
        Assert.assertEquals(headerName, returnedHeaders.get(1).getName());
        Assert.assertEquals(headerValue2, returnedHeaders.get(1).getValue());

        // Test remove first header with matching name
        IHttpHeader removedHeader = request.removeHeader(headerName);
        Assert.assertNotNull(removedHeader);
        Assert.assertEquals(headerName, removedHeader.getName());
        Assert.assertEquals(headerValue1, removedHeader.getValue());
        returnedHeader = request.getHeader(headerName);
        Assert.assertNotNull(returnedHeader);
        Assert.assertEquals(headerName, returnedHeader.getName());
        Assert.assertEquals(headerValue2, returnedHeader.getValue());
        returnedHeaders = request.getHeaders(headerName);
        Assert.assertNotNull(returnedHeaders);
        Assert.assertEquals(1, returnedHeaders.size());
        Assert.assertEquals(headerName, returnedHeaders.get(0).getName());
        Assert.assertEquals(headerValue2, returnedHeaders.get(0).getValue());

        // Test remove all headers with matching name
        request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, uri);
        request.addHeader(header1);
        request.addHeader(header2);
        returnedHeaders = request.getHeaders(headerName);
        Assert.assertNotNull(returnedHeaders);
        Assert.assertEquals(2, returnedHeaders.size());
        Assert.assertEquals(headerName, returnedHeaders.get(0).getName());
        Assert.assertEquals(headerValue1, returnedHeaders.get(0).getValue());
        Assert.assertEquals(headerName, returnedHeaders.get(1).getName());
        Assert.assertEquals(headerValue2, returnedHeaders.get(1).getValue());
        List<IHttpHeader> removedHeaders = request.removeHeaders(headerName);
        Assert.assertNotNull(removedHeaders);
        Assert.assertEquals(2, removedHeaders.size());
        Assert.assertEquals(headerName, removedHeaders.get(0).getName());
        Assert.assertEquals(headerValue1, removedHeaders.get(0).getValue());
        Assert.assertEquals(headerName, removedHeaders.get(1).getName());
        Assert.assertEquals(headerValue2, removedHeaders.get(1).getValue());
        returnedHeaders = request.getHeaders(headerName);
        Assert.assertNotNull(returnedHeaders);
        Assert.assertEquals(0, returnedHeaders.size());

        // Test set header after multiple adds
        request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, uri);
        request.addHeader(header1);
        request.addHeader(header2);
        returnedHeaders = request.getHeaders(headerName);
        Assert.assertNotNull(returnedHeaders);
        Assert.assertEquals(2, returnedHeaders.size());
        Assert.assertEquals(headerName, returnedHeaders.get(0).getName());
        Assert.assertEquals(headerValue1, returnedHeaders.get(0).getValue());
        Assert.assertEquals(headerName, returnedHeaders.get(1).getName());
        Assert.assertEquals(headerValue2, returnedHeaders.get(1).getValue());
        request.setHeader(header3);
        returnedHeader = request.getHeader(headerName);
        Assert.assertNotNull(returnedHeader);
        Assert.assertEquals(headerName, returnedHeader.getName());
        Assert.assertEquals(headerValue3, returnedHeader.getValue());
        returnedHeaders = request.getHeaders(headerName);
        Assert.assertNotNull(returnedHeaders);
        Assert.assertEquals(1, returnedHeaders.size());
        Assert.assertEquals(headerName, returnedHeaders.get(0).getName());
        Assert.assertEquals(headerValue3, returnedHeaders.get(0).getValue());
    }

    @Test
    public void testContent() throws Exception
    {
        String stringContent = "String content";
        String byteContent = "Byte content";
        byte[] byteContentArray = byteContent.getBytes();
        String streamContent = "Stream content";
        String stringUri = "https://localhost:8443/agility/login.html";
        URI uri = new URI(stringUri);
        IHttpRequest request = new DefaultHttpRequest(HttpMethod.GET, uri);
        Assert.assertNull(request.getContent());
        Assert.assertNull(request.getContentAsByteArray());
        Assert.assertNull(request.getContentAsStream());
        Assert.assertEquals(0, request.getContentLength());

        request.setContent(stringContent);
        Assert.assertEquals(stringContent, request.getContent());
        Assert.assertTrue(Arrays.equals(stringContent.getBytes(), request.getContentAsByteArray()));
        Assert.assertEquals(stringContent, convertStreamToString(request.getContentAsStream()));
        Assert.assertEquals(stringContent.length(), request.getContentLength());

        request.setContent(byteContentArray);
        Assert.assertEquals(byteContent, request.getContent());
        Assert.assertFalse(byteContentArray == request.getContentAsByteArray());
        Assert.assertTrue(Arrays.equals(byteContentArray, request.getContentAsByteArray()));
        Assert.assertEquals(byteContent, convertStreamToString(request.getContentAsStream()));
        Assert.assertEquals(byteContentArray.length, request.getContentLength());

        request.setContent(new ByteArrayInputStream(streamContent.getBytes()));
        Assert.assertEquals(streamContent, request.getContent());
        Assert.assertNull(request.getContent()); // Can't do this twice
        Assert.assertNull(request.getContentAsByteArray());
        Assert.assertNull(request.getContentAsStream());
        Assert.assertEquals(-1, request.getContentLength());

        request.setContent(new ByteArrayInputStream(streamContent.getBytes()), streamContent.length());
        Assert.assertTrue(Arrays.equals(streamContent.getBytes(), request.getContentAsByteArray()));
        Assert.assertNull(request.getContentAsByteArray()); // Can't do this twice
        Assert.assertNull(request.getContent());
        Assert.assertNull(request.getContentAsStream());
        Assert.assertEquals(streamContent.length(), request.getContentLength());

        InputStream inputStream = new ByteArrayInputStream(streamContent.getBytes());
        request.setContent(inputStream);
        Assert.assertEquals(inputStream, request.getContentAsStream());
        Assert.assertEquals(-1, request.getContentLength());
    }

    private String convertStreamToString(InputStream stream) throws Exception
    {
        String rv = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] bytesIn = new byte[8192];
        int bytesRead = 0;

        try
        {
            while ((bytesRead = stream.read(bytesIn, 0, 8192)) != -1)
            {
                out.write(bytesIn, 0, bytesRead);
            }

            rv = new String(out.toByteArray());
        }
        finally
        {
            out.close();
        }

        return rv;
    }
}
