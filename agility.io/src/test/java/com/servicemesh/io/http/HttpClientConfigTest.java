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

import java.io.Reader;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.cert.Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMParser;
import org.junit.Assert;
import org.junit.Test;

import com.servicemesh.io.proxy.Proxy;
import com.servicemesh.io.proxy.ProxyType;

public class HttpClientConfigTest
{
    @Test
    public void testHttpClientConfig() throws Exception
    {
        KeyManager keyMgr = new KeyManager() {
        };

        KeyManager[] keyMgrs = new KeyManager[] { keyMgr };
        TrustManager trustMgr = new TrustManager() {
        };
        TrustManager[] trustMgrs = new TrustManager[] { trustMgr };
        Credentials creds = new Credentials(Credentials.CredentialsType.CREDENTIALS_TYPE_USERNAMEPASSORD);
        Proxy proxy = new Proxy("localhost", 1080, ProxyType.HTTP_PROXY, null);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClientConfig config = builder.build();

        Assert.assertNull(config.getConnectionTimeout());
        Assert.assertNull(config.getSocketTimeout());
        Assert.assertNull(config.getIdleTimeout());
        Assert.assertNull(config.getRetries());
        Assert.assertNull(config.getMaxConnections());
        Assert.assertNull(config.getKeyManagers());
        Assert.assertNull(config.getTrustManagers());
        Assert.assertNull(config.getCredentials());
        Assert.assertNull(config.getProxy());

        builder.setConnectionTimeout(300);
        builder.setSocketTimeout(500);
        builder.setIdleTimeout(444);
        builder.setRetries(4);
        builder.setMaxConnections(23);
        builder.setKeyManagers(keyMgrs);
        builder.setTrustManagers(trustMgrs);
        builder.setCredentials(creds);
        builder.setProxy(proxy);
        config = builder.build();
        Assert.assertNotNull(config.getConnectionTimeout());
        Assert.assertEquals(300, config.getConnectionTimeout().intValue());
        Assert.assertNotNull(config.getSocketTimeout());
        Assert.assertEquals(500, config.getSocketTimeout().intValue());
        Assert.assertNotNull(config.getIdleTimeout());
        Assert.assertEquals(444, config.getIdleTimeout().intValue());
        Assert.assertNotNull(config.getRetries());
        Assert.assertEquals(4, config.getRetries().intValue());
        Assert.assertNotNull(config.getMaxConnections());
        Assert.assertEquals(23, config.getMaxConnections().intValue());
        Assert.assertNotNull(config.getKeyManagers());
        Assert.assertEquals(1, config.getKeyManagers().length);
        Assert.assertNotNull(config.getTrustManagers());
        Assert.assertEquals(1, config.getTrustManagers().length);
        Assert.assertNotNull(config.getCredentials());
        Assert.assertEquals(Credentials.CredentialsType.CREDENTIALS_TYPE_USERNAMEPASSORD, config.getCredentials().getType());
        Assert.assertNotNull(config.getProxy());
        Assert.assertEquals("localhost", config.getProxy().getHostname());
    }

    @Test
    public void testHttpClientConfig_null() throws Exception
    {
        KeyManager keyMgr = new KeyManager() {
        };

        KeyManager[] keyMgrs = new KeyManager[] { keyMgr };
        TrustManager[] trustMgrs = null;
        Credentials creds = new Credentials(Credentials.CredentialsType.CREDENTIALS_TYPE_USERNAMEPASSORD);
        Proxy proxy = new Proxy("localhost", 1080, ProxyType.HTTP_PROXY, null);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClientConfig config = builder.build();

        Assert.assertNull(config.getConnectionTimeout());
        Assert.assertNull(config.getSocketTimeout());
        Assert.assertNull(config.getIdleTimeout());
        Assert.assertNull(config.getRetries());
        Assert.assertNull(config.getMaxConnections());
        Assert.assertNull(config.getKeyManagers());
        Assert.assertNull(config.getTrustManagers());
        Assert.assertNull(config.getCredentials());
        Assert.assertNull(config.getProxy());

        builder.setConnectionTimeout(300);
        builder.setSocketTimeout(500);
        builder.setIdleTimeout(444);
        builder.setRetries(4);
        builder.setMaxConnections(23);
        builder.setKeyManagers(keyMgrs);
        builder.setTrustManagers(trustMgrs);
        builder.setCredentials(creds);
        builder.setProxy(proxy);
        config = builder.build();
        Assert.assertNotNull(config.getConnectionTimeout());
        Assert.assertEquals(300, config.getConnectionTimeout().intValue());
        Assert.assertNotNull(config.getSocketTimeout());
        Assert.assertEquals(500, config.getSocketTimeout().intValue());
        Assert.assertNotNull(config.getIdleTimeout());
        Assert.assertEquals(444, config.getIdleTimeout().intValue());
        Assert.assertNotNull(config.getRetries());
        Assert.assertEquals(4, config.getRetries().intValue());
        Assert.assertNotNull(config.getMaxConnections());
        Assert.assertEquals(23, config.getMaxConnections().intValue());
        Assert.assertNotNull(config.getKeyManagers());
        Assert.assertEquals(1, config.getKeyManagers().length);
        Assert.assertNull(config.getTrustManagers());
        Assert.assertNotNull(config.getCredentials());
        Assert.assertEquals(Credentials.CredentialsType.CREDENTIALS_TYPE_USERNAMEPASSORD, config.getCredentials().getType());
        Assert.assertNotNull(config.getProxy());
        Assert.assertEquals("localhost", config.getProxy().getHostname());
    }

