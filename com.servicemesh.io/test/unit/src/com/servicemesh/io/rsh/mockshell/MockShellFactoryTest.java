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

package com.servicemesh.io.rsh.mockshell;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.rsh.IRemoteShellFactory;
import com.servicemesh.io.rsh.RemoteShell;
import com.servicemesh.io.rsh.RemoteShell.SocketFactory;

@RunWith(MockitoJUnitRunner.class)
public class MockShellFactoryTest 
{
	private static final int TEST_PORT = 5150;
    private IRemoteShellFactory remoteShellFactory;
    private RemoteShell remoteShell;
    @Mock
    private SocketFactory socketFactory;
    private IHttpClientConfigBuilder configBuilder;
    private String userName;
    private String passwd; 
    private String key;
    private String host; 
    private int port;
    private int reconnectRetries = 0;
    private int reconnectInterval = 0;
    
    @Before
    public void before()
    throws Exception
    {
    	//Given
        remoteShellFactory = MockShellFactory.getInstance();
        userName = "johndoe";
        passwd = "johndoepw"; 
        key = "-----BEGIN CERTIFICATE-----\\ MIIDhzCCAm+gAwIBAgIVAIZmAuSfUiRtXPKx5Qngv8M2GvljMA0GCSqGSIb3DQEC\\ BQUAMFMxITAfBgNVBAMMGEFXUyBMaW1pdGVkLUFzc3VyYW5jZSBDQTEMMAoGA1UE\\ CwwDQVdTMRMwEQYDVQQKDApBbWF6b24uY29tMQswCQYDVQQGEwJVUzAeFw0xMzA5\\ MzAxNjMyNTBaFw0xNDA5MzAxNjMyNTBaME4xCzAJBgNVBAYTAlVTMRMwEQYDVQQK\\ DApBbWF6b24uY29tMRIwEAYDVQQLDAlBV1MtVXNlcnMxFjAUBgNVBAMMDTE0ZWI5\\ NWM0a3A0NnowggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCEomMVCbfx\\ J5ZRDdwSnq38Pvtd2nj2gY9NzfDr0SiICdRm/h5kzD+K3QEOVra4htE3sK6CVRv4\\ gyK2uvwwdUhHq6kRJni0q/zrLmjKxzFOXuRLq8WO/i9cj7EAyW+pczAt2qwNuKpT\\ 8+8KiZxejJJfRG/ZEuLrYSLh5ArGjbOc0sgCOEkpu1qtaGtKkp4JHMA26crsDzN9\\ 5ty4J8pfSx/oFyGDNcVaPrnF5r4b7s+DnvS+mqq4ZMsWZtxx239Stjnnel+ugnia\\ D2myqgnjk21kchl86ijSbgs6WEAM8gyYYEgIgVteC6EItHb9sLSi3d28I6iR98da\\ AUWWxb3MhfSlAgMBAAGjVzBVMA4GA1UdDwEB/wQEAwIFoDAWBgNVHSUBAf8EDDAK\\ BggrBgEFBQcDAjAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBTyGmXmxYmTbCVnU6P0\\ Ug52y0AELzANBgkqhkiG9w0BAQUFAAOCAQEAMrhWz1fhQmx3gIyMUAUXReIiAP0n\\ JNLK/yMdNMdflEcTViTk43z1FN3Uk3kSr8JI6myalv20Qml5VRz2P6Cm/7lNXZla\\ kDXs7T7nko+pQQWX4hK48LIuXDEn3/EbIwDi4U5PAWp0UdCHejpfm/1HZDqXrLmg\\ unKQXBiNwM9dH9K3xgtQKOZyb0Obnkw8U9Y0xgcWSB1W4uc9VZEK4OF9io2mQuUP\\ XZGgnYJr7qAZM+i9o0PnT/a8ZILD9++yW4ZYvWUeMabqZica6sM2oPOLVKviyJ5D\\ v4dqeI/d0k5JwdKGbr/h4dMp0CL9TiI6XzUfTDb649ocI9zw27/CTWHnfw==\\ -----END CERTIFICATE-----";
        host = "somehost"; 
        port = TEST_PORT;
    }
    
    //---------------
  	//Positive Tests
  	//---------------    
  	
