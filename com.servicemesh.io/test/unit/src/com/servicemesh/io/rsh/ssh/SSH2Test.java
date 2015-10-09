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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.mindbright.nio.NetworkConnection;
import com.servicemesh.io.rsh.RemoteShell;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NetworkConnection.class})
public class SSH2Test
{

    @Test
    public void testSSHLeak()
        throws Exception
    {
        PowerMockito.mockStatic(NetworkConnection.class);
        NetworkConnection sshConnection = mock(NetworkConnection.class);
        RemoteShell.SocketFactory socketFactory = mock(RemoteShell.SocketFactory.class);
        when(socketFactory.createNetworkConnection(anyString(), anyInt(), anyInt())).thenReturn(sshConnection);

        try {
            new SSH(socketFactory, "admin", (String)null, "localhost", 22);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Empty password and empty key", ex.getMessage());
        }

        verify(socketFactory, times(0)).createNetworkConnection(anyString(), anyInt(), anyInt());
        verifyZeroInteractions(sshConnection);

        when(NetworkConnection.open(anyString(), anyInt())).thenReturn(sshConnection);
        PowerMockito.verifyStatic(never());
        NetworkConnection.open(anyString(), anyInt());

        try {
            new SSH((RemoteShell.SocketFactory)null, "admin", (String)null, "localhost", 22);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Empty password and empty key", ex.getMessage());
        }

        PowerMockito.verifyStatic(never());
        NetworkConnection.open(anyString(), anyInt());
        verifyZeroInteractions(sshConnection);
    }
}