    @Test
    public void testHttpClientConfig_tm_ValidCert() throws Exception
    {
        KeyManager keyMgr = new KeyManager() {
        };
        KeyManager[] keyMgrs = new KeyManager[] { keyMgr };
        TrustManager[] trustMgrs = getValidTrustManager();
        Credentials creds = new Credentials(Credentials.CredentialsType.CREDENTIALS_TYPE_USERNAMEPASSORD);
        Proxy proxy = new Proxy("localhost", 1080, ProxyType.HTTP_PROXY, null);
        IHttpClientConfigBuilder builder = HttpClientFactory.getInstance().getConfigBuilder();
        IHttpClientConfig config = builder.build();

        Assert.assertNull(config.getConnectionTimeout());
        Assert.assertNull(config.getSocketTimeout());
        Assert.assertNull(config.getIdleTimeout());
        Assert.assertNull(config.getRetries());
        Assert.assertNull(config.getMaxConnections());
        Assert.assertNull(config.getKeyManagers());
        Assert.assertNull(config.getTrustManagers());
        Assert.assertNull(config.getCredentials());
        Assert.assertNull(config.getProxy());

        builder.setConnectionTimeout(300);
        builder.setSocketTimeout(500);
        builder.setIdleTimeout(444);
        builder.setRetries(4);
        builder.setMaxConnections(23);
        builder.setKeyManagers(keyMgrs);
        builder.setTrustManagers(trustMgrs);
        builder.setCredentials(creds);
        builder.setProxy(proxy);
        config = builder.build();
        Assert.assertNotNull(config.getConnectionTimeout());
        Assert.assertEquals(300, config.getConnectionTimeout().intValue());
        Assert.assertNotNull(config.getSocketTimeout());
        Assert.assertEquals(500, config.getSocketTimeout().intValue());
        Assert.assertNotNull(config.getIdleTimeout());
        Assert.assertEquals(444, config.getIdleTimeout().intValue());
        Assert.assertNotNull(config.getRetries());
        Assert.assertEquals(4, config.getRetries().intValue());
        Assert.assertNotNull(config.getMaxConnections());
        Assert.assertEquals(23, config.getMaxConnections().intValue());
        Assert.assertNotNull(config.getKeyManagers());
        Assert.assertEquals(1, config.getKeyManagers().length);
        Assert.assertNotNull(config.getTrustManagers());
        Assert.assertEquals(1, config.getTrustManagers().length);
        Assert.assertNotNull(config.getCredentials());
        Assert.assertEquals(Credentials.CredentialsType.CREDENTIALS_TYPE_USERNAMEPASSORD, config.getCredentials().getType());
        Assert.assertNotNull(config.getProxy());
        Assert.assertEquals("localhost", config.getProxy().getHostname());
    }