  	@Test
    public void testCreateRemoteShellFactoryByUsernamePasswdHostPort()
    throws Exception
  	{
  		//Then
  		remoteShell = remoteShellFactory.createRemoteShell(userName, passwd, host, port);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof MockShell);
  		assertTrue(remoteShell.getPort() == TEST_PORT);
  	}	    
  	
  	@Test
    public void testCreateRemoteShellFactoryByUsernamePasswdHostDefaultPort()
    throws Exception
  	{
  		//Given
  		port = 0;
  		
  		//Then
  		remoteShell = remoteShellFactory.createRemoteShell(userName, passwd, host, port);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof MockShell);
  		assertTrue(remoteShell.getPort() == MockShellFactory.DEFAULT_PORT);
  	}  	
  	
  	@Test
    public void testCreateRemoteShellFactoryBySocketfactoryUsernamePasswdHostPort()
    throws Exception
  	{
  		//Then
  		remoteShell = remoteShellFactory.createRemoteShell(socketFactory, userName, passwd, host, port);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof MockShell);
  		assertTrue(remoteShell.getPort() == TEST_PORT);
  	}
  	
	@Test
    public void testCreateRemoteShellFactoryBySocketfactoryUsernamePasswdHostDefaultPort()
    throws Exception
  	{
		//Given
		port = 0;
		
  		//Then
  		remoteShell = remoteShellFactory.createRemoteShell(socketFactory, userName, passwd, host, port);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof MockShell);
  		assertTrue(remoteShell.getPort() == MockShellFactory.DEFAULT_PORT);
  	}  	
  	
  	@Test
    public void testCreateRemoteShellFactoryByUsernameKeyHostPort()
    throws Exception
  	{
  		//Then
  		remoteShell = remoteShellFactory.createRemoteShell(userName, key, host, port);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof MockShell);
  		assertTrue(remoteShell.getPort() == TEST_PORT);
  	}
  	
  	@Test
    public void testCreateRemoteShellFactoryByUsernameKeyHostDefaultPort()
    throws Exception
  	{
  		//Given
  		port = 0;
  		
  		//Then
  		remoteShell = remoteShellFactory.createRemoteShell(userName, key, host, port);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof MockShell);
  		assertTrue(remoteShell.getPort() == MockShellFactory.DEFAULT_PORT);
  	}  	
  	
  	@Test
    public void testCreateRemoteShellFactoryBySocketfactoryUsernameKeyHostPort()
    throws Exception
  	{
  		//Then
  		remoteShell = remoteShellFactory.createRemoteShell(socketFactory, userName, key, host, port);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof MockShell);
  		assertTrue(remoteShell.getPort() == TEST_PORT);
  	}
  	
 	@Test
    public void testCreateRemoteShellFactoryBySocketfactoryUsernameKeyHostDefaultPort()
    throws Exception
  	{
 		//Given
 		port  = 0;
 		
  		//Then
  		remoteShell = remoteShellFactory.createRemoteShell(socketFactory, userName, key, host, port);

  		assertNotNull(remoteShell);
  		assertTrue(remoteShell instanceof MockShell);
  		assertTrue(remoteShell.getPort() == MockShellFactory.DEFAULT_PORT);
  	}  	
    
    //---------------
  	//Negative Tests
  	//---------------

 	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByNullUsernamePasswdHostPort()
    throws Exception
  	{
 		//Given
 		userName = null;

  		//Then
  		remoteShellFactory.createRemoteShell(userName, passwd, host, port);
  	}

	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByUsernameNullPasswdHostPort()
    throws Exception
  	{
 		//Given
 		passwd = null;

  		//Then
  		remoteShellFactory.createRemoteShell(userName, passwd, host, port);
  	} 	

	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByUsernamePasswdNullHostPort()
    throws Exception
  	{
 		//Given
 		host = null;

  		//Then
  		remoteShellFactory.createRemoteShell(userName, passwd, host, port);
  	}

 	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryBySocketfactoryNullUsernamePasswdHostPort()
    throws Exception
  	{
 		//Given
 		userName = null;
 		
  		//Then
  		remoteShellFactory.createRemoteShell(socketFactory, userName, passwd, host, port);
  	}  	
 	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryBySocketfactoryUsernameNullPasswdHostPort()
    throws Exception
  	{
 		//Given
		
 		passwd = null;
 		
  		//Then
  		remoteShellFactory.createRemoteShell(socketFactory, userName, passwd, host, port);
  	}   
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryBySocketfactoryUsernamePasswdNullHostPort()
    throws Exception
  	{
 		//Given
 		host = null;
 		
  		//Then
  		remoteShellFactory.createRemoteShell(socketFactory, userName, passwd, host, port);
  	}
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByNullUsernameKeyHostPort()
    throws Exception
  	{
 		//Given
 		userName = null;
 		
  		//Then
        remoteShellFactory.createRemoteShell(userName, key, host, port);
  	}

	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByUsernameNullKeyHostPort()
    throws Exception
  	{
 		//Given
 		key = null;
 		
  		//Then
        remoteShellFactory.createRemoteShell(userName, key, host, port);
  	}	
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryByUsernameKeyNullHostPort()
    throws Exception
  	{
 		//Given
 		host = null;
 		
  		//Then
        remoteShellFactory.createRemoteShell(userName, key, host, port);
  	}
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryBySocketfactoryNullUsernameKeyHostPort()
    throws Exception
  	{
 		//Given
 		userName = null;
		
  		//Then
  		remoteShellFactory.createRemoteShell(socketFactory, userName, key, host, port);
  	}
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryBySocketfactoryUsernameNullKeyHostPort()
    throws Exception
  	{
 		//Given
 		key = null;
		
  		//Then
  		remoteShellFactory.createRemoteShell(socketFactory, userName, key, host, port);
  	}
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryBySocketfactoryUsernameKeyNullHostPort()
    throws Exception
  	{
 		//Given
 		host = null;
		
  		//Then
  		remoteShellFactory.createRemoteShell(socketFactory, userName, key, host, port);
  	}	
  	
 	@Test(expected = UnsupportedOperationException.class)
    public void testCreateRemoteShellFactoryByConfigBuilderUsernamePasswdHostPort()
    throws Exception
  	{
  		//Then
        remoteShellFactory.createRemoteShell(configBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval);
  	}
 	
 	@Test(expected = UnsupportedOperationException.class)
    public void testCreateRemoteShellFactoryByConfigBuilderUsernameKeyHostPort()
    throws Exception
  	{
  		//Then
        remoteShellFactory.createRemoteShell(configBuilder, userName, key, host, port, reconnectRetries, reconnectInterval);
  	}
}
