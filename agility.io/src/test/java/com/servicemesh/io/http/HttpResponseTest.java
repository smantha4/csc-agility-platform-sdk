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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.servicemesh.io.http.impl.BaseHttpHeader;
import com.servicemesh.io.http.impl.DefaultHttpResponse;

public class HttpResponseTest
{
    @Test
    public void testHttpResponse() throws Exception
    {
        String value = "Success";
        HttpStatus status = new HttpStatus(HttpVersion.HTTP_1_1, 200, "");
        IHttpResponse response = new DefaultHttpResponse();

        Assert.assertNull(response.getStatus());
        Assert.assertEquals(-1, response.getStatusCode());
        Assert.assertNull(response.getContent());
        Assert.assertEquals(0, response.getHeaders().size());
        ((DefaultHttpResponse) response).setStatus(status);
        ((DefaultHttpResponse) response).setContent(value.getBytes());
        Assert.assertNotNull(response.getStatus());
        Assert.assertEquals(HttpVersion.HTTP_1_1, response.getStatus().getHttpVersion());
        Assert.assertEquals(200, response.getStatus().getStatusCode());
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertNotNull(response.getContent());
        Assert.assertEquals(value, response.getContent());
        Assert.assertEquals(0, response.getHeaders().size());
    }

    @Test
    public void testResponseHeaders() throws Exception
    {
        String headerName = "Accept";
        String headerValue = "text/html";
        IHttpHeader header1 = new BaseHttpHeader(headerName, headerValue);
        IHttpResponse response = new DefaultHttpResponse();
        List<IHttpHeader> responseHeaders = response.getHeaders();
        Assert.assertEquals(0, responseHeaders.size());

        ((DefaultHttpResponse) response).addHeader(header1);
        responseHeaders = response.getHeaders();
        Assert.assertEquals(1, responseHeaders.size());
        Assert.assertEquals(headerName, responseHeaders.get(0).getName());
        Assert.assertEquals(headerValue, responseHeaders.get(0).getValue());

        String headerName2 = headerName;
        String headerValue2 = "text/plain";
        IHttpHeader header2 = new BaseHttpHeader(headerName2, headerValue2);
        ((DefaultHttpResponse) response).addHeader(header2);
        responseHeaders = response.getHeaders();
        Assert.assertEquals(2, responseHeaders.size());
        Assert.assertEquals(headerName, responseHeaders.get(0).getName());
        Assert.assertEquals(headerValue, responseHeaders.get(0).getValue());
        Assert.assertEquals(headerName, responseHeaders.get(1).getName());
        Assert.assertEquals(headerValue2, responseHeaders.get(1).getValue());

        String headerName3 = "Connection";
        String headerValue3 = "keep-alive";
        IHttpHeader header3 = new BaseHttpHeader(headerName3, headerValue3);
        ((DefaultHttpResponse) response).addHeader(header3);
        responseHeaders = response.getHeaders();
        Assert.assertEquals(3, responseHeaders.size());

        int header1Found = 0;
        int header2Found = 0;
        int header3Found = 0;

        for (IHttpHeader nextHeader : responseHeaders)
        {
            if (headerName.equals(nextHeader.getName()) && headerValue.equals(nextHeader.getValue()))
            {
                ++header1Found;
            }
            else if (headerName2.equals(nextHeader.getName()) && headerValue2.equals(nextHeader.getValue()))
            {
                ++header2Found;
            }
            else if (headerName3.equals(nextHeader.getName()) && headerValue3.equals(nextHeader.getValue()))
            {
                ++header3Found;
            }
        }
        Assert.assertEquals("HttpHeader1 not found", 1, header1Found);
        Assert.assertEquals("HttpHeader2 not found", 1, header2Found);
        Assert.assertEquals("HttpHeader3 not found", 1, header3Found);

        // Get multiple headers with same header name
        responseHeaders = response.getHeaders(headerName);
        Assert.assertEquals(2, responseHeaders.size());
        Assert.assertEquals(headerName, responseHeaders.get(0).getName());
        Assert.assertEquals(headerValue, responseHeaders.get(0).getValue());
        Assert.assertEquals(headerName, responseHeaders.get(1).getName());
        Assert.assertEquals(headerValue2, responseHeaders.get(1).getValue());
    }

