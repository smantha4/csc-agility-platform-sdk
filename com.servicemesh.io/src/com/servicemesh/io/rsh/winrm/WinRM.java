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

package com.servicemesh.io.rsh.winrm;

import intel.management.wsman.WsmanException;
import intel.management.wsman.WsmanUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.servicemesh.io.http.Credentials;
import com.servicemesh.io.http.Credentials.CredentialsType;
import com.servicemesh.io.http.HttpClientFactory;
import com.servicemesh.io.http.HttpMethod;
import com.servicemesh.io.http.IHttpClient;
import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.http.IHttpRequest;
import com.servicemesh.io.http.IHttpResponse;
import com.servicemesh.io.rsh.RemoteShell;

public class WinRM extends RemoteShell 
{
	private static final Logger logger = Logger.getLogger(WinRM.class);
	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	private static final long DEFAULT_WINRM_TIMEOUT = 3600;
	private static final long DEFAULT_WINRM_FILECOPYTIMEOUT = 60000;
	private static final long REQUEST_STATUS_TIMEOUT = 60;      // In seconds
	private static final int POST_RETRIES = 1;
	private static final String TIMEOUT_ERROR_STRING = "cannot complete the operation within the time specified";

	private IHttpClientConfigBuilder _clientConfigBuilder;
	private IHttpClient _httpClient;
	private Credentials _credentials;

	// Connection Properties
	
	/**
	 * Enable / Disable the use of TLS over the connection
	 */
	private boolean _tls=false;
		
	/**
	 * Store remote command shell ID
	 */
	private String _shellID;
	
	/**
	 * SelectorNode - 
	 */
	private Node _selectorNode;
			
	/**
	 * utils
	 */
	protected WinRMUtils _winRMUtils;	
	protected WsmanUtils _wsmanUtils = WsmanUtils.newInstance();
	protected int _reconnectRetries;
	protected int _reconnectInterval;

	public WinRM(IHttpClientConfigBuilder configBuilder, String userName, String passwd, String host, int port, 
	        int reconnectRetries, int reconnectInterval)
	    throws Exception 
	{
		this(configBuilder, userName, passwd, host, port, false, reconnectRetries, reconnectInterval);
	}
	
	public WinRM(IHttpClientConfigBuilder configBuilder, String userName, String passwd, String host, int port, boolean tls,
	        int reconnectRetries, int reconnectInterval)
	    throws Exception 
	{
		super(null, userName, passwd, host, port);
		_tls = tls;
		_clientConfigBuilder = configBuilder;
		_credentials = generateCredentials(userName, passwd);

		if (_credentials != null) {
		    _clientConfigBuilder.setCredentials(_credentials);
		}

		_httpClient = HttpClientFactory.getInstance().getClient(_clientConfigBuilder.build());
		_winRMUtils = WinRMUtils.newInstance(host);
		_reconnectRetries = reconnectRetries;
		_reconnectInterval = reconnectInterval;
		createCommandShell();
	}

	@Override
	public Protocol getProtocol()
	{
		return (_tls ? Protocol.WINRM_HTTPS : Protocol.WINRM_HTTP);
	}
	
	@Override
	public boolean isConnected() 
	{
		return ((_shellID != null) && (_shellID.length() > 0));
	}

	@Override
	public boolean attemptConnect()
	    throws Exception
	{
	    createCommandShell();

	    return isConnected();
	}

	@Override
	public boolean reconnect() 
	{
	    try
	    {
	        int retries = _reconnectRetries;
	        while (retries > 0)
	        {
	            try
	            {

	                if (_httpClient != null) {
	                    close();
	                }

	                _httpClient = HttpClientFactory.getInstance().getClient(_clientConfigBuilder.build());
	                _credentials = generateCredentials(_userName, _passwd);
	                createCommandShell();
	                return true;
	            }
	            catch (Exception ex)
	            {
	                logger.error("Error reconnecting ssh connection: " + ex.getMessage());
	            }
	            Thread.sleep(_reconnectInterval);
	            --retries;
	        }
	        return false;
	    }
	    catch(Exception ex)
	    {
	        logger.error(ex);
	        return false;
	    }
	}

