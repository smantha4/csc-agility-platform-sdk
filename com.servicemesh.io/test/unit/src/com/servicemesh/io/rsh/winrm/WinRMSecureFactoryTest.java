/**
 *              COPYRIGHT (C) 2008-2015 SERVICEMESH, INC.
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

package com.servicemesh.io.rsh.winrm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import intel.management.wsman.WsmanUtils;

import java.io.InputStream;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.servicemesh.io.http.HttpClientFactory;
import com.servicemesh.io.http.HttpMethod;
import com.servicemesh.io.http.IHttpClient;
import com.servicemesh.io.http.IHttpClientConfig;
import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.http.IHttpHeader;
import com.servicemesh.io.http.IHttpRequest;
import com.servicemesh.io.http.IHttpResponse;
import com.servicemesh.io.rsh.IRemoteShellFactory;
import com.servicemesh.io.rsh.RemoteShell;
import com.servicemesh.io.rsh.RemoteShell.SocketFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientFactory.class, WsmanUtils.class, WinRMUtils.class})
public class WinRMSecureFactoryTest 
{
	private static final String HTTP_HEADER_NAME = "Content-Type";
	private static final String HTTP_HEADER_VALUE = "application/soap+xml;charset=UTF-8";
	private static final int HTTP_STATUS_CODE = 202;
	private static final int TEST_PORT = 5150;
    private IRemoteShellFactory remoteShellFactory;
    private RemoteShell remoteShell;
    private SocketFactory socketFactory;    
    @Mock
    private HttpClientFactory mockHttpClientFactory;
    @Mock
    private IHttpClientConfigBuilder mockConfigBuilder;
    @Mock
    private IHttpClientConfig mockConfig;
    @Mock
    private IHttpClient mockHttpClient;
    @Mock
    private IHttpRequest mockHttpRequest;
    @Mock
    private IHttpResponse mockHttpResponse;
    @Mock
    private IHttpHeader mockHttpHeader;
    @Mock
    private WsmanUtils mockWsmanUtils;
    @Mock
    private WinRMUtils mockWinRMUtils;
    @Mock
    private Document mockDocument;
    @Mock
    private Element mockElement;
    @Mock
    private NodeList mockNodeList;
    @Mock
    private InputStream mockInputStream;
    private String userName;
    private String passwd; 
    private String key;
    private String host; 
    private int port;
    private int reconnectRetries = 0;
    private int reconnectInterval = 0;     
    private boolean tls = false;
    
    @Before
    public void before() 
    throws Exception
    {
    	//Given
    	initMocks(this);
    	
        remoteShellFactory = WinRMSecureFactory.getInstance();
        userName = "johndoe";
        passwd = "johndoepw";
        key = "-----BEGIN CERTIFICATE-----\\ MIIDhzCCAm+gAwIBAgIVAIZmAuSfUiRtXPKx5Qngv8M2GvljMA0GCSqGSIb3DQEC\\ BQUAMFMxITAfBgNVBAMMGEFXUyBMaW1pdGVkLUFzc3VyYW5jZSBDQTEMMAoGA1UE\\ CwwDQVdTMRMwEQYDVQQKDApBbWF6b24uY29tMQswCQYDVQQGEwJVUzAeFw0xMzA5\\ MzAxNjMyNTBaFw0xNDA5MzAxNjMyNTBaME4xCzAJBgNVBAYTAlVTMRMwEQYDVQQK\\ DApBbWF6b24uY29tMRIwEAYDVQQLDAlBV1MtVXNlcnMxFjAUBgNVBAMMDTE0ZWI5\\ NWM0a3A0NnowggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCEomMVCbfx\\ J5ZRDdwSnq38Pvtd2nj2gY9NzfDr0SiICdRm/h5kzD+K3QEOVra4htE3sK6CVRv4\\ gyK2uvwwdUhHq6kRJni0q/zrLmjKxzFOXuRLq8WO/i9cj7EAyW+pczAt2qwNuKpT\\ 8+8KiZxejJJfRG/ZEuLrYSLh5ArGjbOc0sgCOEkpu1qtaGtKkp4JHMA26crsDzN9\\ 5ty4J8pfSx/oFyGDNcVaPrnF5r4b7s+DnvS+mqq4ZMsWZtxx239Stjnnel+ugnia\\ D2myqgnjk21kchl86ijSbgs6WEAM8gyYYEgIgVteC6EItHb9sLSi3d28I6iR98da\\ AUWWxb3MhfSlAgMBAAGjVzBVMA4GA1UdDwEB/wQEAwIFoDAWBgNVHSUBAf8EDDAK\\ BggrBgEFBQcDAjAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBTyGmXmxYmTbCVnU6P0\\ Ug52y0AELzANBgkqhkiG9w0BAQUFAAOCAQEAMrhWz1fhQmx3gIyMUAUXReIiAP0n\\ JNLK/yMdNMdflEcTViTk43z1FN3Uk3kSr8JI6myalv20Qml5VRz2P6Cm/7lNXZla\\ kDXs7T7nko+pQQWX4hK48LIuXDEn3/EbIwDi4U5PAWp0UdCHejpfm/1HZDqXrLmg\\ unKQXBiNwM9dH9K3xgtQKOZyb0Obnkw8U9Y0xgcWSB1W4uc9VZEK4OF9io2mQuUP\\ XZGgnYJr7qAZM+i9o0PnT/a8ZILD9++yW4ZYvWUeMabqZica6sM2oPOLVKviyJ5D\\ v4dqeI/d0k5JwdKGbr/h4dMp0CL9TiI6XzUfTDb649ocI9zw27/CTWHnfw==\\ -----END CERTIFICATE-----";
        host = "somehost";
        port = TEST_PORT;
        
        PowerMockito.mockStatic(HttpClientFactory.class);
        PowerMockito.mockStatic(WsmanUtils.class);
        PowerMockito.mockStatic(WinRMUtils.class);
    }

    //---------------
   	//Positive Tests
   	//---------------    
   
	@Test
    public void testCreateRemoteShellFactoryByConfigBuilderUsernamePasswdHostPortReconnectretriesReconnectinterval()
    throws Exception
  	{
        //When    	
	    when(mockConfigBuilder.build()).thenReturn(mockConfig);
	    
	    when(HttpClientFactory.getInstance()).thenReturn(mockHttpClientFactory);
	    
	    when(mockHttpClientFactory.getClient(isA(IHttpClientConfig.class))).thenReturn(mockHttpClient);
	    when(mockHttpClientFactory.createRequest(eq(HttpMethod.POST), isA(URI.class))).thenReturn(mockHttpRequest);
	    when(mockHttpClientFactory.createHeader(eq(HTTP_HEADER_NAME), eq(HTTP_HEADER_VALUE))).thenReturn(mockHttpHeader);
	    
	    when(mockHttpHeader.getName()).thenReturn(HTTP_HEADER_NAME);
	    when(mockHttpClient.exec(isA(IHttpRequest.class))).thenReturn(mockHttpResponse);
	    
	    when(mockHttpResponse.getStatusCode()).thenReturn(HTTP_STATUS_CODE);
	    when(mockHttpResponse.getContentAsStream()).thenReturn(mockInputStream);
	    
	    when(WinRMUtils.newInstance(isA(String.class))).thenReturn(mockWinRMUtils);
	    
	    when(WsmanUtils.newInstance()).thenReturn(mockWsmanUtils);
	    when(WsmanUtils.findChild(isA(Element.class), isA(String.class), isA(String.class))).thenReturn(mockElement); 
	    
	    when(mockWsmanUtils.loadDocumentFromString(isA(String.class))).thenReturn(mockDocument);
	    when(mockWsmanUtils.loadDocument(isA(InputStream.class))).thenReturn(mockDocument);

	    when(mockDocument.getDocumentElement()).thenReturn(mockElement);
	    
	    when(mockElement.getChildNodes()).thenReturn(mockNodeList);
	    when(mockNodeList.getLength()).thenReturn(0);
	    
  		//Then
		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof WinRM);
  		assertTrue(remoteShell.getPort() == TEST_PORT);
  	}    
    
	@Test
    public void testCreateRemoteShellFactoryByConfigBuilderUsernamePasswdHostDefaultPortReconnectretriesReconnectinterval()
    throws Exception
  	{
		//Given
		port = 0;
		
        //When    	
	    when(mockConfigBuilder.build()).thenReturn(mockConfig);
	    
	    when(HttpClientFactory.getInstance()).thenReturn(mockHttpClientFactory);
	    
	    when(mockHttpClientFactory.getClient(isA(IHttpClientConfig.class))).thenReturn(mockHttpClient);
	    when(mockHttpClientFactory.createRequest(eq(HttpMethod.POST), isA(URI.class))).thenReturn(mockHttpRequest);
	    when(mockHttpClientFactory.createHeader(eq(HTTP_HEADER_NAME), eq(HTTP_HEADER_VALUE))).thenReturn(mockHttpHeader);
	    
	    when(mockHttpHeader.getName()).thenReturn(HTTP_HEADER_NAME);
	    when(mockHttpClient.exec(isA(IHttpRequest.class))).thenReturn(mockHttpResponse);
	    
	    when(mockHttpResponse.getStatusCode()).thenReturn(HTTP_STATUS_CODE);
	    when(mockHttpResponse.getContentAsStream()).thenReturn(mockInputStream);
	    
	    when(WinRMUtils.newInstance(isA(String.class))).thenReturn(mockWinRMUtils);
	    
	    when(WsmanUtils.newInstance()).thenReturn(mockWsmanUtils);
	    when(WsmanUtils.findChild(isA(Element.class), isA(String.class), isA(String.class))).thenReturn(mockElement); 
	    
	    when(mockWsmanUtils.loadDocumentFromString(isA(String.class))).thenReturn(mockDocument);
	    when(mockWsmanUtils.loadDocument(isA(InputStream.class))).thenReturn(mockDocument);

	    when(mockDocument.getDocumentElement()).thenReturn(mockElement);
	    
	    when(mockElement.getChildNodes()).thenReturn(mockNodeList);
	    when(mockNodeList.getLength()).thenReturn(0);
	    
  		//Then
		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof WinRM);
  		assertTrue(remoteShell.getPort() == WinRMSecureFactory.DEFAULT_PORT);
  	}
	
 	@Test
    public void testCreateRemoteShellFactoryByConfigBuilderUsernamePasswordHostPortReconnectretriesReconnectintervalTls()
    throws Exception
  	{
        //When    	
	    when(mockConfigBuilder.build()).thenReturn(mockConfig);
	    
	    when(HttpClientFactory.getInstance()).thenReturn(mockHttpClientFactory);
	    
	    when(mockHttpClientFactory.getClient(isA(IHttpClientConfig.class))).thenReturn(mockHttpClient);
	    when(mockHttpClientFactory.createRequest(eq(HttpMethod.POST), isA(URI.class))).thenReturn(mockHttpRequest);
	    when(mockHttpClientFactory.createHeader(eq(HTTP_HEADER_NAME), eq(HTTP_HEADER_VALUE))).thenReturn(mockHttpHeader);
	    
	    when(mockHttpHeader.getName()).thenReturn(HTTP_HEADER_NAME);
	    when(mockHttpClient.exec(isA(IHttpRequest.class))).thenReturn(mockHttpResponse);
	    
	    when(mockHttpResponse.getStatusCode()).thenReturn(HTTP_STATUS_CODE);
	    when(mockHttpResponse.getContentAsStream()).thenReturn(mockInputStream);
	    
	    when(WinRMUtils.newInstance(isA(String.class))).thenReturn(mockWinRMUtils);
	    
	    when(WsmanUtils.newInstance()).thenReturn(mockWsmanUtils);
	    when(WsmanUtils.findChild(isA(Element.class), isA(String.class), isA(String.class))).thenReturn(mockElement); 
	    
	    when(mockWsmanUtils.loadDocumentFromString(isA(String.class))).thenReturn(mockDocument);
	    when(mockWsmanUtils.loadDocument(isA(InputStream.class))).thenReturn(mockDocument);

	    when(mockDocument.getDocumentElement()).thenReturn(mockElement);
	    
	    when(mockElement.getChildNodes()).thenReturn(mockNodeList);
	    when(mockNodeList.getLength()).thenReturn(0);    		
 		
  		//Then
		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, key, host, port, reconnectRetries, reconnectInterval);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof WinRM);        
  		assertTrue(remoteShell.getPort() == TEST_PORT);
  	}
 	
 	@Test
    public void testCreateRemoteShellFactoryByConfigBuilderUsernamePasswordHostDefaultPortReconnectretriesReconnectintervalTls()
    throws Exception
  	{
 		//Given
 		port = 0;
 		
        //When    	
	    when(mockConfigBuilder.build()).thenReturn(mockConfig);
	    
	    when(HttpClientFactory.getInstance()).thenReturn(mockHttpClientFactory);
	    
	    when(mockHttpClientFactory.getClient(isA(IHttpClientConfig.class))).thenReturn(mockHttpClient);
	    when(mockHttpClientFactory.createRequest(eq(HttpMethod.POST), isA(URI.class))).thenReturn(mockHttpRequest);
	    when(mockHttpClientFactory.createHeader(eq(HTTP_HEADER_NAME), eq(HTTP_HEADER_VALUE))).thenReturn(mockHttpHeader);
	    
	    when(mockHttpHeader.getName()).thenReturn(HTTP_HEADER_NAME);
	    when(mockHttpClient.exec(isA(IHttpRequest.class))).thenReturn(mockHttpResponse);
	    
	    when(mockHttpResponse.getStatusCode()).thenReturn(HTTP_STATUS_CODE);
	    when(mockHttpResponse.getContentAsStream()).thenReturn(mockInputStream);
	    
	    when(WinRMUtils.newInstance(isA(String.class))).thenReturn(mockWinRMUtils);
	    
	    when(WsmanUtils.newInstance()).thenReturn(mockWsmanUtils);
	    when(WsmanUtils.findChild(isA(Element.class), isA(String.class), isA(String.class))).thenReturn(mockElement); 
	    
	    when(mockWsmanUtils.loadDocumentFromString(isA(String.class))).thenReturn(mockDocument);
	    when(mockWsmanUtils.loadDocument(isA(InputStream.class))).thenReturn(mockDocument);

	    when(mockDocument.getDocumentElement()).thenReturn(mockElement);
	    
	    when(mockElement.getChildNodes()).thenReturn(mockNodeList);
	    when(mockNodeList.getLength()).thenReturn(0);    		
 		
  		//Then
		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, key, host, port, reconnectRetries, reconnectInterval);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof WinRM);        
  		assertTrue(remoteShell.getPort() == WinRMSecureFactory.DEFAULT_PORT);
  	} 	
    
    //---------------
  	//Negative Tests
  	//---------------
  	
 	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByNullConfigBuilderUsernamePasswdHostPortReconnectretriesReconnectinterval()
    throws Exception
  	{
 		//Given
 		mockConfigBuilder = null;
 		
  		//Then
 		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval);
  	} 	
 	
 	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByConfigBuilderNullUsernamePasswdHostPortReconnectretriesReconnectinterval()
    throws Exception
  	{
 		//Given
 		userName = null;
 		
  		//Then
 		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval);
  	}  	
 	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByConfigBuilderUsernameNullPasswdHostPortReconnectretriesReconnectinterval()
    throws Exception
  	{
 		//Given
 		passwd = null;
 		
  		//Then
 		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval);
  	}
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByConfigBuilderUsernamePasswdNullHostPortReconnectretriesReconnectinterval()
    throws Exception
  	{
 		//Given
 		host = null;
 		
  		//Then
 		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval);
  	}	
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByNullConfigBuilderUsernamePasswdHostPortReconnectretriesReconnectintervalTls()
    throws Exception
  	{
 		//Given
 		mockConfigBuilder = null;
 		
  		//Then
 		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval, tls);
  	}	
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByConfigBuilderNullUsernamePasswdHostPortReconnectretriesReconnectintervalTls()
    throws Exception
  	{
 		//Given
 		userName = null;
 		
  		//Then
 		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval, tls);
  	}		
 	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByConfigBuilderUsernameNullPasswdHostPortReconnectretriesReconnectintervalTls()
    throws Exception
  	{
 		//Given
 		passwd = null;
 		
  		//Then
 		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval, tls);
  	}		
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByConfigBuilderUsernamePasswdNullHostPortReconnectretriesReconnectintervalTls()
    throws Exception
  	{
 		//Given
 		host = null;
 		
  		//Then
 		remoteShell = remoteShellFactory.createRemoteShell(mockConfigBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval, tls);
  	}		
	
 	@Test(expected = UnsupportedOperationException.class)
    public void testCreateRemoteShellFactoryByUsernamePasswdHostPort()
    throws Exception
  	{
  		//Then
        remoteShellFactory.createRemoteShell(userName, passwd, host, port);
  	}
 	
 	@Test(expected = UnsupportedOperationException.class)
    public void testCreateRemoteShellFactoryBySocketFactoryUsernamePasswdHostPort()
    throws Exception
  	{
  		//Then
        remoteShellFactory.createRemoteShell(socketFactory, userName, passwd, host, port);
  	} 	
 	
 	@Test(expected = UnsupportedOperationException.class)
    public void testCreateRemoteShellFactoryByUsernameKeyHostPort()
    throws Exception
  	{
  		//Then
        remoteShellFactory.createRemoteShell(userName, key, host, port);
  	}
 	
 	@Test(expected = UnsupportedOperationException.class)
    public void testCreateRemoteShellFactoryBySocketFactoryUsernameKeyHostPort()
    throws Exception
  	{
  		//Then
        remoteShellFactory.createRemoteShell(socketFactory, userName, key, host, port);
  	} 	
}