    @Test
    public void testResponseMultiValueHeaders() throws Exception
    {
        String headerName = "Accept";
        String headerValue1 = "text/html";
        String headerValue2 = "text/plain";
        String returnedHeader1Value = "text/html,text/plain";
        String multiHeader1 = "text/html,,text/json";
        List<String> multiHeaderValues1 = new ArrayList<String>();

        multiHeaderValues1.add(headerValue1);
        multiHeaderValues1.add(headerValue2);

        IHttpHeader header1 = new BaseHttpHeader(headerName, multiHeaderValues1);
        IHttpResponse response = new DefaultHttpResponse();
        List<IHttpHeader> responseHeaders = response.getHeaders();
        Assert.assertEquals(0, responseHeaders.size());

        ((DefaultHttpResponse) response).addHeader(header1);
        responseHeaders = response.getHeaders();
        Assert.assertEquals(1, responseHeaders.size());
        IHttpHeader responseHeader = responseHeaders.get(0);
        Assert.assertEquals(headerName, responseHeader.getName());
        Assert.assertEquals(returnedHeader1Value, responseHeader.getValue());
        Assert.assertEquals(2, responseHeader.getValues().size());
        Assert.assertEquals(headerValue1, responseHeader.getValues().get(0));
        Assert.assertEquals(headerValue2, responseHeader.getValues().get(1));

        IHttpHeader header2 = new BaseHttpHeader(headerName, multiHeader1);
        IHttpResponse response2 = new DefaultHttpResponse();
        responseHeaders = response2.getHeaders();
        Assert.assertEquals(0, responseHeaders.size());
        ((DefaultHttpResponse) response2).addHeader(header2);
        responseHeaders = response2.getHeaders();
        Assert.assertEquals(1, responseHeaders.size());
        responseHeader = responseHeaders.get(0);
        Assert.assertEquals(headerName, responseHeader.getName());
        Assert.assertEquals(multiHeader1, responseHeader.getValue());
        Assert.assertEquals(3, responseHeader.getValues().size());
        Assert.assertEquals("text/html", responseHeader.getValues().get(0));
        Assert.assertEquals("", responseHeader.getValues().get(1));
        Assert.assertEquals("text/json", responseHeader.getValues().get(2));
    }

    @Test
    public void testContent() throws Exception
    {
        DefaultHttpResponse responseImpl = new DefaultHttpResponse();
        IHttpResponse response = responseImpl;
        String content = new String("String content");

        Assert.assertNull(response.getContent());
        Assert.assertNull(response.getContentAsByteArray());
        Assert.assertNull(response.getContentAsStream());
        Assert.assertEquals(0, response.getContentLength());

        responseImpl.setContent(content.getBytes());
        Assert.assertEquals(content, response.getContent());
        Assert.assertTrue(Arrays.equals(content.getBytes(), response.getContentAsByteArray()));
        Assert.assertEquals(content, convertStreamToString(response.getContentAsStream()));
        Assert.assertEquals(content.length(), response.getContentLength());
    }

    @Test
    public void testClose()
    {
        DefaultHttpResponse responseImpl = new DefaultHttpResponse();
        IHttpResponse response = responseImpl;
        String content = new String("String content");

        Assert.assertNull(response.getContent());
        Assert.assertNull(response.getContentAsByteArray());
        Assert.assertNull(response.getContentAsStream());
        Assert.assertEquals(0, response.getContentLength());

        responseImpl.setContent(content.getBytes());
        Assert.assertTrue(Arrays.equals(content.getBytes(), response.getContentAsByteArray()));
        Assert.assertEquals(content.length(), response.getContentLength());

        response.close();
        Assert.assertNull(response.getContent());
        Assert.assertNull(response.getContentAsByteArray());
        Assert.assertNull(response.getContentAsStream());
        Assert.assertEquals(0, response.getContentLength());
    }

    @Test
    public void testFinalize()
    {
        DefaultHttpResponse responseImpl = new DefaultHttpResponse();
        IHttpResponse response = responseImpl;
        String content = new String("String content");

        Assert.assertNull(response.getContent());
        Assert.assertNull(response.getContentAsByteArray());
        Assert.assertNull(response.getContentAsStream());
        Assert.assertEquals(0, response.getContentLength());

        responseImpl.setContent(content.getBytes());
        Assert.assertTrue(Arrays.equals(content.getBytes(), response.getContentAsByteArray()));
        Assert.assertEquals(content.length(), response.getContentLength());

        responseImpl.finalize();
        Assert.assertNull(response.getContent());
        Assert.assertNull(response.getContentAsByteArray());
        Assert.assertNull(response.getContentAsStream());
        Assert.assertEquals(0, response.getContentLength());
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
