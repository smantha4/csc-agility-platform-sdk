package com.servicemesh.io.rsh.mockshell;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.servicemesh.io.rsh.RemoteShell;


public class MockShell extends RemoteShell
{
	   public MockShell(String userName, byte[] key, String host, int port) throws Exception
	    {
	    	this(null,userName,key,host,port);
	    }

	    public MockShell(String userName, String passwd, String host, int port) throws Exception
	    {
	    	this(null,userName,passwd,host,port);
	    }

	    public MockShell(SocketFactory socketFactory, String userName, byte[] key, String host, int port) throws Exception
	    {
	    	super(socketFactory,userName,null,host,port);
	    	_key = key;
	    }

	    public MockShell(SocketFactory socketFactory, String userName, String passwd, String host, int port) throws Exception
	    {
	    	super(socketFactory,userName,passwd,host,port);
	    	_key = null;
	    }

	@Override
	public Protocol getProtocol() {
		return Protocol.SSH;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public boolean reconnect() {
		return true;
	}

	@Override
	public int exec(String cmd, long timeout) {
		return 0;
	}

	@Override
	public int exec(String cmd, StringBuilder stdout, StringBuilder stderr, long timeout) {
		return 0;
	}

	@Override
	public int exec(String cmd, String stdin, StringBuilder stdout,
			StringBuilder stderr, long timeout) {
		return 0;
	}

	@Override
	public int exec(String cmd, OutputStream stdout, OutputStream stderr,
			long timeout) {
		return 0;
	}

	@Override
	public int exec(String cmd, ByteArrayOutputStream stdout,
			ByteArrayOutputStream stderr, long timeout, Callback cb,
			String passwd) throws Exception {
		//  Probably need to call the cb...
		return 0;
	}

	@Override
	public int exec(String cmd, ByteArrayOutputStream stdout,
			ByteArrayOutputStream stderr, long timeout, String passwd,
			CompletionHandler handler) throws Exception {
		handler.exitStatus(0, stdout, stderr);
		return 0;
	}

	@Override
	public boolean copy(String lfile, String rfile) {
		return true;
	}

	@Override
	public boolean copy(InputStream is, long size, String rfile) {
		return true;
	}

	@Override
	public boolean copy(InputStream is, String mode, long size, String rfile) {
		return true;
	}

	@Override
	public boolean copy(String rfile, OutputStream os) {
		return true;
	}

	@Override
	public void close() {
	}

	@Override
	public boolean rcopy(String user, String passwd, String host, String src,
			String dst, long timeout) {
		return true;
	}

	@Override
	public int sudo(String cmd, String passwd, long timeout) {
		return 0;
	}

	@Override
	public int resetPassword(String old_pw, String new_pw) {
		return 0;
	}

}
