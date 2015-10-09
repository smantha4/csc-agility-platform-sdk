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

import org.apache.commons.lang.StringUtils;

import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.rsh.RemoteShell.SocketFactory;

public abstract class RemoteShellFactory
implements IRemoteShellFactory 
{
	protected abstract void logTraceMessage(String message);
	protected abstract int getDefaultPort();

	private void validateString(String target, String targetName)
	{
		if (StringUtils.isEmpty(target)) 
		{
			throw new IllegalArgumentException(targetName + " cannot be empty.");
		}
	}
	
	private void validateSocketFactory(SocketFactory socketFactory)
	{
		//Currently it is okay for the socketFactory to be null
		//SSH will create a NetworkConnection if it is null
	}
	
	private void validateKey(byte[] key)
	{
		if (key == null || key.length == 0)
		{
			throw new IllegalArgumentException("key cannot be empty.");
		}
	}	

	private void validateConfigBuilder(IHttpClientConfigBuilder configBuilder)
	{
		if (configBuilder == null)
		{
			throw new IllegalArgumentException("configBuilder cannot be null.");
		}
	}	
	
	protected int resolvePort(int port)
	{
		logTraceMessage("Resolving port...");
		
		int resolvedPort = port;
		
        if (port <= 0)
        {
        	resolvedPort = getDefaultPort(); 
        }
		
		logTraceMessage("Done resolving port!");
		return resolvedPort;
	}
	
	protected void validateCreateRemoteShellArgs(String userName, String passwd, String host, int port)
	{
		logTraceMessage("Validating createRemoteShell args...");
		validateString(userName, "userName");
		validateString(passwd, "passwd");
		validateString(host, "host");
		logTraceMessage("Done validating createRemoteShell args!");
	}
	
	protected void validateCreateRemoteShellArgs(SocketFactory socketFactory, String userName, String passwd, String host, int port)
	{
		logTraceMessage("Validating createRemoteShell args...");
		validateSocketFactory(socketFactory);
		validateString(userName, "userName");
		validateString(passwd, "passwd");
		validateString(host, "host");
		logTraceMessage("Done validating createRemoteShell args!");
	}
	
	protected void validateCreateRemoteShellArgs(String userName, byte[] key, String host, int port)
	{
		logTraceMessage("Validating createRemoteShell args...");
		validateString(userName, "userName");
		validateKey(key);
		validateString(host, "host");
		logTraceMessage("Done validating createRemoteShell args!");		
	}
	
	protected void validateCreateRemoteShellArgs(SocketFactory socketFactory, String userName, byte[] key, String host, int port)
	{
		logTraceMessage("Validating createRemoteShell args...");
		validateSocketFactory(socketFactory);
		validateString(userName, "userName");
		validateKey(key);
		validateString(host, "host");
		logTraceMessage("Done validating createRemoteShell args!");			
	}
	
	protected void validateCreateRemoteShellArgs(IHttpClientConfigBuilder configBuilder, String userName, String passwd, String host, int port, int reconnectRetries, int reconnectInterval)
	{
		logTraceMessage("Validating createRemoteShell args...");
		validateConfigBuilder(configBuilder);
		validateString(userName, "userName");
		validateString(passwd, "passwd");
		validateString(host, "host");
		logTraceMessage("Done validating createRemoteShell args!");
	}
	
	protected void validateCreateRemoteShellArgs(IHttpClientConfigBuilder configBuilder, String userName, String passwd, String host, int port, int reconnectRetries, int reconnectInterval, boolean tls)
	{
		logTraceMessage("Validating createRemoteShell args...");
		validateConfigBuilder(configBuilder);
		validateString(userName, "userName");
		validateString(passwd, "passwd");
		validateString(host, "host");
		logTraceMessage("Done validating createRemoteShell args!");		
	}
}