	private Credentials generateCredentials(String user, String password)
	{
	    Credentials creds = null;

	    if ((user != null) && !user.isEmpty()) {
	        String domain = null;
	        int index = user.indexOf("\\");

	        if (index > 0) {
	            domain = user.substring(0, index);
	            user = user.substring(index + 1);
	        }

	        creds = new Credentials(CredentialsType.CREDENTIALS_TYPE_NTCREDS);
	        creds.setUsername(user);
	        creds.setPassword(password);

	        if ((domain != null) && !domain.isEmpty()) {
	            creds.setDomain(domain);
	        }
	    }

	    return creds;
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
	public int exec(final String cmd, final long timeout)
	{
		try
		{
			long timeoutInSecs = 0;

			if (timeout > 0) {
			    timeoutInSecs = timeout / 1000;

			    // In case the timeout had fractions of a second
			    if ((timeout % 1000) > 0) {
			        timeoutInSecs += 1;
			    }
			}

			String commandID = executeCommand(cmd, timeoutInSecs);
			StringBuilder stdout = new StringBuilder();
			StringBuilder stderr = new StringBuilder();

			return receiveResultSet(commandID, stdout, stderr, timeoutInSecs);
		}
		catch(Exception ex)
		{
			logger.error(ex);
			return -1;
		}
	}

	@Override
	public int exec(String cmd, StringBuilder stdout, StringBuilder stderr, long timeout) 
	{
		try
		{
			String commandID = executeCommand(cmd, timeout);
			return receiveResultSet(commandID,stdout,stderr,timeout);
		}
		catch(Exception ex)
		{
			logger.error(ex);
			return -1;
		}
	}

	@Override
	public int exec(String cmd, String stdin, StringBuilder stdout, StringBuilder stderr, long timeout) 
	{
		try
		{
			String commandID = executeCommand(cmd, timeout);
			sendInputStream(commandID, new ByteArrayInputStream(stdin.getBytes()));
			return receiveResultSet(commandID,stdout,stderr,timeout);
		}
		catch(Exception ex)
		{
			logger.error(ex);
			return -1;
		}
	}

	@Override
	public int exec(String cmd, OutputStream stdout, OutputStream stderr, long timeout) 
	{
		try
		{
			String commandID = executeCommand(cmd,timeout);
			return receiveResultSet(commandID,stdout,stderr,timeout,null, null);
		}
		catch(Exception ex)
		{
			logger.error(ex);
			return -1;
		}
	}

	@Override
	public int exec(String cmd, ByteArrayOutputStream stdout, ByteArrayOutputStream stderr, long timeout, Callback cb, String passwd) throws Exception 
	{
		try
		{
			String commandID = executeCommand(cmd,timeout);
			
			Thread.sleep(POLL_INTERVAL * 2); 
			return receiveResultSet(commandID,stdout,stderr,timeout,cb,passwd);
		}
		catch(Exception ex)
		{
			logger.error(ex);
			return -1;
		}
	}

	@Override
	public boolean copy(String lfile, String rfile) 
	{
		FileInputStream is = null;
		try
		{
			File file = new File(lfile);
			if(file.exists())
				return false;
			is = new FileInputStream(lfile);
			return copy(is,"0600",file.length(),rfile);
		}
		catch (Exception ex)
		{
			logger.error(ex);
			return false;
		}
		finally
		{
			try { if (is != null) is.close(); } catch (Exception ignored) {}
		}
	}
	
	@Override
	public boolean copy(InputStream is, long size, String rfile) 
	{
		return copy(is,"0600",size,rfile);
	}

	@Override
	public boolean copy(InputStream is, String mode, long size, String rfile) 
	{
		try
		{	// check for remote file exists and delete prior to copy 
			if (deleteRemoteFile(rfile)){
				StringBuilder stdout = new StringBuilder();
				StringBuilder stderr = new StringBuilder();
				StringBuilder cmd = new StringBuilder();
				cmd.append("powershell ");
				cmd.append("Invoke-Command -scriptBlock{");
	            cmd.append("$stdin=[Console]::OpenStandardInput();");
	            cmd.append("$wStream=new-object IO.FileStream ");
	            cmd.append(rfile);
	            cmd.append(",([System.IO.FileMode]::Append),([IO.FileAccess]::Write),([IO.FileShare]::Read);");
	            cmd.append("$stdin.CopyTo($wStream);");
	            cmd.append("$wStream.Close();}");
				String commandID = executeCommand(cmd.toString(),DEFAULT_WINRM_FILECOPYTIMEOUT);
				sendInputStream(commandID, is);
				int retval = receiveResultSet(commandID,stdout,stderr,DEFAULT_WINRM_FILECOPYTIMEOUT);
				return (retval == 0);
			}
			logger.error("remote file exists - not able to delete!");
			return false;
		}
		catch(Exception ex)
		{
			logger.error(ex);
			return false;
		}
	}

	@Override
	public boolean copy(String rfile, OutputStream os) 
	{
		try
		{
			String commandID = executeCommand("powershell get-content -encoding byte " + rfile, DEFAULT_WINRM_FILECOPYTIMEOUT);
			int retval = receiveResultSet(commandID,os,null,DEFAULT_WINRM_FILECOPYTIMEOUT,null,null);
			return (retval == 0);
		}
		catch(Exception ex)
		{
			logger.error(ex);
			return false;
		}
	}

	@Override
	public void close() 
	{
		try
		{
			// close the remote shell
			if(_shellID != null)
			{
				Document requestDoc = _wsmanUtils.loadDocumentFromString(WsmanUtils.WSMANXMLTemplate);
				_winRMUtils.InitializeMessage(requestDoc);
				_winRMUtils.SetResourceURI(WsmanUtils.WINRSCMDURI_NAMESPACE, requestDoc);
				_winRMUtils.SetActionURI(WsmanUtils.WINRSDELETEURI_NAMESPACE, requestDoc);
				addSelectorSet(requestDoc);		 			
				doPost(requestDoc);
			}
		}
		catch (Exception ex)
		{
			logger.error(ex);
		}
		finally
		{
			_shellID = null;

			if (_httpClient != null) {
			    try {
			        _httpClient.close();
			    } catch (IOException ex) {
			        // Ignore
			    }

			    _httpClient = null;
			}
		}
	}

	@Override
	public boolean rcopy(String user, String passwd, String host, String src, String dst, long timeout) 
	{
		return false;
	}

	@Override
	public int sudo(String cmd, String passwd, long timeout) 
	{
		return 0;
	}

	@Override
	public int resetPassword(String old_pw, String new_pw) 
	{
		return 0;
	};
	
	
	/**
     * Post a request to the client
     */
	private Document doPost(Document request) throws Exception
	{	
		return doPost(request, null, -1);
	}
	   
    /**
     * Post a request to the client
     * * requires new password in case of useradd/password change script where password is changed during cmd execution  
     *       -- subsequent call to retrieve status requires new password 
     *       -----hence method has signature to pass in the new password
     */
	private Document doPost(Document request, String passwd, long timeout) throws Exception
	{
	    return doPost(request, passwd, timeout, POST_RETRIES);
	}

	private Document doPost(final Document request, final String passwd, final long timeout, final int retries)
	    throws Exception
	{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IHttpClient httpClient = _httpClient;
        String userName = getUserName();
        StringBuilder uriBuilder = new StringBuilder();
        Exception errorException = null;
 
        uriBuilder.append(_tls ? HTTPS : HTTP);
        uriBuilder.append("://");
        uriBuilder.append(getHost());

        if (getPort() > 0) {
            uriBuilder.append(":");
            uriBuilder.append("" + getPort());
        }

        uriBuilder.append("/wsman");

        URI uri = new URI(uriBuilder.toString());
        IHttpRequest httpRequest = HttpClientFactory.getInstance().createRequest(HttpMethod.POST, uri);

        if (timeout > -1) {
            httpRequest.setRequestTimeout((int)(timeout * 1000));
        }

        httpRequest.setHeader(HttpClientFactory.getInstance().createHeader("Content-Type", "application/soap+xml;charset=UTF-8"));
        _wsmanUtils.saveDocument(request, os);

        byte[] requestBytes = os.toByteArray();
        httpRequest.setContent(requestBytes);

        if ((passwd != null) && !passwd.isEmpty()) {
            IHttpClientConfigBuilder clientConfigBuilder = _clientConfigBuilder.adapt();
            Credentials credentials = generateCredentials(userName, passwd);

            if (credentials != null) {
                clientConfigBuilder.setCredentials(credentials);
            }

            httpClient = HttpClientFactory.getInstance().getClient(clientConfigBuilder.build());
        }

		int retriesLeft = retries;
		do {
			IHttpResponse httpResponse = null;

			try {
				if (logger.isDebugEnabled()) {
					logger.debug("REQUEST: " + new String(requestBytes));
				}

				errorException = null;
				httpResponse = httpClient.exec(httpRequest);
				int status = httpResponse.getStatusCode();
				switch (status) {
					case 200: // ok
					case 201: // created
					case 202: // accepted
					{
						Document response = getDocument(httpRequest, httpResponse);
						if(logger.isDebugEnabled())
						{
							 os = new ByteArrayOutputStream();
					        _wsmanUtils.saveDocument(response,os);
					        logger.debug("RESPONSE: " + new String(os.toByteArray()));
						}
						return response;
					}
					default: // error status
					{
						Document error = getDocument(httpRequest, httpResponse);

						if(error == null) {
						    errorException = new Exception("WinRM:  Post failed with HTTP status: " + status);
						} else {
						    errorException = new WsmanException(error);
						}
						logger.error("POST Error, status=" + status + " error=: " + errorException.getMessage());
						cleanup(httpResponse);
					}
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage());
				cleanup(httpResponse);
			}

			if (retriesLeft > 0) {
			    Thread.sleep(5000);
			}
		} while (retriesLeft-- > 0);
		
		if (passwd == null) { 
		    if (errorException != null) {
		        throw errorException;
		    } else {
		        throw new Exception("unable to connect to: " + uri.toString());
		    }
		}

		return null;
	}
	
	private Document getDocument(IHttpRequest request, IHttpResponse response) throws Exception
	{
	    Document document = null;
	    InputStream instream = null;

	    try {
	        instream = response.getContentAsStream();
	        document = _wsmanUtils.loadDocument(instream);
	    } catch (Exception ex) {
	        //request.abort();
	    } finally {
	        if (instream != null) {
	            instream.close();
			}
		}
	    
	    return document;
	}

	private void cleanup(IHttpResponse response) 
	{
	    if (response != null) {
	        try {
	            response.close();
	        } catch (Exception ex) {
	            // Ignore
	        }
	    }
	}

   
	/**
     * Retrieve Selector value from response - require for executing subsequent command calls
     *
     * @param Selector name and response document
     *
     *
     *
    */
	private Object getSelectorValue(String name, Document document) 
	{
		NodeList nodes=null;
		Object result=null;
		
		Element headrefElt=WsmanUtils.findChild(document.getDocumentElement(),
				WsmanUtils.SOAP_NAMESPACE,
		        "Body");
		
		Element resElt=WsmanUtils.findChild(headrefElt,
		    		WsmanUtils.WSMANTRANSFER_NAMESPACE,
		            "ResourceCreated");
		
		Element refElt=WsmanUtils.findChild(resElt,
				WsmanUtils.ADDRESSING_NAMESPACE,
		        "ReferenceParameters");
		
		nodes = refElt.getChildNodes();
		for (int i=0; nodes!=null && i< nodes.getLength();i++)
		{
			Node node = nodes.item(i);
			if (node.getLocalName().equals("SelectorSet") )
			{
				 _selectorNode = node;
				 break;
			}
		}
		Element selectorsElt=WsmanUtils.findChild(refElt,
		        WsmanUtils.WSMAN_NAMESPACE,
		        "SelectorSet");
		
		if (selectorsElt!=null)
		    nodes  = selectorsElt.getChildNodes();
		for (int i=0; nodes!=null && i< nodes.getLength();i++) 
		{
		    Node node = nodes.item(i);
		    if (node.getNodeType()==Node.ELEMENT_NODE &&
		            node.getLocalName().equals("Selector") &&
		            node.getNamespaceURI().endsWith(WsmanUtils.WSMAN_NAMESPACE) &&
		            ((Element)node).hasAttribute("Name") &&
		            ((Element)node).getAttribute("Name").equals(name))
		    {
		    		result = node.getTextContent();
		    		break;
		    }   
		}
		
		return result;
    }
	 
	 /**
    * Add established selector value to the request XML
    *
    * @param request XML doc
    *
    */
	 
	private void addSelectorSet(Document document) 
	{
		Element headerElt=WsmanUtils.findChild(document.getDocumentElement(),
				WsmanUtils.SOAP_NAMESPACE,
		        "Header");
		
		headerElt.appendChild(document.importNode(_selectorNode, true));		
		try {
			Element node =  DocumentBuilderFactory
				    .newInstance()
				    .newDocumentBuilder()
				    .parse(new ByteArrayInputStream(_wsmanUtils.WINRSCOMMANDOPTION.getBytes()))
				    .getDocumentElement();
			headerElt.appendChild(document.importNode(node, true));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		     
	}
	    
   /**
    * Create Remote Command Shell
    *
    * @param None
    *
    */
   
    private void createCommandShell() throws Exception
    {
	   if(_shellID == null)
	   {
		   Document requestDoc, responseDoc;
			requestDoc = _wsmanUtils.loadDocumentFromString(WsmanUtils.WSMANXMLTemplate);
		 
			_winRMUtils.InitializeMessage(requestDoc);
			_winRMUtils.SetBody(WsmanUtils.WINRSCREATEBODY, requestDoc);   
			_winRMUtils.SetResourceURI(WsmanUtils.WINRSCMDURI_NAMESPACE, requestDoc);
			_winRMUtils.SetActionURI(WsmanUtils.WINRSCREATEURI_NAMESPACE, requestDoc);
			
			responseDoc = doPost(requestDoc);
			_shellID = (String)getSelectorValue("ShellId", responseDoc);
	   }
   }
   
   /**
    * Call to execute remote powershell commands
    *
    * @param command The remote powershell command to execute
    * @param timeout The timeout in seconds
    *
    */
    public String executeCommand(String cmdline, long timeout) throws Exception
    {
		Document requestDoc, responseDoc;
	
		requestDoc = _wsmanUtils.loadDocumentFromString(WsmanUtils.WSMANXMLTemplate.replace("PT60S", "PT" + String.valueOf(timeout) + "S"));
		_winRMUtils.InitializeMessage(requestDoc);
		
		String cmd;
		String args = null;
		int index = cmdline.indexOf(" ");
		if(index > 0)
		{
			cmd = cmdline.substring(0,index);
			args = cmdline.substring(index+1);
		}
		else
		{
			cmd = cmdline;
		}
		if (cmd.equals("/bin/bash") || cmd.equals("/bin/sh") || cmd.equals("sh"))
			cmd = "c:\\cygwin\\bin\\bash.exe";
			
		StringBuilder cmdString = new StringBuilder();
		cmdString.append(WsmanUtils.WINRSCOMMANDBODY_BEGIN.replace("{Command}", cmd));
		if(args != null)
		{
			cmdString.append("<Arguments>");
			cmdString.append(StringEscapeUtils.escapeXml(args));
			cmdString.append("</Arguments>");
		}	
		cmdString.append(WsmanUtils.WINRSCOMMANDBODY_END);
		
		_winRMUtils.SetBody(cmdString.toString(),requestDoc);
		_winRMUtils.SetResourceURI(WsmanUtils.WINRSCMDURI_NAMESPACE, requestDoc);
		_winRMUtils.SetActionURI(WsmanUtils.WINRSCOMMANDURI_NAMESPACE, requestDoc);
		        
		addSelectorSet(requestDoc);
		        
		responseDoc = doPost(requestDoc, null, timeout);
		return _winRMUtils.getCommandId(responseDoc);
    }
   
    private void sendInputStream(String commandID, InputStream is) throws Exception
    {
    	byte[] bytes = new byte[32768];
    	int sequenceID = 0;
    	int count;
    	while((count = is.read(bytes)) > 0)
    	{
    		boolean endofStreamFlag = (is.available() == 0);
    		
    		Document requestDoc = buildSendRequest(commandID, sequenceID++, bytes, count, endofStreamFlag);
    		doPost(requestDoc);
    	}
    	// send end of stream marker
    	if(count == bytes.length)
    	{
			Document requestDoc = buildSendRequest(commandID, sequenceID, bytes, 0, true);
			doPost(requestDoc);
    	}
    }
   
   /**
    * Receive Result set
    *
    * @param None
    */
    public <T extends OutputStream> int receiveResultSet(String commandID, T stdout, T stderr, long timeout, Callback cb, String passwd) throws Exception
    {
    	long start = Calendar.getInstance().getTimeInMillis();
        int sequenceID = 0;
        Integer retval = null;
        long timeoutInMilliseconds = timeout * 1000;
        final long requestStatusTimeout = (timeout < REQUEST_STATUS_TIMEOUT) ? timeout : REQUEST_STATUS_TIMEOUT;
        final long httpTimeout = requestStatusTimeout + 1;
        Exception exception = null;

		while ((retval == null) && ((timeoutInMilliseconds == 0) || (Calendar.getInstance().getTimeInMillis() - start < timeoutInMilliseconds)))
		{
		    // build up the request
		    Document requestDoc = buildReceiveRequest(commandID, sequenceID++, requestStatusTimeout);

 			// poll for completion
            Document responseDoc;
            try {
                exception = null;
                responseDoc = doPost(requestDoc, passwd, httpTimeout, 1);

                if (responseDoc != null) {
                    retval = _winRMUtils.getOutputValues(responseDoc, stdout, stderr);
                }
            } catch (WsmanException ex) {
                String reason = ex.getMessage();

                exception = ex;
                if ((reason != null) && reason.contains(TIMEOUT_ERROR_STRING)) {
                    // Ignore, expected possibility
                    sequenceID--;
                } else {
                    throw ex;
                }
            } catch (SocketTimeoutException ex) {
                // Ignore, expected possibility
                sequenceID--;
                exception = ex;
            } catch (ExecutionException ex) {
                Throwable th = ex.getCause();

                exception = ex;
                if (th instanceof SocketTimeoutException) {
                    //Ignore, expected possibility
                    sequenceID--;
                } else {
                    throw ex;
                }
            }

            if ((retval != null) && (passwd != null) && (retval == 0)) {
                _passwd = passwd;
            }

            if (cb != null) {
                cb.update(stdout, stderr);
			}

			if (retval == null)
 				Thread.sleep(POLL_INTERVAL); 
		}

		if (retval == null) {
		    if (exception != null) {
		        throw exception;
		    } else {
		        throw new Exception("timeout executing command");
		    }
		}

		return retval;
	}
    
    /**
     * Receive Result set
     *
     * @param None
     */
    public int receiveResultSet(String commandID, StringBuilder stdout, StringBuilder stderr, long timeout) throws Exception
    {
     	long start = Calendar.getInstance().getTimeInMillis();
        int sequenceID = 0;
        Integer retval = null;
        long timeoutInMilliseconds = timeout * 1000;
        final long requestStatusTimeout = (timeout < REQUEST_STATUS_TIMEOUT) ? timeout : REQUEST_STATUS_TIMEOUT;
        final long httpTimeout = requestStatusTimeout + 1;
        Exception exception = null;

        while ((retval == null) && ((timeoutInMilliseconds == 0) || (Calendar.getInstance().getTimeInMillis() - start < timeoutInMilliseconds)))
 		{
 			// build up the request
 			Document requestDoc = buildReceiveRequest(commandID, sequenceID++, requestStatusTimeout);
 			
 			// poll for completion
 			Document responseDoc;
 			try {
 			    exception = null;
 			    responseDoc = doPost(requestDoc, (String)null, httpTimeout, 1);

 			    if (responseDoc != null) {
 			        retval = _winRMUtils.getOutputValues(responseDoc, stdout, stderr);
 			    }
 			} catch (WsmanException ex) {
 			    String reason = ex.getMessage();

 			    exception = ex;
 			    if ((reason != null) && reason.contains(TIMEOUT_ERROR_STRING)) {
 			        // Ignore, expected possibility
 			        sequenceID--;
 			    } else {
 			        throw ex;
 			    }
 			} catch (SocketTimeoutException ex) {
 			    // Ignore, expected possibility
                sequenceID--;
                exception = ex;
 			} catch (ExecutionException ex) {
 			    Throwable th = ex.getCause();

 			    exception = ex;
 			    if (th instanceof SocketTimeoutException) {
 			        // Ignore, expected possibility
 			        sequenceID--;
                } else {
                    throw ex;
                }
 			}
 			
 			if (retval == null)
 				Thread.sleep(POLL_INTERVAL); 
 		}

        if (retval == null) {
            if (exception != null) {
                throw exception;
            } else {
                throw new Exception("timeout executing command");
            }
        }

        return retval;
 	}

    private Document buildSendRequest(String commandID, int sequenceID, byte[] input, int count, boolean end) throws Exception
    {
		Document requestDoc = _wsmanUtils.loadDocumentFromString(WsmanUtils.WSMANXMLTemplate);
		_winRMUtils.InitializeMessage(requestDoc);
		 
		Element headerElt=WsmanUtils.findChild(requestDoc.getDocumentElement(),
	            WsmanUtils.SOAP_NAMESPACE,
	            "Header");
	    
		headerElt.appendChild(requestDoc.importNode(_selectorNode, true));

		StringBuffer sendBody = new StringBuffer();
		sendBody.append(WsmanUtils.WINRSSENDBODY_BEGIN.replace("{SequenceId}", String.valueOf(sequenceID)).replace("{CommandId}", commandID).replace("{End}", String.valueOf(end)));
		WsmanUtils.getBase64String(input,count,sendBody);
		sendBody.append(WsmanUtils.WINRSSENDBODY_END);
		
		_winRMUtils.SetBody(sendBody.toString(),requestDoc);
		_winRMUtils.SetResourceURI(WsmanUtils.WINRSCMDURI_NAMESPACE, requestDoc);
		_winRMUtils.SetActionURI(WsmanUtils.WINRSSENDURI_NAMESPACE, requestDoc);
		return requestDoc;
    }

    private Document buildReceiveRequest(String commandID, int sequenceID,long timeout) throws Exception
    {
	    Document requestDoc = _wsmanUtils.loadDocumentFromString(WsmanUtils.WSMANXMLTemplate.replace("PT60S", "PT" + String.valueOf(timeout) + "S"));
		_winRMUtils.InitializeMessage(requestDoc);
		
		Element headerElt=WsmanUtils.findChild(requestDoc.getDocumentElement(),
		            WsmanUtils.SOAP_NAMESPACE,
		            "Header");
		    
		headerElt.appendChild(requestDoc.importNode(_selectorNode, true));
		 
		String recvbody = WsmanUtils.WINRSRECEIVEBODY.replace("{SequenceId}", String.valueOf(sequenceID)).replace("{CommandId}", commandID);
		
		_winRMUtils.SetBody(recvbody, requestDoc);
		_winRMUtils.SetResourceURI(WsmanUtils.WINRSCMDURI_NAMESPACE, requestDoc);
		_winRMUtils.SetActionURI(WsmanUtils.WINRSRECEIVEURI_NAMESPACE, requestDoc);
		return requestDoc;
    }
    
    private boolean deleteRemoteFile(String rfile) throws Exception{
    	
    	StringBuilder stdout = new StringBuilder();
    	StringBuilder stderr = new StringBuilder();
    	String cmd = "PowerShell Invoke-Command -scriptBlock{if (Test-Path "+ rfile + "){Remove-Item " +rfile +"}}";
    	
    	String commandID = executeCommand(cmd.toString(),DEFAULT_WINRM_TIMEOUT);
		int retval = receiveResultSet(commandID,stdout,stderr,DEFAULT_WINRM_TIMEOUT);
		
		return (retval == 0);
    	
    }

	@Override
	public int exec(String cmd, ByteArrayOutputStream stdout,
			ByteArrayOutputStream stderr, long timeout, String passwd,
			CompletionHandler handler) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
}
