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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.ConnectionClosedException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.servicemesh.core.async.Promise;
import com.servicemesh.io.http.impl.DefaultHttpCallback;
import com.servicemesh.io.http.impl.DefaultHttpClient;
import com.servicemesh.io.http.impl.DefaultHttpResponse;
import com.servicemesh.io.util.JunitWireMockClassRule;

public class HttpClientTest
{
    // This should default to be homed under the bundle home directory: bundles/io
    private final static String JUNIT_KEYSTORE = "/junitkeystore.jks";
    private final static String JUNIT_KEYSTORE_FILENAME = HttpClientTest.class.getResource(JUNIT_KEYSTORE).getFile();

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @ClassRule
    public static JunitWireMockClassRule wireMockRule = new JunitWireMockClassRule(JUNIT_KEYSTORE_FILENAME, true);

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    @Test
    public void testDefaultCallback() throws Exception
    {
        reset();
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");

        verify(1, getRequestedFor(urlEqualTo("/agility/api/current/storeproducttype")).withHeader("Content-Type",
                equalTo("text/xml")));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());

        IHttpCallback<IHttpResponse> callback = new DefaultHttpCallback();
        future = httpClient.execute(request, callback);
        httpResponse = future.get();
        contentTypeHeaderIn = httpResponse.getHeader("Content-Type");

