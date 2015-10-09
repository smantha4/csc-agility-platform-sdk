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

import org.apache.log4j.Logger;

import com.servicemesh.io.rsh.RemoteShell.Protocol;
import com.servicemesh.io.rsh.mockshell.MockShellFactory;
import com.servicemesh.io.rsh.ssh.SSHFactory;
import com.servicemesh.io.rsh.winrm.WinRMFactory;
import com.servicemesh.io.rsh.winrm.WinRMSecureFactory;

public final class RemoteShellFactoryHelper
{
	private final static Logger logger = Logger.getLogger(RemoteShellFactoryHelper.class);
	
    public enum CredentialType 
    {
    	USER_PASSWORD,
		SSH,
		CERTIFICATE,
		ENCRYPT_DECRYPT,
		CERTIFICATE_PRIVATE_KEY;
    }
	
    public enum AddressType
    {
    	LOCALHOST,
    	REMOTEHOST;
    }
    
	public static IRemoteShellFactory createRemoteShellFactory(final AddressType addressType)
	{
		if (addressType == null) 
		{
			throw new IllegalArgumentException("AddressType cannot be null.");
		}
		
        return createRemoteShellFactory(addressType, null, null);
	}    
    
    public static IRemoteShellFactory createRemoteShellFactory(final CredentialType credentialType)
	{
		if (credentialType == null) 
		{
			throw new IllegalArgumentException("CredentialType cannot be null.");
		}
		
        return createRemoteShellFactory(null, credentialType, null);
	}
	
	public static IRemoteShellFactory createRemoteShellFactory(final Protocol protocol)
	{
		if (protocol == null) 
		{
			throw new IllegalArgumentException("Protocol cannot be null.");
		}
		
        return createRemoteShellFactory(null, null, protocol);
	}
	
	public static IRemoteShellFactory createRemoteShellFactory(final AddressType addressType, final CredentialType credentialType, final Protocol protocol)
	{
		logger.trace("Creating remote shell factory...");
		
		if (addressType == null && credentialType == null && protocol == null) 
		{
			throw new IllegalArgumentException("At least one of [addressType|credentialType|protocol] cannot be null.");
		}		
		
		IRemoteShellFactory remoteShellFactory = null;
		
	    if (addressType != null && addressType.equals(AddressType.LOCALHOST)) 
	    {
            remoteShellFactory = MockShellFactory.getInstance();
	    }
	    else if (credentialType != null && credentialType.equals(CredentialType.SSH)) 
	    {
			remoteShellFactory = SSHFactory.getInstance();
	    }
	    else if (protocol != null)
	    {
	    	switch(protocol)
	    	{
		    	case WINRM_HTTP :
		    		remoteShellFactory = WinRMFactory.getInstance();
		    		break;
		    	case WINRM_HTTPS :
		    		remoteShellFactory = WinRMSecureFactory.getInstance();
		    		break;
		    	case SSH : 
		    	default :
		    		remoteShellFactory = SSHFactory.getInstance();
	    	}
	    }
		
		logger.trace("Done creating remote shell factory!");
		return remoteShellFactory;
	}
}
