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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.mindbright.nio.NetworkConnection;


/**
 * Potential common base class for SSH & WinRM
 *
 */
public abstract class RemoteShell
{
	final public static int CONNECT_TIMEOUT = 30000;
	final public static int DEFAULT_TIMEOUT = 1800000;
	final public static long AUTH_TIMEOUT = 60000;  //  Authorization timeout in msec
	final public static int POLL_INTERVAL = 1000;
	
	protected final static byte[] NEWLINE = "\n".getBytes();
    protected SocketFactory _factory;
	protected String _userName;
	protected String _passwd;
	protected String _host;
	protected byte[] _key;
	protected int _port;
    
	public enum Protocol { 
		SSH("ssh"), 
		WINRM_HTTPS("winrm-https"), 
		WINRM_HTTP("winrm-http");
		
		private final String value;
 
	    Protocol(final String value) {
	        this.value = value;
	    }
 
	    public String toString() { return value; }
	    public String value() { return value; }
	 
	    public static Protocol fromValue(final String value) {
	        for (Protocol p : Protocol.values()) {
	            if (p.value().equalsIgnoreCase(value)) {
	                return p;
	            }
	        }
	        return null;
	    } 
	}
	
    public static interface SocketFactory {
        public Socket createSocket(String host, int port, long timeout) throws IOException, UnknownHostException;
        public NetworkConnection createNetworkConnection(String host, int port, long timeout) throws IOException, UnknownHostException;
    }

    public static interface Callback {
    	public <T extends OutputStream> void update(T stdout, T stderr) throws Exception;
    }
    
    public static interface CompletionHandler {
    	public <T extends OutputStream> void exitStatus(int status, T stdout, T stderr) throws Exception;
    	public <T extends OutputStream> void timerExpired(T stdout, T stderr);
    	}

    
    public RemoteShell(SocketFactory factory, String userName, String passwd, String host, int port) throws Exception
    {
    	_factory = factory;
    	_userName = userName;
    	_passwd = passwd;
    	_key = null;
    	_host = host;
    	_port = port;
    }
    
    public SocketFactory getFactory() { return _factory; }
    public String getUserName() { return _userName; }
    public String getPassword() { return _passwd; }
    public String getHost() { return _host; }
    public byte[] getKey() { return _key; }
    public int getPort() { return _port; }
    public abstract Protocol getProtocol();
    public void setHost(String host) { _host = host; }
    
    public boolean attemptConnect() throws Exception
    {
        return true;	
    }

    public abstract boolean isConnected();
    
    public abstract boolean reconnect();
        
    public int exec(String cmd)
    {
    	return exec(cmd, DEFAULT_TIMEOUT);
    }
  
 	public int exec(String cmd, StringBuilder stdout, StringBuilder stderr)
	{
	    return exec(cmd,stdout,stderr,DEFAULT_TIMEOUT);
	}

 	public int exec(String cmd, OutputStream stdout, OutputStream stderr)
	{
	    return exec(cmd,stdout,stderr,DEFAULT_TIMEOUT);
	}
 	
	public int exec(String cmd, String stdin, StringBuilder stdout, StringBuilder stderr)
	{
	    return exec(cmd,stdin,stdout,stderr,DEFAULT_TIMEOUT);
	}

	/**
	 * Execute the specified command within the specified timeout.
	 *  
	 * @param cmd  The command to execute.
	 * @param timeout  The timeout in milliseconds.
	 * 
	 * @return  The shell's return status after executing the command.
	 */
	public abstract int exec(String cmd, long timeout);

	public abstract int exec(String cmd, StringBuilder stdout, StringBuilder stderr, long timeout);
	
	public abstract int exec(String cmd, String stdin, StringBuilder stdout, StringBuilder stderr, long timeout);
	
	public abstract int exec(String cmd, OutputStream stdout, OutputStream stderr, long timeout);

	public abstract int exec(String cmd, ByteArrayOutputStream stdout, ByteArrayOutputStream stderr, long timeout, Callback cb, String passwd) throws Exception;
	public abstract int exec(String cmd, ByteArrayOutputStream stdout, ByteArrayOutputStream stderr, long timeout, String passwd, CompletionHandler handler) throws Exception;
	
	public abstract boolean copy(String lfile, String rfile);

	public abstract boolean copy(InputStream is, long size, String rfile);
	
	public abstract boolean copy(InputStream is, String mode, long size, String rfile);
	
	public abstract boolean copy(String rfile, OutputStream os);

	public abstract void close();

	// LVB - check if this is needed for Win - same as copy()
	public boolean rcopy(String user, String passwd, String host, String src, String dst)
	{
	    return rcopy(user, passwd, host, src, dst, DEFAULT_TIMEOUT);
	}

	public abstract boolean rcopy(String user, String passwd, String host, String src, String dst, long timeout); // LVB - check if this is needed for Win - same as copy()

	// LVB - check if this is needed for Win - same as copy()
	public int sudo(String cmd, String passwd)
	{
	    return sudo(cmd, passwd, DEFAULT_TIMEOUT);
	}

	public abstract int sudo(String cmd, String passwd, long timeout); // LVB - check if this is needed for Win, close to execute()

	public abstract int resetPassword(String old_pw, String new_pw);
	
}
