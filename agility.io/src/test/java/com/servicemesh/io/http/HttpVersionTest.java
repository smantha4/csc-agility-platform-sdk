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

import org.junit.Assert;
import org.junit.Test;

public class HttpVersionTest
{
    private final String httpProtocol = "HTTP";

    @Test
    public void testHttpVersionMajorMinorVersions() throws Exception
    {
        Assert.assertEquals(HttpVersion.HTTP_1_0, HttpVersion.find(httpProtocol,1,0));
        Assert.assertEquals(HttpVersion.HTTP_1_1, HttpVersion.find(httpProtocol,1,1));

    }

    @Test
    public void testHTTP1_0isDefault() throws Exception
    {
        Assert.assertEquals(HttpVersion.HTTP_1_0, HttpVersion.getDefault());
    }

    @Test
    public void testHostHeaderIsNotRequiredForHttp1_0() throws Exception
    {
        Assert.assertFalse(HttpVersion.isHostHeaderRequired("HTTP/1.0"));
        Assert.assertTrue(HttpVersion.isHostHeaderRequired("HTTP/1.1"));
    }

    @Test
    public void testParseHttpVersionValue() throws Exception
    {
        Assert.assertEquals(HttpVersion.HTTP_1_0, HttpVersion.parseHttpVersionValue("HTTP/1.0"));
        Assert.assertEquals(HttpVersion.HTTP_1_1, HttpVersion.parseHttpVersionValue("HTTP/1.1"));
    }
}
