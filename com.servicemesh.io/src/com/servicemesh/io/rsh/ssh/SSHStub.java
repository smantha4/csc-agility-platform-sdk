package com.servicemesh.io.rsh.ssh;

import com.servicemesh.io.rsh.RemoteShell;

import java.io.OutputStream;

/*
 * This is a stub that can be used by callers wishing to do async ssh but wait until the operation
 * is complete.  A user might do something like this:
 * 
 * 	   SSHStub stub = new SSHStub();
 *     int exitStatus = ssh.exec(cmd, stdout, stderr,timeout, passwd, stub);
 *     synchronized(stub) {
 *        while (!stub.isReady())
 *        	 stub.wait();
 *     }
 */

public class SSHStub implements RemoteShell.CompletionHandler {
	
	private boolean _ready = false;
	private int _exitStatus;
	
	public SSHStub()
	{
		_ready = false;
	}
	
	public boolean isReady() { return _ready; }
	public int getExitStatus() { return _exitStatus; }
	
	
	@Override
    public <T extends OutputStream> void exitStatus(int estatus, T os, T es) throws Exception 
    {
		synchronized(this)
		{
			_ready = true;
			_exitStatus = estatus;
			this.notifyAll();
		}
    }
    
    public <T extends OutputStream> void timerExpired(T os, T es)
    {
    	synchronized(this)
		{
    		_ready = true;
    		_exitStatus = -1;
			this.notifyAll();
		}
    }
}
