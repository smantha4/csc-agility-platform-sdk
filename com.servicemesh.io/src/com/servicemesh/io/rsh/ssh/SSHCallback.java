package com.servicemesh.io.rsh.ssh;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbright.ssh2.SSH2ConsoleRemote;
import com.mindbright.ssh2.SSH2Connection;
import com.servicemesh.io.rsh.RemoteShell;
import com.servicemesh.nio.ExecResponseHandler;
import com.mindbright.nio.TimerCallback;

public class SSHCallback  implements ExecResponseHandler, TimerCallback {
	
	final static Logger logger = Logger.getLogger(SSH.class);
	private List<RemoteShell.CompletionHandler> _callbacks = new ArrayList<RemoteShell.CompletionHandler>();
	private String _cmd;
	private ByteArrayOutputStream _stdout;
	private ByteArrayOutputStream _stderr;
	private SSH2ConsoleRemote _console;
	private Object _timerKey = null;
	
	
	public SSHCallback(String cmd, ByteArrayOutputStream stdout, ByteArrayOutputStream stderr, SSH2ConsoleRemote console) { 
		_cmd = cmd;
		_stdout = stdout;
		_stderr = stderr;
		_console = console;
		}
	
	public void registerCallback(RemoteShell.CompletionHandler handler)
	{
		_callbacks.add(handler);
	}
	
	@Override
	public SSH2ConsoleRemote getConsole() { return _console; }
	
	@Override
	public void exitStatus(int status, SSH2ConsoleRemote console)
	{
		//  Remove timer is one was started to time out the command:
		if (_timerKey != null)
			console.killTimer(_timerKey);
		
		//  Debug output if there's a problem:
		if(status != 0)
        	logger.debug(_cmd + ":" + new String(_stderr.toByteArray()));
		
		//  Close remote console connection - don't wait or a deadlock will occur:
		if (console != null)
			try { console.close(false); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
				
		//  Callback any interested parties:
		for (RemoteShell.CompletionHandler handler : _callbacks)
			try {
				handler.exitStatus(status, _stdout, _stderr);
			} catch (Exception ignored) {}
	}
	
	@Override
	public void setTimerKey(Object timerKey) { _timerKey = timerKey; }
	
	@Override
	public void timerTrig()
	{
		//  Remove timer is one was started to time out the command:
		if (_timerKey != null)
			_console.killTimer(_timerKey);
		
		//  Close remote console connection - don't wait or a deadlock will occur:
		if (_console != null)
			try { _console.close(false); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
			
		//  Callback any interested parties - send a timer expired message:
		for (RemoteShell.CompletionHandler handler : _callbacks)
		{
			handler.timerExpired(_stdout, _stderr);
		}
	}

}
