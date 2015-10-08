/**
 *              COPYRIGHT (C) 2008-2014 SERVICEMESH, INC.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.servicemesh.io.util.Crypt;
import com.mindbright.nio.NetworkConnection;
import com.mindbright.ssh2.SSH2AuthKbdInteract;
import com.mindbright.ssh2.SSH2AuthPassword;
import com.mindbright.ssh2.SSH2AuthPublicKey;
import com.mindbright.ssh2.SSH2Authenticator;
import com.mindbright.ssh2.SSH2ConsoleRemote;
import com.mindbright.ssh2.SSH2KeyPairFile;
import com.mindbright.ssh2.SSH2Preferences;
import com.mindbright.ssh2.SSH2SCP1Client;
import com.mindbright.ssh2.SSH2Signature;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.sshcommon.TimeoutException;
import com.mindbright.util.SecureRandomAndPad;
import com.servicemesh.io.rsh.ConnectException;
import com.servicemesh.io.rsh.RemoteShell;

public class SSH extends RemoteShell
{	
	final static Logger logger = Logger.getLogger(SSH.class);
	private final static byte[] NEWLINE = "\n".getBytes();
	
    private SSH2SimpleClient _client;
    private SSH2Authenticator _auth;

    private static SSH2Preferences CONFIG = new SSH2Preferences();
    static
    {
    	CONFIG.setPreference(SSH2Preferences.ALIVE, "30");

    	if (logger.isDebugEnabled()) {
    	    CONFIG.setPreference(SSH2Preferences.LOG_LEVEL, "" + com.mindbright.util.Log.LEVEL_DEBUG);
    	} else if (logger.isInfoEnabled()) {
    	    CONFIG.setPreference(SSH2Preferences.LOG_LEVEL, "" + com.mindbright.util.Log.LEVEL_INFO);
    	} else if (logger.isEnabledFor(Level.WARN)) {
    	    CONFIG.setPreference(SSH2Preferences.LOG_LEVEL, "" + com.mindbright.util.Log.LEVEL_WARNING);
    	} else {
    	    CONFIG.setPreference(SSH2Preferences.LOG_LEVEL, "" + com.mindbright.util.Log.LEVEL_ERROR);
    	}
    }

    public SSH(String userName, byte[] key, String host, int port) throws Exception
    {
    	this(null,userName,key,host,port);
    }

    public SSH(String userName, String passwd, String host, int port) throws Exception
    {
    	this(null,userName,passwd,host,port);
    }

    public SSH(SocketFactory socketFactory, String userName, byte[] key, String host, int port) throws Exception
    {
    	super(socketFactory,userName,null,host,port);
    	_key = key;
    }

    public SSH(SocketFactory socketFactory, String userName, String passwd, String host, int port) throws Exception
    {
    	super(socketFactory,userName,passwd,host,port);
    	_key = null;
    }
    
    public Protocol getProtocol() { return Protocol.SSH; }

    private NetworkConnection openConnection()
        throws IOException
    {
        return (_factory != null) ? _factory.createNetworkConnection(_host, _port, CONNECT_TIMEOUT) : NetworkConnection.open(_host, _port);
    }
    
    @Override
    public boolean attemptConnect() throws ConnectException
    {
        NetworkConnection connection = null;
        SSH2Transport transport = null;

        try {
            if ((_passwd != null) && !(_passwd.trim().isEmpty())) {
                String encrypted = Crypt.encryptString(_passwd);

                connection = openConnection();
                transport = new SSH2Transport(connection, CONFIG, createSecureRandom());
                _auth = new SSH2Authenticator(_userName);
                _auth.addModule(new SSH2AuthPassword(encrypted));
                _auth.addModule(new SSH2AuthKbdInteract(encrypted));
                _client = new SSH2SimpleClient(transport, _auth, AUTH_TIMEOUT);        
            } else if ((_key != null) && (_key.length > 0)) {
                SSH2KeyPairFile kpf = new SSH2KeyPairFile();
                byte[] keyWork = new byte[_key.length + NEWLINE.length];

                connection = openConnection();
                transport = new SSH2Transport(connection, CONFIG, createSecureRandom());

                // Make sure we have a newline at the end
                System.arraycopy(_key, 0, keyWork, 0, _key.length);
                System.arraycopy(NEWLINE, 0, keyWork, _key.length, NEWLINE.length);
                kpf.load(new ByteArrayInputStream(keyWork),null);

                String alg  = kpf.getAlgorithmName();
                SSH2Signature sign = SSH2Signature.getInstance(alg);
                sign.initSign(kpf.getKeyPair().getPrivate());
                sign.setPublicKey(kpf.getKeyPair().getPublic());
                
                _auth = new SSH2Authenticator(_userName);
                _auth.addModule(new SSH2AuthPublicKey(sign));        
                _client = new SSH2SimpleClient(transport, _auth, AUTH_TIMEOUT);
            } else {
                throw new IllegalArgumentException("Empty password and empty key");
            }
        } catch (Exception ex) {
        	logger.error(ex.toString());
        	
            if (transport != null) {
                try {
                    transport.normalDisconnect("User disconnect");
                } catch (Exception ex2) {
                    logger.warn("transport normal disconnect exception");
                }
            } else if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex2) {
                    logger.warn("connection close exception");
                }
            }

            throw new ConnectException(ex);
        }
        
        return isConnected();
    }
    
    @Override
    public boolean isConnected()
    {
    	if (_client.getTransport() != null)
    		return _client.getTransport().isConnected();
    	else
    		return false;
    }
    
    @Override
    public boolean reconnect()
    {
    	logger.debug("SSH reconnect - old connected state was: " + isConnected());
		try
		{
	        int retries = 15;
	        while (retries > 0)
	        {
	        	try
	        	{
	        		close();
	        		attemptConnect();
	        		return true;
	        	}
	        	catch (Exception ex)
	        	{
	        		logger.error("Error reconnecting ssh connection: " + ex.getMessage());
	        	}
	        	Thread.sleep(30000);
	        	--retries;
	        }
	        return false;
		}
		catch (Exception ex)
		{
			logger.error("Error reconnecting ssh connection: " + ex.getMessage());
			return false;
		}
    }

    /**
     * Execute the specified command within the specified timeout.
     *  
     * @param cmd  The command to execute.
     * @param timeout  The timeout in milliseconds.
     * 
     * @return  The shell's return status after executing the command.
     */
    @Override
    public int exec(String cmd, long timeout)
	{
	    SSH2ConsoleRemote console = null;
		try{
			ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		    console = new SSH2ConsoleRemote(_client.getConnection(), null, stderr);
		    if(console.command(cmd, true) == false)
		    {
		    	logger.debug("unable to open session: " + cmd);
		    	return -1;
		    }
            int exitStatus = console.waitForExitStatus(timeout);
            if(exitStatus != 0)
            	logger.debug(cmd + ":" + new String(stderr.toByteArray()));
            return exitStatus;
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			return -1;
		}
		finally 
		{
			if (console != null)
				try { console.close(true); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
		}
	}

    @Override
	public int exec(String cmd, StringBuilder stdout, StringBuilder stderr, long timeout)
	{
	    SSH2ConsoleRemote console = null;
		try {
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ByteArrayOutputStream err = new ByteArrayOutputStream();
		    console = new SSH2ConsoleRemote(_client.getConnection(),out,err);
		    if(console.command(cmd, true) == false)
		    {
		    	logger.debug("unable to open session: " + cmd);
		    	return -1;
		    }
            int exitStatus = console.waitForExitStatus(timeout);
            stdout.append(new String(out.toByteArray()));
            stderr.append(new String(err.toByteArray()));
            return exitStatus;
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
			return -1;
		}
		finally 
		{
			if (console != null)
				try { console.close(true); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
		}
	}

	@Override
	public int exec(String cmd, String stdin, StringBuilder stdout, StringBuilder stderr, long timeout)
	{
	    SSH2ConsoleRemote console = null;
		try {
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ByteArrayOutputStream err = new ByteArrayOutputStream();
		    console = new SSH2ConsoleRemote(_client.getConnection(),out,err);
		    if(console.command(cmd, true) == false)
		    {
		    	logger.debug("unable to open session: " + cmd);
		    	return -1;
		    }

            OutputStream in = console.getStdIn();
            in.write(stdin.getBytes());
            in.write("\r".getBytes());
            in.flush();
            
            int exitStatus = console.waitForExitStatus(timeout);
            stdout.append(new String(out.toByteArray()));
            stderr.append(new String(err.toByteArray()));
            return exitStatus;
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			return -1;
		}
		finally 
		{
			if (console != null)
				try { console.close(true); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
		}
	}

	@Override
	public int exec(String cmd, OutputStream stdout, OutputStream stderr)
	{
	    return exec(cmd,stdout,stderr,0);
	}

	@Override
	public int exec(String cmd, OutputStream stdout, OutputStream stderr, long timeout)
	{
	    SSH2ConsoleRemote console = null;
		try {
		    console = new SSH2ConsoleRemote(_client.getConnection(),stdout,stderr);
		    if(console.command(cmd, true) == false)
		    {
		    	logger.debug("unable to open session: " + cmd);
		    	return -1;
		    }
            return console.waitForExitStatus(timeout);
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			return -1;
		}
		finally 
		{
			if (console != null)
				try { console.close(true); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
		}
	}

	
    public int exec(String cmd, ByteArrayOutputStream stdout, ByteArrayOutputStream stderr, long timeout, Callback cb) throws Exception
    {	
		return exec(cmd, stdout, stderr, timeout, cb,null);
    }
    
    @Override
    public int exec(String cmd, ByteArrayOutputStream stdout, ByteArrayOutputStream stderr, long timeout, Callback cb, String passwd) throws Exception
    {
        SSH2ConsoleRemote console = null;
        try {
            int numStdout = 0;
            int numStderr = 0;

		    console = new SSH2ConsoleRemote(_client.getConnection(),stdout,stderr);
            if (console.command(cmd, stdout, stderr, true, "xterm", 40, 80) == false) 
            {
            	logger.debug("unable to open session: " + cmd);
            	return -1;
            }

            // poll periodically to update the status object with stdout/stderr
            long start = Calendar.getInstance().getTimeInMillis();
            timeout *= 1000;
            boolean isInterrupted = false;
            while ((timeout == 0 || Calendar.getInstance().getTimeInMillis() - start < timeout) && console.isFinished() == false && Thread.currentThread().isInterrupted() == false) {
                // is there anything to do
                if (stdout.size() > numStdout || stderr.size() > numStderr) {
                    numStdout = stdout.size();
                    numStderr = stderr.size();
                    cb.update(stdout, stderr);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                	isInterrupted = true;
                    break;
                }
            }
            
            int exitStatus = -1;
            if (isInterrupted || Thread.currentThread().isInterrupted())
                exitStatus = console.waitForExitStatus(1); // return immediately
            else {
                try {
                    long stop = Calendar.getInstance().getTimeInMillis();
                    long duration = stop - start;
                    if (timeout == 0)
                        exitStatus = console.waitForExitStatus(); // wait
                                                                  // indefinitely
                    else if (duration < timeout)
                        exitStatus = console.waitForExitStatus(timeout - duration); // wait
                                                                                    // up
                                                                                    // to
                                                                                    // timeout
                    else
                        exitStatus = console.waitForExitStatus(1); // return
                                                                   // immediately
                } catch (TimeoutException ex) {
                    throw new Exception("timeout waiting for script completion");
                }
            }
            return exitStatus;
        } finally {
            cb.update(stdout, stderr);

            if (console != null)
                try {
                    console.close(true);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
        }
    }
    
    @Override
    public int exec(String cmd, ByteArrayOutputStream stdout, ByteArrayOutputStream stderr, long timeout, String passwd, RemoteShell.CompletionHandler handler) throws Exception
    {
        SSH2ConsoleRemote console = null;
     
           console = new SSH2ConsoleRemote(_client.getConnection(),stdout,stderr);
           SSHCallback callback = new SSHCallback(cmd, stdout, stderr, console);
           callback.registerCallback(handler);
           
           //  Issue call - timeout comes in here as seconds - convert to milliseconds
        if (console.command(cmd, stdout, stderr, true, "xterm", 40, 80, callback, timeout*1000) == false) 
        {
               logger.debug("unable to open session: " + cmd);
               return -1;
        }
        return 0;
    }



	public boolean copy(String lfile, String rfile)
	{
		InputStream is = null;
		try{
		    URL url = Thread.currentThread().getContextClassLoader().getResource(lfile);
		    long size = (new File(url.toURI())).length();
		    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(lfile);
            return copy(is,size,rfile);      			
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		} finally {
		    try{ if(is != null) is.close(); } catch (Exception ex) {}
		}
	}

	public boolean copy(InputStream is, long size, String rfile)
	{
		return copy(is,"0600",size,rfile);		
	}
	
	public boolean copy(InputStream is, String mode, long size, String rfile)
	{
	    ByteArrayOutputStream err = new ByteArrayOutputStream();		    
		SSH2SCP1Client scpClient = null; 
		try		
		{
			scpClient = new SSH2SCP1Client(new File(System.getProperty("user.dir")),
	                                   _client.getConnection(), err, false);
			scpClient.scp1().copyToRemote(is,size,mode,rfile);
		    return true;
		}
		catch(Exception e)
		{
		    logger.error(e.getMessage() + ": " + new String(err.toByteArray()), e);
		    return false;
		}
		finally
		{
		    try{ if(scpClient != null) scpClient.close(); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
		}
	}
	
	public boolean copy(String rfile, OutputStream os)
	{
	    ByteArrayOutputStream err = new ByteArrayOutputStream();		    
		SSH2SCP1Client scpClient = null; 
		try		
		{
			scpClient = new SSH2SCP1Client(new File(System.getProperty("user.dir")),
	                                   _client.getConnection(), err, false);
			scpClient.scp1().copyToLocal(rfile, os);
		    return true;
		}
		catch(Exception e)
		{
		    logger.error(e.getMessage() + ": " + new String(err.toByteArray()));
		    return false;
		}
		finally
		{
		    try{ if(scpClient != null) scpClient.close(); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
		}
	}

	public void close()
	{
		try{
			if (_client.getTransport() != null)
				_client.getTransport().normalDisconnect("User disconnect");
		} catch (Exception ex)
		{
			logger.error(ex.getMessage());
		}
	}

	@Override
	public boolean rcopy(String user, String passwd, String host, String src, String dst, long timeout)
	{
        StringBuilder cmd = new StringBuilder();
        cmd.append("scp -q -o StrictHostKeyChecking=no ");
        cmd.append(user);
        cmd.append("@");
        cmd.append(host);
        cmd.append(":\"\\\"");
        cmd.append(src);
        cmd.append("\\\"\" ");
        cmd.append(dst);
        return sudo(cmd.toString(), passwd, timeout) == 0;
	}

	@Override
	public int sudo(String cmd, String passwd, long timeout)
	{
	    SSH2ConsoleRemote console = null;
		try {
		    console = new SSH2ConsoleRemote(_client.getConnection());
		    if(console.command(cmd, true) == false)
		    {
		    	logger.debug("unable to open session: " + cmd);
		    	return -1;
		    }
		    
            StringBuffer output = new StringBuffer();
            InputStream stdout = console.getStdOut();
            while(output.toString().contains("assword") == false)
            {
                byte buf[] = new byte[1024];
                int cnt = stdout.read(buf, 0, buf.length);
                if(cnt > 0)
                {
                    String str = new String(buf,0,cnt-1);
                    output.append(str);
                }
                if(cnt < 0)
                	break;
            }

            OutputStream stdin = console.getStdIn();
            stdin.write(passwd.getBytes());
            stdin.write("\r".getBytes());
            stdin.flush();
            int status = console.waitForExitStatus(timeout);
            //  See if there's any output - use for troubleshooting:
            String str = readFromStream(stdout);
	        while (str.length() > 0)
	        {
	        	logger.debug("stdout: " + str);
	        	str = readFromStream(stdout);
	        }
            
            return status;
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			return -1;
		}
		finally 
		{
			if (console != null)
				try { console.close(true); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
		}
	}
	
	
	/**
     * Create a random number generator. This implementation uses the
     * system random device if available to generate good random
     * numbers. Otherwise it falls back to some low-entropy garbage.
     */
    private static SecureRandomAndPad createSecureRandom() {
        /*
        byte[] seed;
        File devRandom = new File("/dev/urandom");
        if (devRandom.exists()) {
            RandomSeed rs = new RandomSeed("/dev/urandom", "/dev/urandom");
            seed = rs.getBytesBlocking(20);
        } else {
            seed = RandomSeed.getSystemStateHash();
        }
        return new SecureRandomAndPad(new SecureRandom(seed));
        */
        SecureRandom random = new SecureRandom();
        byte[] seed = random.generateSeed(20);

        return new SecureRandomAndPad(new SecureRandom(seed));
    }
    
    private String readFromStream(InputStream in) throws Exception
	{
		StringBuffer output = new StringBuffer();
		byte buf[] = new byte[1024];
		int tries = 3;
		while (tries > 0)
		{
			if (in.available() > 0)
			{
				int cnt = in.read(buf, 0, buf.length);
				if (cnt > 0)
				{
					String str = new String(buf,0,cnt-1);
					output.append(str);
				}
				tries = 3;
			}
			else
			{
				--tries;
				Thread.sleep(1000);
			}
		}
		return output.toString();
	}
	
	public int resetPassword(String old_pw, String new_pw)
	{
		SSH2ConsoleRemote console = null;
		try
		{
			console = new SSH2ConsoleRemote(_client.getConnection());
			console.shell(true);
			
            InputStream stdout = console.getStdOut();
            OutputStream stdin = console.getStdIn();

			String line = readFromStream(stdout);
	        if (line.contains("(current) UNIX password:") == false)
	        {
	        	//  Probably a normal login - issue password command:
	        	stdin.write("passwd".getBytes());
	 	        stdin.write("\r".getBytes());
	 	        stdin.flush();
	 	        //  now we should get pw prompt:
	 	        line = readFromStream(stdout);
		        if (line.contains("(current) UNIX password:") == false)
		        	return 0;  // no prompt for password
	        }
	        
	        //  We're at the spot where the system is asking for the old password:
	        stdin.write(old_pw.getBytes());
	        stdin.write("\r".getBytes());
	        stdin.flush();
	        
	        line = readFromStream(stdout);
	        if ((line.contains("New UNIX password:") == false) && (line.contains("New password:") == false))
	        	return 0;  // didn't get next prompt
	        
	        stdin.write(new_pw.getBytes());
	        stdin.write("\r".getBytes());
	        stdin.flush();
	        
	        line = readFromStream(stdout);
	        if ((line.contains("Retype new UNIX password:") == false) && (line.contains("Retype new password:") == false))
	        	return 0;  // didn't get final prompt
	        
	        stdin.write(new_pw.getBytes());
	        stdin.write("\r".getBytes());
	        stdin.flush();
	        
	        //  See if there's any response:
	        String str = readFromStream(stdout);
	        while (str.length() > 0)
	        {
	        	logger.debug("stdout: " + str);
	        	str = readFromStream(stdout);
	        }
	        return 0;
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			return -1;
		}
		finally 
		{
			if (console != null)
				try { console.close(true); } catch (Exception ex) { logger.error(ex.getMessage(),ex); }
		}
	}

}
