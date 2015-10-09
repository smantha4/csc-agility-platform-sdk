package com.servicemesh.io.rsh.mockshell;

import org.apache.log4j.Logger;

import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.rsh.CreateException;
import com.servicemesh.io.rsh.RemoteShell;
import com.servicemesh.io.rsh.RemoteShell.SocketFactory;
import com.servicemesh.io.rsh.RemoteShellFactory;

public class MockShellFactory extends RemoteShellFactory 
{
	private final static Logger logger = Logger.getLogger(MockShellFactory.class);
    protected final static int DEFAULT_PORT = 22;	
    private static final MockShellFactory instance;
    
    static 
    {
        try 
        {
            instance = new MockShellFactory();
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Unable to create MockShellFactory singleton!", e);
        }
    }
    
    private MockShellFactory()
    {
    }
    
    public static MockShellFactory getInstance() 
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
	    validateCreateRemoteShellArgs(userName, passwd, host, port);
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;

		try
		{
			remoteShell = new MockShell(userName, passwd, host, resolvePort(port));
		}
		catch(Exception e) 
		{
			throw new CreateException(e);
		}			  

		logTraceMessage("Done creating remote shell!");
		return remoteShell;
	}

	@Override
	public RemoteShell createRemoteShell(SocketFactory socketFactory, String userName, String passwd, String host, int port)
    throws CreateException
	{
	    validateCreateRemoteShellArgs(socketFactory, userName, passwd, host, port);
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;

		try
		{
			remoteShell = new MockShell(socketFactory,userName,passwd,host,resolvePort(port));
		}
		catch(Exception e) 
		{
			throw new CreateException(e);
		}			  

		logTraceMessage("Done creating remote shell!");
		return remoteShell;	
	}

	@Override
	public RemoteShell createRemoteShell(String userName, byte[] key, String host, int port)
    throws CreateException
	{
		validateCreateRemoteShellArgs(userName, key, host, port);
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;

		try
		{
			remoteShell = new MockShell(userName, key, host, resolvePort(port));
		}
		catch(Exception e) 
		{
			throw new CreateException(e);
		}			  

		logTraceMessage("Done creating remote shell!");
		return remoteShell;
	}

	@Override
	public RemoteShell createRemoteShell(SocketFactory socketFactory, String userName, byte[] key, String host, int port)
    throws CreateException 
	{
	    validateCreateRemoteShellArgs(socketFactory, userName, key, host, port);
		logTraceMessage("Creating remote shell...");
		RemoteShell remoteShell = null;

		try
		{
			remoteShell = new MockShell(socketFactory, userName, key, host, resolvePort(port));
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