    public TrustManager[] getValidTrustManager() throws Exception
    {
        String cacert =
                "-----BEGIN CERTIFICATE-----\r\nMIIFwTCCA6mgAwIBAgIJAPe+2+CwiflqMA0GCSqGSIb3DQEBCwUAMHcxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJUWDEPMA0GA1UEBwwGQXVzdGluMQwwCgYDVQQKDANDU0MxDjAMBgNVBAsMBUNsb3VkMRgwFgYDVQQDDA8xOTIuMTY4LjE1MC4xMzcxEjAQBgkqhkiG9w0BCQEWA3NkYTAeFw0xNjEwMTkxMjQ1MjVaFw0xNzEwMTkxMjQ1MjVaMHcxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJUWDEPMA0GA1UEBwwGQXVzdGluMQwwCgYDVQQKDANDU0MxDjAMBgNVBAsMBUNsb3VkMRgwFgYDVQQDDA8xOTIuMTY4LjE1MC4xMzcxEjAQBgkqhkiG9w0BCQEWA3NkYTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAL4yMIJveBjvjrrcaWpjfiNClwNQScrODOFWCd9nMOflpOEKnHj0b9If0LKokRwhcwuvngimbYPl+XLzhE0CECT+EUvyqMQudmkc1g7Pos7oox9pJMKzUrgW1HLmN/9RmraS1WtdaOTxDjXtjh48jKx/EAj6sYizI99ofU++ja7VJ+oXVQyKwXKsE2vVl1RXee+A8DQtaYUCRJv6xuAPiWR1vTSYFsXzVMaOlv2fM5t8qlw/r1vjvsyQzNM30srF3rtY9gq2m76xx9dRNMW/n16WD6Ek4MaK/EFR12T6H3V7bucne2CdgBAk3HYvmaRblvmKbJJClzFEb0PscYVm0LWVM3mafytk2wjgyRqKFSM8GEOx2Q9FWTeuxyjmu6aSvHGOzF8a/FNM0kmAsNRSfHb+ne4Ux0HDUvlanjVrtYg32lzo8LAEo/r6BOhDkcyCU6qXit5cKAqH8o5U0fSiRm2tFWWHrE3KTbx75YByipUvAcPY5F0TGboIhUpBghSzGe0CViDzvt40cCR93JFsNpbhAc1vTlebviGyCpoyd9NdLHl7LnlRYuI8n5Yhj5da5Abc9N4wsiPOJKWdpB1eLfT4BmT9RrA/m6DjG7acgdtI5sWcCzquU6vkor7jUe/RQIkNNqrMT5w3frpMRCH4sR+pVJ+8gl56KhM/BnfhJeNbAgMBAAGjUDBOMB0GA1UdDgQWBBTlDJA9lYQnXrDi5P9KfgRKX+a8vzAfBgNVHSMEGDAWgBTlDJA9lYQnXrDi5P9KfgRKX+a8vzAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4ICAQC0cu2Ob6wYOS0ZZaMR/uDd7XoUZ82tu5Zm5QOV1D7SFv9k8lvq7igM1nw1cpplBjy+9kllyi41KHlq9J+2zTi8C5KFciyPX5SBEIDcySS/YgWU9alkaKOEie6NeqQH7pSkm9E8dAbx7col4TPutiFu9izVQ6cntwq5Pu5BlfSUoZ69thuwejfQWina2E5YCP16D56712ebXkVTCwndjn50++M69J7z+eO0obMHfgiQNRjm1NpxR4oKm7GK30C53ybgzqt7WJMkFWH/tz4VRcJWkInm6/8bUgDt+pi56kv8vVbvNwZ3EgdZOt00CbY722kChiys1/hUGTlHg0/dizWn/FW9YxHNjXtdVIqMk2yRi8kl+KCkANGq+dPEOI1y9kly0nLDcd3q+UZ85hsBb+u3sFmn8GvByK7xSR4Z3EFPAq7qTK9CzdFxpQBrGXm52K1Q5HsDHMSHkl5Hm6LPfetL4s/1QY+nX339zCn6sC+U3V23o7D6gDSF5rwfaYSUfwFFDsh0Iz5htsZcycD5WKH2S/2+wjr7Wr11l7Ph8hdl1SXhnoGvhD4wyNu/VtJ12RaxZx/Ny9GfJsXIXn0Jbp2iwxKCRe0C4W/m0S27zAM+Vy2kM7uNVeKRgwLSRGjKhvXdPnma9laHSGJXeBQ00z8GU7C9Rv6KqCuA54HdM4/FEw==\r\n-----END CERTIFICATE-----";
        KeyStore trustStore = createTrustStore(cacert);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        return trustManagers;
    }

    public static KeyStore createTrustStore(String capem) throws Exception
    {
        KeyStore trustStore = null;
        trustStore = KeyStore.getInstance("JKS");
        Reader certReader = new StringReader(capem);
        PEMParser pemParser = new PEMParser(certReader);
        X509CertificateHolder certificateHolder = (X509CertificateHolder) pemParser.readObject();
        Certificate caCertificate = new JcaX509CertificateConverter()
                .setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()).getCertificate(certificateHolder);
        trustStore.load(null);
        trustStore.setCertificateEntry("ca", caCertificate);

        return trustStore;
    }
}
