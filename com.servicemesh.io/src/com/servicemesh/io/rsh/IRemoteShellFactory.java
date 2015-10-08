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

import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.rsh.RemoteShell.SocketFactory;

public interface IRemoteShellFactory
{
	public RemoteShell createRemoteShell(String userName, String passwd, String host, int port) throws CreateException;
	public RemoteShell createRemoteShell(SocketFactory socketFactory, String userName, String passwd, String host, int port) throws CreateException;
	public RemoteShell createRemoteShell(String userName, byte[] key, String host, int port) throws CreateException;
	public RemoteShell createRemoteShell(SocketFactory socketFactory, String userName, byte[] key, String host, int port) throws CreateException;
	public RemoteShell createRemoteShell(IHttpClientConfigBuilder configBuilder, String userName, String passwd, String host, int port, int reconnectRetries, int reconnectInterval) throws CreateException;
	public RemoteShell createRemoteShell(IHttpClientConfigBuilder configBuilder, String userName, String passwd, String host, int port, int reconnectRetries, int reconnectInterval, boolean tls) throws CreateException;
}