        verify(2, getRequestedFor(urlEqualTo("/agility/api/current/storeproducttype")).withHeader("Content-Type",
                equalTo("text/xml")));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();
    }

    @Test
    public void testPromise() throws Throwable
    {
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Promise<IHttpResponse> promise = httpClient.promise(request);
        IHttpResponse httpResponse = promise.get();
        IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");

        verify(1, getRequestedFor(urlEqualTo("/agility/api/current/storeproducttype")).withHeader("Content-Type",
                equalTo("text/xml")));
        Assert.assertFalse(promise.isCancelled());
        Assert.assertFalse(promise.isFailed());
        Assert.assertTrue(promise.isCompleted());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
    }

    @Test
    public void testPromiseWithException() throws Throwable
    {
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype")).willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Promise<IHttpResponse> promise = httpClient.promise(request);

        try
        {
            promise.get();
            Assert.fail("Succeeded when promise failed");
        }
        catch (ConnectionClosedException ex)
        {
            // Expected
        }
    }

    @Test
    public void testStringPayload() throws Exception
    {
        final String responseBody = "<response>Some content</response>";
        final String stringRequestBody = "<request>request test</request>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        final String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        final IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        final IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        request.setHeader(contentTypeHeaderOut);
        request.setContent(stringRequestBody);
        final IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        final IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        final IHttpCallback<IHttpResponse> callback = new DefaultHttpCallback();
        final Future<IHttpResponse> future = httpClient.execute(request, callback);
        final IHttpResponse httpResponse = future.get();
        final IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");

        verify(1, getRequestedFor(urlEqualTo("/agility/api/current/storeproducttype"))
                .withHeader("Content-Type", equalTo("text/xml")).withRequestBody(equalTo(stringRequestBody)));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();
    }

    @Test
    public void testByteArrayPayload() throws Exception
    {
        // Test byte array payload
        final String responseBody = "<response>Some content</response>";
        final String stringRequestBody = "<byteRequest>request byte array test</byteRequest>";
        final byte[] byteRequestBody = stringRequestBody.getBytes();
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        final String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        final IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        final IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        request.setHeader(contentTypeHeaderOut);
        request.setContent(byteRequestBody);
        final IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        final IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        final IHttpCallback<IHttpResponse> callback = new DefaultHttpCallback();
        final Future<IHttpResponse> future = httpClient.execute(request, callback);
        final IHttpResponse httpResponse = future.get();
        final IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");

        verify(1, getRequestedFor(urlEqualTo("/agility/api/current/storeproducttype"))
                .withHeader("Content-Type", equalTo("text/xml")).withRequestBody(equalTo(stringRequestBody)));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();
    }

    @Test
    public void testInputStreamPayload() throws Exception
    {
        // Test byte array payload
        final String responseBody = "<response>Some content</response>";
        final String stringRequestBody = "<fileRequest>request input stream test</fileRequest>";
        final byte[] byteRequestBody = stringRequestBody.getBytes();
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        final File sourceFile = tempFolder.newFile("InputStreamTest.txt");

        try (FileOutputStream fos = new FileOutputStream(sourceFile))
        {
            fos.write(byteRequestBody);
        }
        Assert.assertEquals(byteRequestBody.length, sourceFile.length());

        try (FileInputStream fis = new FileInputStream(sourceFile))
        {
            final String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
            final IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
            final IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
            request.setHeader(contentTypeHeaderOut);
            request.setContent(fis);

            final IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
            final IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
            final IHttpCallback<IHttpResponse> callback = new DefaultHttpCallback();
            final Future<IHttpResponse> future = httpClient.execute(request, callback);
            final IHttpResponse httpResponse = future.get();
            final IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");

            verify(1, getRequestedFor(urlEqualTo("/agility/api/current/storeproducttype"))
                    .withHeader("Content-Type", equalTo("text/xml")).withRequestBody(equalTo(stringRequestBody)));
            Assert.assertFalse(future.isCancelled());
            Assert.assertTrue(future.isDone());
            Assert.assertNotNull(contentTypeHeaderIn);
            Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
            Assert.assertNotNull(httpResponse.getStatus());
            Assert.assertEquals(200, httpResponse.getStatusCode());
            Assert.assertEquals(responseBody, httpResponse.getContent());
            httpClient.close();
        }
    }

    @Test
    public void testCustomCallback() throws Exception
    {
        reset();
        IHttpCallback<IHttpResponse> callback = new CustomHttpCallback();
        String resonseBody = "<response>Some content</response>";
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        stubFor(get(urlEqualTo("/agility/api/current/project"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(resonseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/project";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request, callback);
        IHttpResponse httpResponse = future.get();
        IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");

        verify(1, getRequestedFor(urlEqualTo("/agility/api/current/project")).withHeader("Content-Type", equalTo("text/xml")));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(resonseBody + "<additional>extra</additional>", httpResponse.getContent());
        httpClient.close();

        Set<Integer> set = new HashSet<Integer>();
        IHttpCallback<IHttpResponse> callbackNoFuture = new CustomHttpCallbackNoFuture(set);
        Assert.assertTrue(set.isEmpty());
        stubFor(get(urlEqualTo("/agility/api/current/container"))
                .willReturn(aResponse().withStatus(403).withHeader("Content-Type", "text/xml").withBody(resonseBody)));

        String stringUriNoFuture = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/container";
        IHttpRequest requestNoFuture = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUriNoFuture));
        requestNoFuture.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builderNoFuture = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClientNoFuture = HttpClientFactory.getInstance().getClient(builderNoFuture.build());
        Future<IHttpResponse> futureNoFuture = httpClientNoFuture.execute(requestNoFuture, callbackNoFuture);
        IHttpResponse httpResponseNoFuture = futureNoFuture.get();
        contentTypeHeaderIn = httpResponseNoFuture.getHeader("Content-Type");

        verify(1, getRequestedFor(urlEqualTo("/agility/api/current/container")).withHeader("Content-Type", equalTo("text/xml")));
        Assert.assertFalse(futureNoFuture.isCancelled());
        Assert.assertTrue(futureNoFuture.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponseNoFuture.getStatus());
        Assert.assertEquals(403, httpResponseNoFuture.getStatusCode());
        Assert.assertEquals(resonseBody, httpResponseNoFuture.getContent());
        Assert.assertFalse(set.isEmpty());
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(set.contains(Integer.valueOf(403)));
        httpClientNoFuture.close();
    }

    @Test
    public void testCustomCallbackTyped() throws Exception
    {
        reset();
        IHttpCallback<Integer> callback = new CustomHttpCallbackTyped();
        String resonseBody = "13";
        stubFor(get(urlEqualTo("/agility/api/current/project/count"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(resonseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/project/count";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<Integer> future = httpClient.execute(request, callback);

        Assert.assertEquals(13, future.get().intValue());
        httpClient.close();
    }

    @Test
    public void testCompletionThrowsException() throws Exception
    {
        final IHttpCallback<IHttpResponse> callback = new CustomHttpCallback() {
            @Override
            public IHttpResponse decoder(final IHttpResponse response)
            {
                throw new IllegalArgumentException("Testing completion failure");
            }
        };
        final String resonseBody = "<response>Some content</response>";
        final IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        stubFor(get(urlEqualTo("/agility/api/current/project"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(resonseBody)));

        final String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/project";
        final IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        request.setHeader(contentTypeHeaderOut);
        final IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        final IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        final Future<IHttpResponse> future = httpClient.execute(request, callback);

        try
        {
            future.get(1, TimeUnit.SECONDS);
            Assert.fail("Callback handler successfully completed the Future when it should have failed");
        }
        catch (ExecutionException ex)
        {
            Assert.assertTrue(ex.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testTimeoutException() throws Exception
    {
        reset();
        instanceRule.addRequestProcessingDelay(200);
        String resonseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/template"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(resonseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/template";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);

        try
        {
            future.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("get() succeeded when future has error");
        }
        catch (Exception ex)
        {
            if (ex instanceof TimeoutException)
            {
                Assert.assertNotNull(ex.getMessage());
                Assert.assertEquals("Timeout waiting for task.", ex.getMessage());
            }
            else
            {
                Assert.fail("Incorrect exception received");
            }
        }
    }

    @Test
    public void testSocketTimeout() throws Exception
    {
        reset();
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/template")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody).withFixedDelay(1000)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/template";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setSocketTimeout(100);
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);

        try
        {
            future.get();
            Assert.fail("get() succeeded when future should have timed out");
        }
        catch (Exception ex)
        {
            if (ex instanceof ExecutionException)
            {
                Assert.assertTrue(ex.getCause() instanceof SocketTimeoutException);
            }
            else
            {
                Assert.fail("Wrong exception received");
            }
        }
    }

    @Test
    public void testSocketTimeoutRequest() throws Exception
    {
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/template")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody).withFixedDelay(1000)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/template";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();

        request.setHeader(contentTypeHeaderOut);
        request.setRequestTimeout(100);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);

        try
        {
            future.get();
            Assert.fail("get() succeeded when future should have timed out through request");
        }
        catch (Exception ex)
        {
            if (ex instanceof ExecutionException)
            {
                Assert.assertTrue(ex.getCause() instanceof SocketTimeoutException);
            }
            else
            {
                Assert.fail("Wrong exception received");
            }
        }
    }

    @Test
    public void testNoRetries() throws Exception
    {
        final String responseBody = "<response>Some content</response>";
        final String scenario = "Test retries with zero retries";

        stubFor(get(urlEqualTo("/agility/api/current/template")).inScenario(scenario).whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)).willSetStateTo("Retrying"));

        stubFor(get(urlEqualTo("/agility/api/current/template")).inScenario(scenario).whenScenarioStateIs("Retrying")
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/template";
        final String stringRequestBody = "<request>request test</request>";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        request.setContent(stringRequestBody);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setRetries(0);
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);

        try
        {
            future.get();
            Assert.fail("get() succeeded when future has error");
        }
        catch (Exception ex)
        {
            if (ex instanceof ExecutionException)
            {
                Assert.assertTrue(ex.getCause() instanceof ConnectionClosedException);
            }
            else
            {
                Assert.fail("Wrong exception received");
            }
        }
    }

    @Test
    public void testRetries() throws Exception
    {
        final String responseBody = "<response>Some content</response>";
        final String scenario = "Test retries with non-zero retries";

        stubFor(get(urlEqualTo("/agility/api/current/template")).inScenario(scenario).whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)).willSetStateTo("Retrying"));

        stubFor(get(urlEqualTo("/agility/api/current/template")).inScenario(scenario).whenScenarioStateIs("Retrying")
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/template";
        final String stringRequestBody = "<request>request test</request>";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        request.setContent(stringRequestBody);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setRetries(1);
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse response = future.get();

        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals(responseBody, response.getContent());
        verify(2, getRequestedFor(urlEqualTo("/agility/api/current/template")).withHeader("Content-Type", equalTo("text/xml")));
    }

    @Test
    public void testParameters() throws Exception
    {
        reset();
        String resonseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(resonseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        IHttpCallback<IHttpResponse> callback = new DefaultHttpCallback();

        try
        {
            httpClient.execute((IHttpRequest) null);
            Assert.fail("Executed with null request");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertEquals("Missing request parameter", ex.getMessage());
        }

        try
        {
            httpClient.execute((IHttpRequest) null, callback);
            Assert.fail("Executed with null request");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertEquals("Missing request parameter", ex.getMessage());
        }

        try
        {
            httpClient.execute(request, (IHttpCallback<IHttpResponse>) null);
            Assert.fail("Executed with null callback");
        }
        catch (IllegalArgumentException ex)
        {
            Assert.assertEquals("Missing callback parameter", ex.getMessage());
        }
    }

    @Test
    public void testGetRedirects() throws Exception
    {
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(307).withHeader("Content-Type", "text/plain").withHeader("Location",
                        "https://localhost:" + instanceRule.httpsPort() + "/agility/api/redirect/storeproducttype")));

        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/redirect/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpHeader xMSVersion = HttpClientFactory.getInstance().createHeader("x-ms-version", "2012-08-01");
        request.setHeader(xMSVersion);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        verify(getRequestedFor(urlEqualTo("/agility/api/redirect/storeproducttype")).withHeader("x-ms-version",
                equalTo("2012-08-01")));
        httpClient.close();
    }

    @Test
    public void testHeadRedirects() throws Exception
    {
        stubFor(head(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(307).withHeader("Content-Type", "text/plain").withHeader("Location",
                        "https://localhost:" + instanceRule.httpsPort() + "/agility/api/redirect/storeproducttype")));

        stubFor(head(urlEqualTo("/agility/api/redirect/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml")));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.HEAD, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpHeader xMSVersion = HttpClientFactory.getInstance().createHeader("x-ms-version", "2012-08-01");
        request.setHeader(xMSVersion);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        verify(headRequestedFor(urlEqualTo("/agility/api/redirect/storeproducttype")).withHeader("x-ms-version",
                equalTo("2012-08-01")));
        httpClient.close();
    }

    @Test
    public void testDeleteRedirects() throws Exception
    {
        stubFor(delete(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(307).withHeader("Content-Type", "text/plain").withHeader("Location",
                        "https://localhost:" + instanceRule.httpsPort() + "/agility/api/redirect/storeproducttype")));

        String responseBody = "<response>Some content</response>";
        stubFor(delete(urlEqualTo("/agility/api/redirect/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.DELETE, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpHeader xMSVersion = HttpClientFactory.getInstance().createHeader("x-ms-version", "2012-08-01");
        request.setHeader(xMSVersion);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        verify(deleteRequestedFor(urlEqualTo("/agility/api/redirect/storeproducttype")).withHeader("x-ms-version",
                equalTo("2012-08-01")));
        httpClient.close();
    }

    @Test
    public void testPostRedirects() throws Exception
    {
        String redirectBody = "Temporary redirect";
        final String stringRequestBody = "<request>request test</request>";
        stubFor(post(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(307).withHeader("Content-Type", "text/plain")
                        .withHeader("Location",
                                "https://localhost:" + instanceRule.httpsPort() + "/agility/api/redirect/storeproducttype")
                .withBody(redirectBody)));

        String responseBody = "<response>Some content</response>";
        stubFor(post(urlEqualTo("/agility/api/redirect/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.POST, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        request.setContent(stringRequestBody);

        IHttpHeader xMSVersion = HttpClientFactory.getInstance().createHeader("x-ms-version", "2012-08-01");
        request.setHeader(xMSVersion);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        IHttpHeader contentTypeHeaderIn = httpResponse.getHeader("Content-Type");
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(contentTypeHeaderIn);
        Assert.assertEquals("text/xml", contentTypeHeaderIn.getValue());
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        verify(postRequestedFor(urlEqualTo("/agility/api/redirect/storeproducttype")).withHeader("x-ms-version",
                equalTo("2012-08-01")));
        httpClient.close();
    }

    @Test
    public void testServer429BusyNoRetries() throws Exception
    {
        serverBusyNoRetriesTest(429);
    }

    @Test
    public void testServer429BusyRetriesFail() throws Exception
    {
        serverBusyRetriesFailTest(429);
    }

    @Test
    public void testServer429BusyRetriesSucceed() throws Exception
    {
        serverBusyRetriesSucceedTest(429);
    }

    @Test
    public void testServer503BusyNoRetries() throws Exception
    {
        serverBusyNoRetriesTest(503);
    }

    @Test
    public void testServer503BusyRetriesFail() throws Exception
    {
        serverBusyRetriesFailTest(503);
    }

    @Test
    public void testServer503BusyRetriesSucceed() throws Exception
    {
        serverBusyRetriesSucceedTest(503);
    }

    @Test
    public void testMultiHeaderDuplicateValues() throws Exception
    {
        final String headerName = "Cache-Control";
        final String headerValue = "proxy-revalidate, proxy-revalidate";
        final String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "text/xml").withHeader(headerName, headerValue).withBody(responseBody)));

        final String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        final IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        final IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        final IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        final IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        final Future<IHttpResponse> future = httpClient.execute(request);

        final IHttpResponse response = future.get(1, TimeUnit.SECONDS);
        final IHttpHeader responseHeader = response.getHeader(headerName.toLowerCase());
        Assert.assertNotNull(responseHeader);
        Assert.assertEquals(headerName, responseHeader.getName());
        Assert.assertEquals(headerValue, responseHeader.getValue());
    }

    @Test
    public void testMultiHeaderValues() throws Exception
    {
        final String headerName = "Cache-Control";
        final String headerValue1 = "proxy-revalidate, proxy-revalidate";
        final String headerValue2 = "no-cache";
        final String headerValue3 = "no-store";
        final String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype")).willReturn(
                aResponse().withStatus(200).withHeader(headerName, headerValue1).withHeader("Content-Type", "text/xml")
                        .withHeader(headerName, headerValue2).withHeader(headerName, headerValue3).withBody(responseBody)));

        final String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        final IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        final IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        final IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        final IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        final Future<IHttpResponse> future = httpClient.execute(request);

        final IHttpResponse response = future.get(1, TimeUnit.SECONDS);
        IHttpHeader responseHeader = response.getHeader("Content-Type");
        Assert.assertNotNull(responseHeader);
        Assert.assertEquals("Content-Type", responseHeader.getName());
        Assert.assertEquals("text/xml", responseHeader.getValue());

        responseHeader = response.getHeader(headerName.toLowerCase());
        Assert.assertNotNull(responseHeader);
        Assert.assertEquals(headerName, responseHeader.getName());
        Assert.assertEquals(headerValue1, responseHeader.getValue());

        List<IHttpHeader> responseHeaders = response.getHeaders(headerName.toLowerCase());
        Assert.assertNotNull(responseHeaders);
        Assert.assertEquals(3, responseHeaders.size());
        Assert.assertEquals(headerName, responseHeaders.get(0).getName());
        Assert.assertEquals(headerValue1, responseHeaders.get(0).getValue());
        Assert.assertEquals(headerName, responseHeaders.get(1).getName());
        Assert.assertEquals(headerValue2, responseHeaders.get(1).getValue());
        Assert.assertEquals(headerName, responseHeaders.get(2).getName());
        Assert.assertEquals(headerValue3, responseHeaders.get(2).getValue());
    }

    @Test
    public void testClose() throws Exception
    {
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);

        future.get();
        httpClient.close();

        try
        {
            httpClient.execute(request);
            Assert.fail("Succeeded when expected IllegalStateException");
        }
        catch (IllegalStateException ex)
        {
            // Ignore, expected
        }
    }

    @Test
    public void testFinalize() throws Throwable
    {
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);

        future.get();
        ((DefaultHttpClient) httpClient).finalize();

        try
        {
            httpClient.execute(request);
            Assert.fail("Succeeded when expected IllegalStateException");
        }
        catch (IllegalStateException ex)
        {
            // Ignore, expected
        }
    }

    private void serverBusyNoRetriesTest(final int statusCode) throws Exception
    {
        final String responseBody = "<ErrorMessage>Service Unavailable</ErrorMessage>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(statusCode).withHeader("Content-Type", "text/plain").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setServerBusyRetries(0);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertEquals(statusCode, httpResponse.getStatusCode());
        verify(1, getRequestedFor(urlEqualTo("/agility/api/current/storeproducttype")).withHeader("Content-Type",
                equalTo("text/xml")));
    }

    private void serverBusyRetriesFailTest(final int statusCode) throws Exception
    {
        final String responseBody = "<ErrorMessage>Service Unavailable</ErrorMessage>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(statusCode).withHeader("Content-Type", "text/plain").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setServerBusyRetries(2).setServerBusyRetryInterval(250);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertEquals(statusCode, httpResponse.getStatusCode());
        verify(3, getRequestedFor(urlEqualTo("/agility/api/current/storeproducttype")).withHeader("Content-Type",
                equalTo("text/xml")));
    }

    public void serverBusyRetriesSucceedTest(final int statusCode) throws Exception
    {
        final String errorResponseBody = "<ErrorMessage>Service Unavailable</ErrorMessage>";
        final String responseBody = "<response>Some content</response>";
        final String scenario = "Test non-zero retries";

        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype")).inScenario(scenario)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(
                        aResponse().withStatus(statusCode).withHeader("Content-Type", "text/plain").withBody(errorResponseBody))
                .willSetStateTo("Retrying"));

        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype")).inScenario(scenario).whenScenarioStateIs("Retrying")
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setServerBusyRetries(2).setServerBusyRetryInterval(250);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertEquals(200, httpResponse.getStatusCode());
        verify(2, getRequestedFor(urlEqualTo("/agility/api/current/storeproducttype")).withHeader("Content-Type",
                equalTo("text/xml")));
    }

    private static class CustomHttpCallback extends DefaultHttpCallback
    {
        public CustomHttpCallback()
        {
            super();
        }

        @Override
        public void onCompletion(final IHttpResponse response)
        {
            if (response instanceof DefaultHttpResponse)
            {
                DefaultHttpResponse httpResponse = (DefaultHttpResponse) response;
                String newBody = response.getContent() + "<additional>extra</additional>";

                httpResponse.setContent(newBody.getBytes());
            }
        }

        @Override
        public void onFailure(final Throwable th)
        {
            throw new RuntimeException(th.getMessage(), th);
        }
    }

    private static class CustomHttpCallbackNoFuture extends DefaultHttpCallback
    {
        final private Set<Integer> _set;

        public CustomHttpCallbackNoFuture(Set<Integer> set)
        {
            super();
            _set = set;
        }

        @Override
        public void onCompletion(final IHttpResponse response)
        {
            super.onCompletion(response);
            _set.add(response.getStatusCode());
        }
    }

    private static class CustomHttpCallbackTyped extends BaseHttpCallback<Integer>
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
    }
}
