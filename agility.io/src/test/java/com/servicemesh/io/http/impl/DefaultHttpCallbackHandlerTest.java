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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;

import com.servicemesh.io.http.HttpVersion;
import com.servicemesh.io.http.IHttpCallback;
import com.servicemesh.io.http.IHttpResponse;

public class DefaultHttpCallbackHandlerTest
{
    @Test
    public void testCompleted() throws Exception
    {
        HttpResponse response = mock(HttpResponse.class, Answers.RETURNS_DEEP_STUBS.get());
        String httpResponseText = "<httpresponse>response</httpresponse>";
        String header1Name = "Connection";
        String header1Value = "keep-alive";
        Header header1 = new BasicHeader(header1Name, header1Value);
        Header[] headers = new Header[] { header1 };
        ProtocolVersion protocolVersion = new ProtocolVersion(HttpVersion.HTTP_1_1.getProtocol(), 1, 1);
        StatusLine statusLine = new BasicStatusLine(protocolVersion, 200, "success");
        BasicHttpEntity httpEntity = new BasicHttpEntity();

        httpEntity.setContent(new ByteArrayInputStream(httpResponseText.getBytes()));
        when(response.getAllHeaders()).thenReturn(headers);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(httpEntity);

        IHttpCallback<IHttpResponse> callback = new DefaultHttpCallback();
        DefaultHttpResponseFuture<IHttpResponse> future = new DefaultHttpResponseFuture<IHttpResponse>(callback);
        DefaultHttpCallbackHandler<IHttpResponse> callbackHandler = new DefaultHttpCallbackHandler<IHttpResponse>(future);

        callbackHandler.completed(response);
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        IHttpResponse responseBack = future.get();
        Assert.assertNotNull(responseBack);
        Assert.assertEquals(200, responseBack.getStatusCode());
        Assert.assertEquals("success", responseBack.getStatus().getReason());
        Assert.assertEquals(httpResponseText, responseBack.getContent());

        try
        {
            new DefaultHttpCallbackHandler<IHttpResponse>(null);
            Assert.fail("Created handler with null callback");
        }
        catch (NullPointerException ex)
        {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Missing callback", ex.getMessage());
        }
    }

    @Test
    public void testCancelled() throws Exception
    {
        HttpResponse response = mock(HttpResponse.class, Answers.RETURNS_DEEP_STUBS.get());
        String httpResponseText = "<httpresponse>response</httpresponse>";
        String header1Name = "Connection";
        String header1Value = "keep-alive";
        Header header1 = new BasicHeader(header1Name, header1Value);
        Header[] headers = new Header[] { header1 };
        ProtocolVersion protocolVersion = new ProtocolVersion(HttpVersion.HTTP_1_1.getProtocol(), 1, 1);
        StatusLine statusLine = new BasicStatusLine(protocolVersion, 200, "success");
        BasicHttpEntity httpEntity = new BasicHttpEntity();

        httpEntity.setContent(new ByteArrayInputStream(httpResponseText.getBytes()));
        when(response.getAllHeaders()).thenReturn(headers);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(httpEntity);

        IHttpCallback<IHttpResponse> callback = new DefaultHttpCallback();
        DefaultHttpResponseFuture<IHttpResponse> future = new DefaultHttpResponseFuture<IHttpResponse>(callback);
        DefaultHttpCallbackHandler<IHttpResponse> callbackHandler = new DefaultHttpCallbackHandler<IHttpResponse>(future);

        callbackHandler.cancelled();
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());

        try
        {
            future.get();
            Assert.fail("Cancelled callback handler returned future result");
        }
        catch (CancellationException ex)
        {
            // All is good
        }
    }

    @Test
    public void testFailed() throws Exception
    {
        HttpResponse response = mock(HttpResponse.class, Answers.RETURNS_DEEP_STUBS.get());
        String httpResponseText = "<httpresponse>response</httpresponse>";
        String header1Name = "Connection";
        String header1Value = "keep-alive";
        Header header1 = new BasicHeader(header1Name, header1Value);
        Header[] headers = new Header[] { header1 };
        ProtocolVersion protocolVersion = new ProtocolVersion(HttpVersion.HTTP_1_1.getProtocol(), 1, 1);
        StatusLine statusLine = new BasicStatusLine(protocolVersion, 200, "success");
        BasicHttpEntity httpEntity = new BasicHttpEntity();

        httpEntity.setContent(new ByteArrayInputStream(httpResponseText.getBytes()));
        when(response.getAllHeaders()).thenReturn(headers);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(httpEntity);

        IHttpCallback<IHttpResponse> callback = new DefaultHttpCallback();
        DefaultHttpResponseFuture<IHttpResponse> future = new DefaultHttpResponseFuture<IHttpResponse>(callback);
        DefaultHttpCallbackHandler<IHttpResponse> callbackHandler = new DefaultHttpCallbackHandler<IHttpResponse>(future);

        callbackHandler.failed(new IllegalArgumentException("test"));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());

        try
        {
            future.get();
            Assert.fail("Cancelled callback handler returned future result");
        }
        catch (ExecutionException ex)
        {
            Assert.assertTrue(ex.getCause() instanceof IllegalArgumentException);
            // All is good
        }
    }
}
