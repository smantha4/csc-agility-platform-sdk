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

package com.servicemesh.io.rsh.ssh;

import org.apache.log4j.Logger;

import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.rsh.CreateException;
import com.servicemesh.io.rsh.RemoteShell;
import com.servicemesh.io.rsh.RemoteShell.SocketFactory;
import com.servicemesh.io.rsh.RemoteShellFactory;

public class SSHFactory extends RemoteShellFactory
{
	private final static Logger logger = Logger.getLogger(SSHFactory.class);
    protected final static int DEFAULT_PORT = 22;	
    private static final SSHFactory instance;
    
    static 
    {
        try 
        {
            instance = new SSHFactory();
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Unable to create SSHFactory singleton!", e);
        }
    }
    
    private SSHFactory()
    {
    }
    
    public static SSHFactory getInstance() 
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
	public RemoteShell createRemoteShell(final String userName, final String passwd, final String host, final int port)
	throws CreateException
	{
	    validateCreateRemoteShellArgs(userName, passwd, host, port);
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;
		
		try
		{
            remoteShell = new SSH(userName, passwd, host, resolvePort(port)); 
		}
		catch(Exception e) 
		{
			throw new CreateException(e);
		}	
		
		logTraceMessage("Done creating remote shell!");
		return remoteShell;
	}	
	
	@Override
	public RemoteShell createRemoteShell(final SocketFactory socketFactory, final String userName, final String passwd, final String host, final int port)
    throws CreateException
	{
	    validateCreateRemoteShellArgs(socketFactory, userName, passwd, host, port);
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;
		
		try
		{
            remoteShell = new SSH(socketFactory, userName, passwd, host, resolvePort(port)); 
		}
		catch(Exception e) 
		{
			throw new CreateException(e);
		}			
		
		logTraceMessage("Done creating remote shell!");
		return remoteShell;
	}

	@Override
	public RemoteShell createRemoteShell(final String userName, final byte[] key, final String host, final int port)
    throws CreateException
	{
	    validateCreateRemoteShellArgs(userName, key, host, port);
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;
				
		try
		{
            remoteShell = new SSH(userName, key, host, resolvePort(port)); 
		}
		catch(Exception e) 
		{
			throw new CreateException(e);
		}
	
		logTraceMessage("Done creating remote shell!");
		return remoteShell;
	}

	@Override
	public RemoteShell createRemoteShell(final SocketFactory socketFactory, final String userName, final byte[] key, final String host, final int port)
    throws CreateException
	{
	    validateCreateRemoteShellArgs(socketFactory, userName, key, host, port);
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;
		
		try
		{
            remoteShell = new SSH(socketFactory, userName, key, host, resolvePort(port)); 
		}
		catch(Exception e) 
		{
			throw new CreateException(e);
		}		
		
		logTraceMessage("Done creating remote shell!");
		return remoteShell;
	}

	@Override
	public RemoteShell createRemoteShell(IHttpClientConfigBuilder configBuilder, String userName, String passwd, String host, int port, int reconnectRetries, int reconnectInterval)
    throws CreateException 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public RemoteShell createRemoteShell(IHttpClientConfigBuilder configBuilder, String userName, String passwd, String host, int port, int reconnectRetries, int reconnectInterval, boolean tls) 
    throws CreateException
	{
		throw new UnsupportedOperationException();
	}

}
