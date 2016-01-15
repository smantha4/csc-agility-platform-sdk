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

public class HttpStatusTest
{
    @Test
    public void testHttpStatus() throws Exception
    {
        String reason = "Good to go";
        HttpStatus status = new HttpStatus(HttpVersion.HTTP_1_0, 200, reason);
        Assert.assertEquals(HttpVersion.HTTP_1_0, status.getHttpVersion());
        Assert.assertEquals(200, status.getStatusCode());
        Assert.assertEquals(reason, status.getReason());

        try
        {
            new HttpStatus(null, 200, reason);
            Assert.fail("Constructed with missing HttpVersion");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Missing version", ex.getMessage());
        }

        try
        {
            new HttpStatus(HttpVersion.HTTP_1_0, -1, reason);
            Assert.fail("Constructed with invalid status code");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Invalid status code", ex.getMessage());
        }
    }
}
