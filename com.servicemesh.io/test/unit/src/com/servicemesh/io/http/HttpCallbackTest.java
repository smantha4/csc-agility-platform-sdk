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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.servicemesh.io.http.impl.DefaultHttpCallback;
import com.servicemesh.io.http.impl.DefaultHttpResponse;
import com.servicemesh.io.http.impl.DefaultHttpResponseFuture;

public class HttpCallbackTest
{
    @Test
    @Ignore
    public void testHttpCallback()
        throws Exception
    {
        IHttpCallback<IHttpResponse> callback = new DefaultHttpCallback();
        IHttpResponseFuture<IHttpResponse> future = new DefaultHttpResponseFuture<IHttpResponse>(callback);
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());

        callback.onCancel();
        Assert.assertTrue(future.isCancelled());
        Assert.assertFalse(future.isDone());

        // Test success
        IHttpResponse testResponse = new DefaultHttpResponse();
        HttpStatus testStatus = new HttpStatus(HttpVersion.HTTP_1_1, 200, null);
        ((DefaultHttpResponse)testResponse).setStatus(testStatus);
        ((DefaultHttpResponse)testResponse).setContent("success".getBytes());

        callback = new DefaultHttpCallback();
        future = new DefaultHttpResponseFuture<IHttpResponse>(callback);
        callback.onCompletion(testResponse);
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        IHttpResponse responseBack = future.get();
        Assert.assertNotNull(responseBack);
        Assert.assertEquals(200, responseBack.getStatusCode());
        Assert.assertEquals("success", responseBack.getContent());

        // Test success with timeout
        callback = new DefaultHttpCallback();
        future = new DefaultHttpResponseFuture<IHttpResponse>(callback);
        callback.onCompletion(testResponse);
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());

        try {
            responseBack = future.get(100, TimeUnit.MILLISECONDS);
            Assert.assertNotNull(responseBack);
            Assert.assertEquals(200, responseBack.getStatusCode());
            Assert.assertEquals("success", responseBack.getContent());
        } catch (TimeoutException ex) {
            Assert.fail("Timeout fired when retrieving a set future");
        }
    }

    @Test
    @Ignore
    public void testCustomCallback()
        throws Exception
    {
        IHttpCallback<Integer> callback = new CustomHttpCallback();
        IHttpResponseFuture<Integer> future = new DefaultHttpResponseFuture<Integer>(callback);

        callback.onCompletion(Integer.valueOf(10));
        Assert.assertEquals(10, future.get().intValue());
    }

    private static class CustomHttpCallback
        implements IHttpCallback<Integer>
    {
        @Override
        public void onCompletion(final Integer response)
        {
        }

        @Override
        public Integer decoder(final IHttpResponse response)
        {
            return Integer.valueOf(response.getContent());
        }

        @Override
        public void onCancel()
        {
        }

        @Override
        public void onFailure(final Throwable th)
        {
        }
    }
}
