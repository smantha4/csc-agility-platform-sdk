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

package com.servicemesh.io.http.impl;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.servicemesh.io.http.IHttpResponse;

public class DefaultServerBusyRetryStrategyTest
{
    @Test
    public void testDefaults()
    {
        IHttpResponse response = mock(IHttpResponse.class);
        DefaultServerBusyRetryStrategy strategy = new DefaultServerBusyRetryStrategy();

        when(response.getStatusCode()).thenReturn(503);
        Assert.assertEquals(DefaultServerBusyRetryStrategy.DEFAULT_BUSY_RETRY_INTERVAL, strategy.retryInterval());
        Assert.assertTrue(strategy.retry(response));
        Assert.assertTrue(strategy.retry(response));
        Assert.assertTrue(strategy.retry(response));
        Assert.assertFalse(strategy.retry(response));
    }
}
