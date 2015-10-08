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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class HttpHeaderTest
{
    @Test
    public void testHttpRequestHeader()
        throws Exception
    {
        String headerName = "Accept";
        String headerValue = "text/html";

        IHttpHeader header = HttpClientFactory.getInstance().createHeader(headerName, headerValue);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals(headerValue, header.getValue());
        Assert.assertEquals(headerName + ": " + headerValue, header.toString());

        List<String> values = header.getValues();
        Assert.assertNotNull(values);
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(headerValue, values.get(0));

        try {
            values.add("text/plain");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }

        header = HttpClientFactory.getInstance().createHeader(headerName, (String)null);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals("", header.getValue());
        Assert.assertEquals(headerName + ": ", header.toString());
        values = header.getValues();
        Assert.assertNotNull(values);
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("", values.get(0));

        try {
            values.add("text/plain");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }
    }

    @Test
    public void testHttpRequestHeaderNegative()
        throws Exception
    {
        String headerValue = "text/html";

        try {
            HttpClientFactory.getInstance().createHeader(null, headerValue);
            Assert.fail("HttpHeader constructed with invalid name");
        } catch (IllegalArgumentException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertTrue(ex.getMessage().contains("Invalid name parameter"));
        }

        try {
            HttpClientFactory.getInstance().createHeader("", headerValue);
            Assert.fail("HttpHeader constructed with invalid name");
        } catch (IllegalArgumentException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertTrue(ex.getMessage().contains("Invalid name parameter"));
        }
    }

    @Test
    public void testHttpRequestHeaderMultiValue()
        throws Exception
    {
        String headerName = "Accept";
        String headerValue1 = "text/html";
        String headerValue2 = "text/plain";
        String headerValue3 = null;
        String multiHeader1 = "text/html,,text/plain";
        String multiHeader2 = "text/html,";
        String multiHeader3 = ",text/plain";
        String multiHeader4 = "text/json,";
        List<String> headerValues = new ArrayList<String>();
        List<String> multiHeaderValues1 = new ArrayList<String>();
        List<String> multiHeaderValues2 = new ArrayList<String>();

        headerValues.add(headerValue1);
        headerValues.add(headerValue2);
        headerValues.add(headerValue3);

        multiHeaderValues1.add(headerValue1);
        multiHeaderValues1.add(multiHeader3);

        multiHeaderValues2.add(headerValue1);
        multiHeaderValues2.add(multiHeader4);

        IHttpHeader header = HttpClientFactory.getInstance().createHeader(headerName, multiHeader1);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals(multiHeader1, header.getValue());
        Assert.assertEquals(3, header.getValues().size());
        Assert.assertEquals("text/html", header.getValues().get(0));
        Assert.assertEquals("", header.getValues().get(1));
        Assert.assertEquals("text/plain", header.getValues().get(2));

        try {
            header.getValues().add("gzip");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }

        header = HttpClientFactory.getInstance().createHeader(headerName, multiHeader2);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals(multiHeader2, header.getValue());
        Assert.assertEquals(2, header.getValues().size());
        Assert.assertEquals("text/html", header.getValues().get(0));
        Assert.assertEquals("", header.getValues().get(1));

        try {
            header.getValues().add("gzip");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }

        header = HttpClientFactory.getInstance().createHeader(headerName, multiHeader3);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals(multiHeader3, header.getValue());
        Assert.assertEquals(2, header.getValues().size());
        Assert.assertEquals("", header.getValues().get(0));
        Assert.assertEquals("text/plain", header.getValues().get(1));

        try {
            header.getValues().add("gzip");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }

        header = HttpClientFactory.getInstance().createHeader(headerName, headerValues);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals("text/html,text/plain,", header.getValue());
        Assert.assertEquals(3, header.getValues().size());
        Assert.assertEquals("text/html", header.getValues().get(0));
        Assert.assertEquals("text/plain", header.getValues().get(1));
        Assert.assertEquals("", header.getValues().get(2));

        try {
            header.getValues().add("gzip");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }

        header = HttpClientFactory.getInstance().createHeader(headerName, multiHeaderValues1);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals("text/html,,text/plain", header.getValue());
        Assert.assertEquals(3, header.getValues().size());
        Assert.assertEquals("text/html", header.getValues().get(0));
        Assert.assertEquals("", header.getValues().get(1));
        Assert.assertEquals("text/plain", header.getValues().get(2));

        try {
            header.getValues().add("gzip");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }

        header = HttpClientFactory.getInstance().createHeader(headerName, multiHeaderValues2);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals("text/html,text/json,", header.getValue());
        Assert.assertEquals(3, header.getValues().size());
        Assert.assertEquals("text/html", header.getValues().get(0));
        Assert.assertEquals("text/json", header.getValues().get(1));
        Assert.assertEquals("", header.getValues().get(2));

        try {
            header.getValues().add("gzip");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }

        header = HttpClientFactory.getInstance().createHeader(headerName, (List<String>)null);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals("", header.getValue());
        Assert.assertEquals(0, header.getValues().size());

        try {
            header.getValues().add("gzip");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }

        header = HttpClientFactory.getInstance().createHeader(headerName, new ArrayList<String>());
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals("", header.getValue());
        Assert.assertEquals(0, header.getValues().size());

        try {
            header.getValues().add("gzip");
            Assert.fail("Modified values list");
        } catch (UnsupportedOperationException ex) {
            // All is good
        }

        // Test with duplicate value header
        final String duplicateValueHeader = "text/html,text/html";

        header = HttpClientFactory.getInstance().createHeader(headerName, duplicateValueHeader);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals(duplicateValueHeader, header.getValue());
        Assert.assertEquals(2, header.getValues().size());
        Assert.assertEquals("text/html", header.getValues().get(0));
        Assert.assertEquals("text/html", header.getValues().get(1));

        // Test duplicate values in list
        final List<String> headerValues1 = new ArrayList<String>();

        headerValues1.add(headerValue1);
        headerValues1.add(headerValue1);
        header = HttpClientFactory.getInstance().createHeader(headerName, headerValues1);
        Assert.assertEquals(headerName, header.getName());
        Assert.assertEquals(duplicateValueHeader, header.getValue());
        Assert.assertEquals(2, header.getValues().size());
        Assert.assertEquals("text/html", header.getValues().get(0));
        Assert.assertEquals("text/html", header.getValues().get(1));
    }

    @Test
    public void testHttpRequstHeaderMultiValueNegative()
        throws Exception
    {
        String headerValue1 = "text/html";
        String headerValue2 = "text/plain";
        List<String> headerValues = new ArrayList<String>();

        // Valid values list
        headerValues.add(headerValue1);
        headerValues.add(headerValue2);

        try {
            HttpClientFactory.getInstance().createHeader(null, headerValues);
            Assert.fail("HttpHeader constructed with invalid name");
        } catch (IllegalArgumentException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertTrue(ex.getMessage().contains("Invalid name parameter"));
        }

        try {
            HttpClientFactory.getInstance().createHeader("", headerValues);
            Assert.fail("HttpHeader constructed with invalid name");
        } catch (IllegalArgumentException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertTrue(ex.getMessage().contains("Invalid name parameter"));
        }
    }
}
