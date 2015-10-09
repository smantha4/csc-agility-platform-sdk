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

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Future;
import java.net.URI;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.protocol.HttpContext;

import com.servicemesh.core.async.CompletablePromise;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.async.PromiseFactory;
import com.servicemesh.io.http.Credentials;
import com.servicemesh.io.http.Credentials.CredentialsType;
import com.servicemesh.io.http.IHttpCallback;
import com.servicemesh.io.http.IHttpClientConfig;
import com.servicemesh.io.http.IHttpHeader;
import com.servicemesh.io.http.IHttpRequest;
import com.servicemesh.io.http.IHttpResponse;
import com.servicemesh.io.http.IHttpClient;
import com.servicemesh.io.http.impl.reactor.IOReactorFactory;
import com.servicemesh.io.proxy.impl.HttpIOSessionStrategy;
import com.servicemesh.io.proxy.impl.HttpsIOSessionStrategy;
import com.servicemesh.io.proxy.impl.ProxyHost;

public class DefaultHttpClient
    implements IHttpClient
{
    private final CloseableHttpAsyncClient client;
    private final IHttpClientConfig config;
    private ProxyHost proxyHost = null;

    public DefaultHttpClient()
    {
        config = null;

        try {
            HttpAsyncClientBuilder builder = HttpAsyncClients.custom().setRedirectStrategy(new SMRedirectStrategy());
            ConnectingIOReactor ioReactor = IOReactorFactory.getInstance().createConnectingReactor();
            Registry<SchemeIOSessionStrategy> registry = generateRegistry(config);

            builder.setConnectionManager(new PoolingNHttpClientConnectionManager(ioReactor, registry));
            //setSSLContext(builder, config);
            client = builder.build();
            client.start();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }
    }

    public DefaultHttpClient(final IHttpClientConfig config)
    {
        this.config = config;

        try {
            HttpAsyncClientBuilder builder = HttpAsyncClients.custom().setRedirectStrategy(new SMRedirectStrategy());
            ConnectingIOReactor ioReactor = IOReactorFactory.getInstance().createConnectingReactor();
            Registry<SchemeIOSessionStrategy> registry = generateRegistry(config);

            builder.setConnectionManager(new PoolingNHttpClientConnectionManager(ioReactor, registry));

            if (config.getProxy() != null) {
                proxyHost = new ProxyHost(config.getProxy());
                builder.setProxy(proxyHost);
            }

            client = builder.build();
            client.start();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    @Deprecated
    public IHttpResponse exec(final IHttpRequest request)
        throws Exception
    {
        Future<IHttpResponse> future = execute(request);

        return future.get();
    }

    @Override
    public Future<IHttpResponse> execute(final IHttpRequest request)
    {
        IHttpCallback<IHttpResponse> callback = new IHttpCallback<IHttpResponse>() {
            @Override
            public void onCompletion(final IHttpResponse value) {}

            @Override
            public IHttpResponse decoder(final IHttpResponse response)
            {
                return response;
            }

            @Override
            public void onCancel() {}

            @Override
            public void onFailure(final Throwable th) {}
        };

        return execute(request, callback);
    }

    @Override
    public <V> Future<V> execute(final IHttpRequest request, final IHttpCallback<V> callback)
    {
        if (request == null) {
            throw new IllegalArgumentException("Missing request parameter");
        }

        if (callback == null) {
            throw new IllegalArgumentException("Missing callback parameter");
        }

        DefaultHttpResponseFuture<V> future = new DefaultHttpResponseFuture<V>(callback);
        FutureCallback<HttpResponse> handler = new DefaultHttpCallbackHandler<V>(future, this, request, config);

        execute(request, handler);

        return future;
    }
    
    @Override
    public Promise<IHttpResponse> promise(IHttpRequest request)
    {
    	final CompletablePromise<IHttpResponse> response = PromiseFactory.create();

    	execute(request, new IHttpCallback<IHttpResponse>() {

			@Override
			public void onCompletion(IHttpResponse value) {
				response.complete(value);
			}

			@Override
			public IHttpResponse decoder(IHttpResponse response) {
				return response;
			}

			@Override
			public void onCancel() {
			}

			@Override
			public void onFailure(Throwable th) {
				response.failure(th);
			}
    	});

    	return response;
    }

    @Override
    public void close()
        throws IOException
    {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void finalize()
        throws Throwable
    {
        try {
            close();
        } catch (Exception ex) {
            // Ignore
        }

        super.finalize();
    }

    public void execute(final IHttpRequest request, final FutureCallback<HttpResponse> handler)
    {
        InputStream stream = request.getContentAsStream();
        RequestBuilder requestBuilder = RequestBuilder.create(request.getMethod().getName());
        Integer requestTimeout = request.getRequestTimeout();
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        HttpContext context = null;

        if (config != null) {
            if (config.getConnectionTimeout() != null) {
                int connectionTimeout = (config.getConnectionTimeout() > 0) ? config.getConnectionTimeout() : 0;

                configBuilder.setConnectTimeout(connectionTimeout);
                configBuilder.setConnectionRequestTimeout(connectionTimeout);
            }

            if ((config.getSocketTimeout() != null) || (requestTimeout != null)) {
                int socketTimeout;

                if (requestTimeout != null) {
                    socketTimeout = (requestTimeout > 0) ? requestTimeout : 0;
                } else {
                    socketTimeout = (config.getSocketTimeout() > 0) ? config.getSocketTimeout() : 0;
                }

                configBuilder.setSocketTimeout(socketTimeout);
             }

            CredentialsProvider credsProvider = generateCredentialsProvider(request, config.getCredentials());
            if (credsProvider != null) {
                HttpClientContext clientContext = HttpClientContext.create();

                clientContext.setCredentialsProvider(credsProvider);
                context = clientContext;
            }
        } else if (requestTimeout != null) {
            int socketTimeout = (requestTimeout > 0) ? requestTimeout : 0;

            configBuilder.setSocketTimeout(socketTimeout);
        }

        configBuilder.setRedirectsEnabled(true);
        requestBuilder.setConfig(configBuilder.build());

        if (proxyHost != null) {
            URI uri = request.getUri();
            int port = uri.getPort();
            if (port == -1)
            {
            	if (uri.getScheme().equals("http"))
            		port = 80;
            	else if (uri.getScheme().equals("https"))
            		port = 443;
            	else
            		port = 80;  //  decent default???
            }
            HttpHost endpoint = new HttpHost(uri.getHost(), port, uri.getScheme());

            proxyHost.setEndpoint(endpoint);
        }

        requestBuilder.setUri(request.getUri());

        for (IHttpHeader nextHeader : request.getHeaders()) {
            requestBuilder.addHeader(nextHeader.getName(), nextHeader.getValue());
        }

        if (stream != null) {
            try {
                requestBuilder.setEntity(new BufferedHttpEntity(new InputStreamEntity(stream, request.getContentLength())));
            } catch (IOException ex) {
                throw new RuntimeException(ex.getLocalizedMessage(), ex);
            }
        }
        
        if (context != null) {
            client.execute(requestBuilder.build(), context, handler);
        } 
        else {
            client.execute(requestBuilder.build(), handler);
        }
    }

    private void setSSLContext(final HttpAsyncClientBuilder builder, final IHttpClientConfig config)
    {
        TrustManager trustManager = new EasyTrustManager();
        KeyManager[] keyManagers = (config != null) ? config.getKeyManagers() : null;

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(keyManagers, new TrustManager[] { trustManager }, null);
            builder.setSSLStrategy(new SSLIOSessionStrategy(sslContext));
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private Registry<SchemeIOSessionStrategy> generateRegistry(final IHttpClientConfig config)
    {
        RegistryBuilder<SchemeIOSessionStrategy> builder = RegistryBuilder.create();
        TrustManager trustManager = new EasyTrustManager();
        KeyManager[] keyManagers = (config != null) ? config.getKeyManagers() : null;

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(keyManagers, new TrustManager[] { trustManager }, null);
            builder.register("https", new HttpsIOSessionStrategy(sslContext, SSLIOSessionStrategy.ALLOW_ALL_HOSTNAME_VERIFIER));
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }

        builder.register("http", new HttpIOSessionStrategy());

        return builder.build();
    }

    private CredentialsProvider generateCredentialsProvider(IHttpRequest request, Credentials credentials)
    {
        CredentialsProvider provider = null;

        if (credentials != null) {
            CredentialsType credsType = credentials.getType();
            String username = credentials.getUsername();
            String password = credentials.getPassword();

            if ((username != null) && !username.isEmpty()) {
                if (CredentialsType.CREDENTIALS_TYPE_USERNAMEPASSORD == credsType) {
                    provider = new BasicCredentialsProvider();
                    provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                } else if (CredentialsType.CREDENTIALS_TYPE_NTCREDS == credsType) {
                    String domain = credentials.getDomain();
                    String host = request.getUri().getHost();

                    provider = new BasicCredentialsProvider();
                    provider.setCredentials(AuthScope.ANY, new NTCredentials(username, password, host, domain));
                }
            }
        }

        return provider;
    }

    private static class EasyTrustManager
        implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) 
            throws CertificateException
        {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException
        {}

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }           
    }
}
