package com.servicemesh.io.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbright.nio.NetworkConnection;
import com.servicemesh.io.rsh.RemoteShell;
import com.servicemesh.proxy.HttpNetworkConnectionProxy;
import com.servicemesh.proxy.HttpProxy;
import com.servicemesh.proxy.HttpsNetworkConnectionProxy;
import com.servicemesh.proxy.HttpsProxy;
import com.servicemesh.proxy.NetworkConnectionProxy;
import com.servicemesh.proxy.ProxyConnection;
import com.servicemesh.proxy.ProxySocket;
import com.servicemesh.proxy.Socks5NetworkConnectionProxy;
import com.servicemesh.proxy.Socks5Proxy;
import com.servicemesh.proxy.UserPasswordAuthentication;


public class ProxySocketFactory implements RemoteShell.SocketFactory {
	
	private com.servicemesh.proxy.Proxy _proxy = null;
	private com.servicemesh.proxy.NetworkConnectionProxy _connectionProxy = null;
	private static final Logger logger = Logger.getLogger(ProxySocketFactory.class);

	public ProxySocketFactory(List<Proxy> proxies) throws Exception
	{
	    this(proxies, false);
	}

	public ProxySocketFactory(List<Proxy> proxies, boolean useNIO)
	    throws Exception
	{
	    // support connecting through a chain of proxies
		try {
		    if (!useNIO) {
		        setupSocketProxy(proxies);
		    } else {
		        setupNetworkConnectionProxy(proxies);
		    }
		} catch(Exception ex) {
			throw new Exception(ex.getMessage(),ex);
		}
	}

	@Override
	public Socket createSocket(String host, int port, long timeout) throws IOException, UnknownHostException
	{
		return new ProxySocket(_proxy,host,port);
	}

	@Override
	public NetworkConnection createNetworkConnection(String host, int port, long timeout)
	    throws IOException, UnknownHostException
	{
	    return new ProxyConnection(_connectionProxy, host, port);
	}
	
	public InputStream getInputStream(Socket socket)throws IOException
	{
		return socket.getInputStream();
	}
	 
	public OutputStream getOutputStream(Socket socket)throws IOException
	{
		return socket.getOutputStream();
	}

	private void setupSocketProxy(List<Proxy> proxies)
	{
        com.servicemesh.proxy.Proxy p = null;
        for (Proxy proxy : proxies) {
            switch (proxy.getType()) {
                case SOCKS5_PROXY:
                    try {
                        Socks5Proxy s5proxy = new Socks5Proxy(p, proxy.getHostname(), proxy.getPort());
                        if (proxy.getPassword() != null && proxy.getPassword().trim().length() > 0) {
                            s5proxy.setAuthenticationMethod(UserPasswordAuthentication.METHOD_ID, new UserPasswordAuthentication(proxy.getAdmin(),proxy.getPassword()));
                        }
                        p = s5proxy;
                    } catch (UnknownHostException unh) {
                        logger.error("Unknown hostname: " + proxy.getHostname());
                        //  skip this proxy...
                    }
                    break;
                case HTTP_PROXY:
                    try {
                        HttpProxy httpProxy = new HttpProxy(p, proxy.getHostname(), proxy.getPort());
                        if (proxy.getPassword() != null && proxy.getPassword().trim().length() > 0) {
                             httpProxy.setUserPassword(proxy.getAdmin(),proxy.getPassword());
                        }

                        p = httpProxy;
                    } catch (UnknownHostException unh) {
                        logger.error("Unknown hostname: " + proxy.getHostname());
                        //  skip this proxy...
                    }
                    break;
                case HTTPS_PROXY:
                    try {
                        HttpsProxy httpsProxy = new HttpsProxy(p, proxy.getHostname(), proxy.getPort());
                        if (proxy.getPassword() != null && proxy.getPassword().trim().length() > 0) {
                             httpsProxy.setUserPassword(proxy.getAdmin(),proxy.getPassword());
                        }
                        p = httpsProxy;
                    } catch (UnknownHostException unh) {
                        logger.error("Unknown hostname: " + proxy.getHostname());
                        //  skip this proxy...
                    }
                    break;
                default:
                    logger.error("Unsupported proxy type: " + proxy.getType().toString());
                    //  skip this proxy...
            }
        }
        _proxy = p;
	}

	private void setupNetworkConnectionProxy(List<Proxy> proxies)
	{
        NetworkConnectionProxy p = null;

        for (Proxy proxy : proxies) {
            switch (proxy.getType()) {
                case SOCKS5_PROXY:
                    try {
                        Socks5NetworkConnectionProxy socks5Proxy = new Socks5NetworkConnectionProxy(p, proxy.getHostname(), proxy.getPort());
                        if (proxy.getPassword() != null && proxy.getPassword().trim().length() > 0) {
                             socks5Proxy.setUserPassword(proxy.getAdmin(), proxy.getPassword());
                        }
                        p = socks5Proxy;
                    } catch (UnknownHostException unh) {
                        logger.error("Unknown hostname: " + proxy.getHostname());
                        //  skip this proxy...
                    }
                    break;
                case HTTP_PROXY:
                    try {
                        HttpNetworkConnectionProxy httpProxy = new HttpNetworkConnectionProxy(p, proxy.getHostname(), proxy.getPort());
                        if (proxy.getPassword() != null && proxy.getPassword().trim().length() > 0) {
                             httpProxy.setUserPassword(proxy.getAdmin(), proxy.getPassword());
                        }
                        p = httpProxy;
                    } catch (UnknownHostException unh) {
                        logger.error("Unknown hostname: " + proxy.getHostname());
                        //  skip this proxy...
                    }
                    break;
                case HTTPS_PROXY:
                    try {
                        HttpsNetworkConnectionProxy httpsProxy = new HttpsNetworkConnectionProxy(p, proxy.getHostname(), proxy.getPort());
                        if (proxy.getPassword() != null && proxy.getPassword().trim().length() > 0) {
                             httpsProxy.setUserPassword(proxy.getAdmin(), proxy.getPassword());
                        }
                        p = httpsProxy;
                    } catch (UnknownHostException unh) {
                        logger.error("Unknown hostname: " + proxy.getHostname());
                        //  skip this proxy...
                    }
                    break;
                default:
                    logger.error("Unsupported proxy type: " + proxy.getType().toString());
                    //  skip this proxy...
            }
        }
        _connectionProxy = p;
	}
}
