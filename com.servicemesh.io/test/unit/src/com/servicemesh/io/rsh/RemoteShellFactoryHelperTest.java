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

package com.servicemesh.io.rsh;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.servicemesh.io.rsh.RemoteShell.Protocol;
import com.servicemesh.io.rsh.RemoteShellFactoryHelper.AddressType;
import com.servicemesh.io.rsh.RemoteShellFactoryHelper.CredentialType;
import com.servicemesh.io.rsh.mockshell.MockShellFactory;
import com.servicemesh.io.rsh.ssh.SSHFactory;
import com.servicemesh.io.rsh.winrm.WinRMFactory;
import com.servicemesh.io.rsh.winrm.WinRMSecureFactory;

@RunWith(JUnit4.class)
public class RemoteShellFactoryHelperTest 
{
	private IRemoteShellFactory remoteShellFactory;
	private AddressType addressType;
    private	CredentialType credentialType;
    private Protocol protocol;

	//---------------
	//Positive Tests
	//---------------    
	
	@Test
    public void testCreateRemoteShellFactoryByAddressType() throws Exception
	{
		//Given
		addressType = AddressType.LOCALHOST;
		
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(addressType);

		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof MockShellFactory);
	}	
	
	@Test
    public void testCreateRemoteShellFactoryByCredentialType() throws Exception
	{
		//Given
        credentialType = CredentialType.SSH;
		
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(credentialType);

		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof SSHFactory);
	}		
	
	@Test
    public void testCreateRemoteShellFactoryByProtocolWinRm() throws Exception
	{
		//Given
        protocol = Protocol.WINRM_HTTP;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(protocol);

		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof WinRMFactory);
	}	
	
	@Test
    public void testCreateRemoteShellFactoryByProtocolWinRmSecure() throws Exception
	{
		//Given
        protocol = Protocol.WINRM_HTTPS;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(protocol);

		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof WinRMSecureFactory);
	}		
	
	@Test
    public void testCreateRemoteShellFactoryByProtocolSsh() throws Exception
	{
		//Given
        protocol = Protocol.SSH;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(protocol);

		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof SSHFactory);
	}
	
	@Test
    public void testCreateRemoteShellFactoryWithAddressTypeNullCredentialTypeNullProtocol() throws Exception
	{
		//Given
		addressType = AddressType.LOCALHOST;
        credentialType = null;
        protocol = null;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(addressType, credentialType, protocol);
		
		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof MockShellFactory);		
	}		
	
	@Test
    public void testCreateRemoteShellFactoryWithNullAddressTypeCredentialTypeNullProtocol() throws Exception
	{
		//Given
		addressType = null;
        credentialType = CredentialType.SSH;
        protocol = null;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(addressType, credentialType, protocol);
		
		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof SSHFactory);		
	}		
	
	@Test
    public void testCreateRemoteShellFactoryWithNullAddressTypeNullCredentialTypeProtocolWinRm() throws Exception
	{
		//Given
		addressType = null;
        credentialType = null;
        protocol = Protocol.WINRM_HTTP;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(addressType, credentialType, protocol);

		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof WinRMFactory);
	}	
	
	@Test
    public void testCreateRemoteShellFactoryWithNullAddressTypeNullCredentialTypeProtocolWinRmSecure() throws Exception
	{
		//Given
		addressType = null;
        credentialType = null;
        protocol = Protocol.WINRM_HTTPS;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(addressType, credentialType, protocol);

		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof WinRMSecureFactory);
	}		
	
	@Test
    public void testCreateRemoteShellFactoryWithNullAddressTypeNullCredentialTypeProtocolSsh() throws Exception
	{
		//Given
		addressType = null;
        credentialType = null;
        protocol = Protocol.SSH;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(addressType, credentialType, protocol);
		
		assertNotNull(remoteShellFactory);
		assertTrue(remoteShellFactory instanceof SSHFactory);		
	}		
	
	//---------------
	//Negative Tests
	//---------------
    
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryWithNullAddressType() throws Exception
	{
		//Given
		addressType = null;
		
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(addressType);
	}	
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryWithCredentialType() throws Exception
	{
		//Given
        credentialType = null;
		
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(credentialType);
	}		
	
	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryWithNullProtocol() throws Exception
	{
		//Given
        protocol = null;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(protocol);
	}	

	@Test(expected = IllegalArgumentException.class)
    public void testCreateRemoteShellFactoryWithNullAddressTypeNullCredentialTypeNullProtocol() throws Exception
	{
		//Given
		addressType = null;
        credentialType = null;
        protocol = null;
       
		//Then
		remoteShellFactory = RemoteShellFactoryHelper.createRemoteShellFactory(addressType, credentialType, protocol);
	}	
}
