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

import org.apache.log4j.Logger;

import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.rsh.CreateException;
import com.servicemesh.io.rsh.RemoteShell;
import com.servicemesh.io.rsh.RemoteShell.SocketFactory;
import com.servicemesh.io.rsh.RemoteShellFactory;

public class WinRMFactory extends RemoteShellFactory 
{
	private final static Logger logger = Logger.getLogger(WinRMFactory.class);
    protected final static int DEFAULT_PORT = 5985;	
    private static final WinRMFactory instance;
    
    static 
    {
        try 
        {
            instance = new WinRMFactory();
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Unable to create WinRMFactory singleton!", e);
        }
    }
    
    protected WinRMFactory()
    {
    }
    
    public static WinRMFactory getInstance() 
    {
        return instance;
    }    
    
    @Override
    protected void logTraceMessage(String message)
    {
        logger.trace(message);    	
    }      
    
    @Override
    protected int getDefaultPort() 
    {
    	return DEFAULT_PORT;
    }
    
	@Override
	public RemoteShell createRemoteShell(String userName, String passwd, String host, int port)
    throws CreateException 
	{
		throw new UnsupportedOperationException();		
	}

	@Override
	public RemoteShell createRemoteShell(SocketFactory socketFactory, String userName, String passwd, String host, int port)
    throws CreateException
	{
		throw new UnsupportedOperationException();		
	}

	@Override
	public RemoteShell createRemoteShell(String userName, byte[] key, String host, int port)
    throws CreateException 
    {
		throw new UnsupportedOperationException();		
	}

	@Override
	public RemoteShell createRemoteShell(SocketFactory socketFactory, String userName, byte[] key, String host, int port)
    throws CreateException
	{
		throw new UnsupportedOperationException();		
	}

	@Override
	public RemoteShell createRemoteShell(final IHttpClientConfigBuilder configBuilder, final String userName, final String passwd, final String host, final int port, final int reconnectRetries, final int reconnectInterval) 
    throws CreateException 
	{
		validateCreateRemoteShellArgs(configBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval); 
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;

		try
		{
			remoteShell = new WinRM(configBuilder, userName, passwd, host, resolvePort(port), reconnectRetries, reconnectInterval);
		}
		catch(Exception e) 
		{
			throw new CreateException(e);
		}			  

		logTraceMessage("Done creating remote shell!");
		return remoteShell;				
	}

	@Override
	public RemoteShell createRemoteShell(final IHttpClientConfigBuilder configBuilder, final String userName, final String passwd, final String host, final int port, final int reconnectRetries, final int reconnectInterval, final boolean tls) 
    throws CreateException
	{
		validateCreateRemoteShellArgs(configBuilder, userName, passwd, host, port, reconnectRetries, reconnectInterval, tls); 
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;

		try
		{
			remoteShell = new WinRM(configBuilder, userName, passwd, host, resolvePort(port), tls, reconnectRetries, reconnectInterval);
		}
		catch(Exception e) 
		{
			throw new CreateException(e);
		}			  

		logTraceMessage("Done creating remote shell!");
		return remoteShell;				
	}
	
}
