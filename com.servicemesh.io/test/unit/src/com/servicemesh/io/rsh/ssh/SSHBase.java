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

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.PublicKey;

import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.junit.rules.TemporaryFolder;

public abstract class SSHBase
{
    private static JUnitSSHDServer junitSSHServer;

    private static class DummyPublickeyAuthenticator
        implements PublickeyAuthenticator
    {
        @Override
        public boolean authenticate(String user, PublicKey publicKey, ServerSession serverSession)
        {
            return true;
        }
    }

    private static class DummyPasswordAuthenticator
        implements PasswordAuthenticator
    {
        @Override
        public boolean authenticate(String username, String password, ServerSession session)
        {
            return true;
        }
    }

    protected static class JUnitSSHDServer
    {
        private final SshServer sshServer;
        private final File hostkey;

        public JUnitSSHDServer(final TemporaryFolder tempFolder)
        {
            this(tempFolder, false);
        }

        public JUnitSSHDServer(final TemporaryFolder tempFolder, boolean isScp)
        {
            try {
                ServerSocket serverSocket = new ServerSocket(0);
                int port = serverSocket.getLocalPort();

                serverSocket.close();
                hostkey = tempFolder.newFile("hostkey.ser");
                sshServer = SshServer.setUpDefaultServer();
                sshServer.setPublickeyAuthenticator(new DummyPublickeyAuthenticator());
                sshServer.setPasswordAuthenticator(new DummyPasswordAuthenticator());
                sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(hostkey.getAbsolutePath()));
                sshServer.setPort(port);
                sshServer.setShellFactory(new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }));

                if (isScp) {
                    sshServer.setCommandFactory(new ScpCommandFactory());
                } else {
                    sshServer.setCommandFactory(new ScpCommandFactory(new CommandFactory() {
                        @Override
                        public Command createCommand(String command) {
                            //return new ProcessShellFactory(command.split(" ")).create();
                            String[] echoCommand = new String[]{ "echo", command };
                            return new ProcessShellFactory(echoCommand).create();
                        }
                    }));
                }
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex.getLocalizedMessage(), ex);
            }
        }

        public void start()
            throws IOException
        {
            if (sshServer != null) {
                sshServer.start();

                // Prime the server by starting a client session
                SshClient client = SshClient.setUpDefaultClient();
                ClientSession session = null;
                client.start();

                try {
                    session = client.connect("admin", "localhost", getPort()).await().getSession();

                    session.addPasswordIdentity("M3sh@dmin!");
                    session.waitFor(ClientSession.WAIT_AUTH, 500);
                } catch (Exception ex) {
                    // Ignore
                } finally {
                    if (session != null) {
                        session.close(true);
                    }

                    client.stop();
                }
            }
        }

        public void stop()
            throws InterruptedException
        {
            if (sshServer != null) {
                sshServer.stop(true);
            }
        }

        public int getPort()
        {
            return (sshServer != null) ? sshServer.getPort() : -1;
        }
    }

    protected static class JunitSSHResource
        extends TemporaryFolder
    {
        final boolean _isScp;

        public JunitSSHResource()
        {
            this(false);
        }

        public JunitSSHResource(boolean isScp)
        {
            _isScp = isScp;
        }

        @Override
        public void before()
            throws Throwable
        {
            super.before();

            junitSSHServer = new JUnitSSHDServer(this, _isScp);
            junitSSHServer.start();
        }

        @Override
        public void after()
        {
            if (junitSSHServer != null) {
                try {
                    junitSSHServer.stop();
                } catch (Exception ex) {
                    // Ignore
                }
            }

            super.after();
        }

        public int getPort()
        {
            return (junitSSHServer != null) ? junitSSHServer.getPort() : -1;
        }
    }
}
