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

package com.servicemesh.io.proxy;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.extras.SelfSignedSslEngineSource;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.servicemesh.io.http.HttpClientFactory;
import com.servicemesh.io.http.HttpMethod;
import com.servicemesh.io.http.IHttpClient;
import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.http.IHttpHeader;
import com.servicemesh.io.http.IHttpRequest;
import com.servicemesh.io.http.IHttpResponse;
import com.servicemesh.io.util.JunitWireMockClassRule;

import socks.JSocksServer;

public class ProxyTest
{
    // This should default to be homed under the bundle home directory: bundles/io
    private final static String JUNIT_KEYSTORE = "/junitkeystore.jks";
    private final static String JUNIT_KEYSTORE_FILENAME = ProxyTest.class.getResource(JUNIT_KEYSTORE).getFile();

    @ClassRule
    public static JUnitSocks5ProxyServer socks5ProxyServer = new JUnitSocks5ProxyServer();

    @ClassRule
    public static JUnitHttpProxyServer httpProxyServer = new JUnitHttpProxyServer();

    @ClassRule
    public static JUnitHttpsProxyServer httpsProxyServer = new JUnitHttpsProxyServer();

    @ClassRule
    public static JunitWireMockClassRule wireMockRule = new JunitWireMockClassRule(JUNIT_KEYSTORE_FILENAME, true);

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    @Test
    public void testHttpProxy() throws Exception
    {
        Proxy proxy = new Proxy("localhost", httpProxyServer.getPort(), ProxyType.HTTP_PROXY, null);
        Proxy badProxy = new Proxy("localhost", getBadProxyPort(), ProxyType.HTTP_PROXY, null);
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(proxy);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Test bad proxy
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(badProxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);

        try
        {
            future.get();
            Assert.fail("future.get succeeded when should have gotten connection exception");
        }
        catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof ExecutionException);
            Assert.assertTrue(ex.getCause() instanceof ConnectException);
        }

        httpClient.close();
    }

    @Test
    public void testHttpProxyHttpEndpoint() throws Exception
    {
        Proxy proxy = new Proxy("localhost", httpProxyServer.getPort(), ProxyType.HTTP_PROXY, null);
        Proxy badProxy = new Proxy("localhost", getBadProxyPort(), ProxyType.HTTP_PROXY, null);
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "http://localhost:" + instanceRule.port() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(proxy);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Test bad proxy
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(badProxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);

        try
        {
            future.get();
            Assert.fail("future.get succeeded when should have gotten connection exception");
        }
        catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof ExecutionException);
            Assert.assertTrue(ex.getCause() instanceof ConnectException);
        }

        httpClient.close();
    }

    @Test
    public void testHttpsProxy() throws Exception
    {
        Proxy proxy = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, null);
        Proxy badProxy = new Proxy("localhost", 3130, ProxyType.HTTPS_PROXY, null);
        //String responseBody = "<response>Some content</response>";
        String responseBody = createLongResponse(8300);
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(proxy);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Test bad proxy
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(badProxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);

        try
        {
            future.get();
            Assert.fail("future.get succeeded when should have gotten connection exception");
        }
        catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof ExecutionException);
            Assert.assertTrue(ex.getCause() instanceof ConnectException);
        }

        httpClient.close();
    }

    @Test
    public void testHttpsProxyHttpEndpoint() throws Exception
    {
        Proxy proxy = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, null);
        String responseBody = createLongResponse(8300);
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "http://localhost:" + instanceRule.port() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(proxy);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();
    }

    @Test
    public void testSocks5Proxy() throws Exception
    {
        Proxy proxy = new Proxy("localhost", socks5ProxyServer.getPort(), ProxyType.SOCKS5_PROXY, null, "rsanchez", "M3sh@dmin!");
        Proxy badProxy = new Proxy("localhost", 1070, ProxyType.SOCKS5_PROXY, null, "rsanchez", "M3sh@dmin!");
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(proxy);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Test bad proxy
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(badProxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);

        try
        {
            future.get();
            Assert.fail("future.get succeeded when should have gotten connection exception");
        }
        catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof ExecutionException);
            Assert.assertTrue(ex.getCause() instanceof ConnectException);
        }

        httpClient.close();
    }

    @Test
    public void testProxyChain() throws Exception
    {
        Proxy proxy3 = new Proxy("localhost", httpProxyServer.getPort(), ProxyType.HTTP_PROXY, null);
        Proxy proxy2 =
                new Proxy("localhost", socks5ProxyServer.getPort(), ProxyType.SOCKS5_PROXY, proxy3, "rsanchez", "M3sh@dmin!");
        Proxy badProxy2 = new Proxy("localhost", getBadProxyPort(), ProxyType.HTTP_PROXY, null);
        Proxy proxy = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, proxy2);
        Proxy badProxy = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, badProxy2);
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "https://localhost:" + instanceRule.httpsPort() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();

        // First test with HTTPS proxy first
        builder.setProxy(proxy);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Second test with HTTPS proxy in the middle
        proxy3 = new Proxy("localhost", socks5ProxyServer.getPort(), ProxyType.SOCKS5_PROXY, null, "rsanchez", "M3sh@dmin!");
        proxy2 = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, proxy3);
        proxy = new Proxy("localhost", httpProxyServer.getPort(), ProxyType.HTTP_PROXY, proxy2);
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(proxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);
        httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Third test with HTTPS proxy last
        proxy3 = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, null);
        proxy2 = new Proxy("localhost", httpProxyServer.getPort(), ProxyType.HTTP_PROXY, proxy3);
        proxy = new Proxy("localhost", socks5ProxyServer.getPort(), ProxyType.SOCKS5_PROXY, proxy2, "rsanchez", "M3sh@dmin!");
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(proxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);
        httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Test bad proxy
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(badProxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);

        try
        {
            future.get();
            Assert.fail("future.get succeeded when should have gotten connection exception");
        }
        catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof ExecutionException);
            Assert.assertTrue(ex.getCause() instanceof RuntimeException);
            Assert.assertTrue(ex.getCause().getMessage().contains("Bad Gateway"));
        }

        httpClient.close();
    }

    @Test
    public void testProxyChainHttpEndpoint() throws Exception
    {
        Proxy proxy3 = new Proxy("localhost", httpProxyServer.getPort(), ProxyType.HTTP_PROXY, null);
        Proxy proxy2 =
                new Proxy("localhost", socks5ProxyServer.getPort(), ProxyType.SOCKS5_PROXY, proxy3, "rsanchez", "M3sh@dmin!");
        Proxy badProxy2 = new Proxy("localhost", getBadProxyPort(), ProxyType.HTTP_PROXY, null);
        Proxy proxy = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, proxy2);
        Proxy badProxy = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, badProxy2);
        String responseBody = "<response>Some content</response>";
        stubFor(get(urlEqualTo("/agility/api/current/storeproducttype"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody(responseBody)));

        String stringUri = "http://localhost:" + instanceRule.port() + "/agility/api/current/storeproducttype";
        IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(stringUri));
        IHttpHeader contentTypeHeaderOut = HttpClientFactory.getInstance().createHeader("Content-Type", "text/xml");
        request.setHeader(contentTypeHeaderOut);

        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();

        // First test with HTTPS proxy first
        builder.setProxy(proxy);

        IHttpClient httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        Future<IHttpResponse> future = httpClient.execute(request);
        IHttpResponse httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Second test with HTTPS proxy in the middle
        proxy3 = new Proxy("localhost", socks5ProxyServer.getPort(), ProxyType.SOCKS5_PROXY, null, "rsanchez", "M3sh@dmin!");
        proxy2 = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, proxy3);
        proxy = new Proxy("localhost", httpProxyServer.getPort(), ProxyType.HTTP_PROXY, proxy2);
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(proxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);
        httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Third test with HTTPS proxy last
        proxy3 = new Proxy("localhost", httpsProxyServer.getPort(), ProxyType.HTTPS_PROXY, null);
        proxy2 = new Proxy("localhost", httpProxyServer.getPort(), ProxyType.HTTP_PROXY, proxy3);
        proxy = new Proxy("localhost", socks5ProxyServer.getPort(), ProxyType.SOCKS5_PROXY, proxy2, "rsanchez", "M3sh@dmin!");
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(proxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);
        httpResponse = future.get();
        Assert.assertNotNull(httpResponse.getStatus());
        Assert.assertEquals(200, httpResponse.getStatusCode());
        Assert.assertEquals(responseBody, httpResponse.getContent());
        httpClient.close();

        // Test bad proxy
        builder = HttpClientFactory.getInstance().getConfigBuilder();
        builder.setProxy(badProxy);
        httpClient = HttpClientFactory.getInstance().getClient(builder.build());
        future = httpClient.execute(request);

        try
        {
            future.get();
            Assert.fail("future.get succeeded when should have gotten connection exception");
        }
        catch (Exception ex)
        {
            Assert.assertTrue(ex instanceof ExecutionException);
            Assert.assertTrue(ex.getCause() instanceof RuntimeException);
            Assert.assertTrue(ex.getCause().getMessage().contains("Bad Gateway"));
        }

        httpClient.close();
    }

    private int getBadProxyPort() throws IOException
    {
        try (ServerSocket serverSocket = new ServerSocket(0))
        {
            return serverSocket.getLocalPort();
        }
    }

    private String createLongResponse(int minSize)
    {
        StringBuilder builder = new StringBuilder("<response>");

        while (builder.length() < minSize)
        {
            builder.append("RepeatingString");
        }

        return builder.append("</response>").toString();
    }

    private static class JUnitHttpProxyServer extends ExternalResource
    {
        private HttpProxyServer _server;
        private int _port = -1;

        @Override
        protected void before() throws Throwable
        {
            try (ServerSocket serverSocket = new ServerSocket(0))
            {
                _port = serverSocket.getLocalPort();
            }

            _server = DefaultHttpProxyServer.bootstrap().withAllowLocalOnly(true).withPort(_port).start();
        }

        @Override
        protected void after()
        {
            if (_server != null)
            {
                _server.stop();
            }
        }

        public int getPort()
        {
            return _port;
        }
    }

    private static class JUnitHttpsProxyServer extends ExternalResource
    {
        private HttpProxyServer _server;
        private int _port = -1;

        @Override
        protected void before() throws Throwable
        {
            try (ServerSocket serverSocket = new ServerSocket(0))
            {
                _port = serverSocket.getLocalPort();
            }

            _server = DefaultHttpProxyServer.bootstrap().withAllowLocalOnly(true).withPort(_port)
                    .withSslEngineSource(new SelfSignedSslEngineSource(true, true, "unittest.p12"))
                    .withAuthenticateSslClients(false).start();
        }

        @Override
        protected void after()
        {
            if (_server != null)
            {
                _server.stop();
            }
        }

        public int getPort()
        {
            return _port;
        }
    }

    private static class JUnitSocks5ProxyServer extends ExternalResource
    {
        private JSocksServer _server;
        private int _port = -1;

        @Override
        protected void before() throws Throwable
        {
            try (ServerSocket serverSocket = new ServerSocket(0))
            {
                _port = serverSocket.getLocalPort();
            }

            _server = new JSocksServer();
            _server.start(_port);
        }

        @Override
        protected void after()
        {
            if (_server != null)
            {
                _server.stop();
            }
        }

        public int getPort()
        {
            return _port;
        }
    }
}
