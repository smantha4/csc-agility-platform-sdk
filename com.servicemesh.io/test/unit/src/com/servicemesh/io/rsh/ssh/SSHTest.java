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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.google.common.util.concurrent.AbstractFuture;
import com.servicemesh.io.rsh.RemoteShell.CompletionHandler;

public class SSHTest
    extends SSHBase
{
    private final static String TEST_STRING = "My string";
    private final static String TEST_STRING2 = "My new string";

    @ClassRule
    public static JunitSSHResource junitSSHResource = new JunitSSHResource();

    @Test
    public void testSSH()
        throws Exception
    {
        SSH ssh = new SSH("admin", "M3sh@dmin!", "localhost", junitSSHResource.getPort());
        ssh.attemptConnect();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        int rc;

        try {
            rc = ssh.exec(TEST_STRING, stdout, stderr);
        } finally {
            ssh.close();
        }

        String stringOut = new String(stdout.toByteArray());
        Assert.assertEquals(0,  rc);
        Assert.assertTrue(Arrays.equals(TEST_STRING.getBytes(), stringOut.trim().getBytes()));
        Assert.assertEquals(0, stderr.toByteArray().length);

        // Test reconnect
        Assert.assertTrue(ssh.reconnect());
        stdout = new ByteArrayOutputStream();
        stderr = new ByteArrayOutputStream();

        try {
            rc = ssh.exec(TEST_STRING2, stdout, stderr);
        } finally {
            ssh.close();
        }

        stringOut = new String(stdout.toByteArray());
        Assert.assertEquals(0,  rc);
        Assert.assertTrue(Arrays.equals(TEST_STRING2.getBytes(), stringOut.trim().getBytes()));
        Assert.assertEquals(0, stderr.toByteArray().length);
    }

    @Test
    public void testSSHKeys()
        throws Exception
    {
        String keyfile =
            "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIICXQIBAAKBgQCwckgILRp0uxomOty/+znaXxKvhWKt7rg1Tt1dD314m9tIvY4B\n" +
            "nl9RSYe2wmBR83G34L+7T+Nnq5Ub5b8oIg+Gq2Yi9b31IPFBJsL8Z8CepXYgIkjv\n" +
            "2sLen0PeyI00+7DQnic9+gw2ck+bk2Kqh19BlyoZVl20Y0cNkF4gPw1j/QIDAQAB\n" +
            "AoGAMAUqLW94/8Pb/gOPCICq4g912mcG0Cdj4r4v6J/KmgRwzX8pIq+jaVAFq2uD\n" +
            "I0qJ7G0yQRwg25oK3qjMOXPrnVs42BtscNhZzmdyjUDPMkTFk1pGWuQMKnuHo7py\n" +
            "jiw8SL7s+0zx+3NNH4hKbs1k/LMdfGOfcCxNz8X2/PLuioECQQDf615R9UGWy8og\n" +
            "QHEABxvacfM9oBT12FvR1BNHzw4V3xcApZBxEp5A5kpWekIH+pS1oW50tuEFGe3T\n" +
            "D7KxqPC5AkEAybnCOYGtyX25mvatzdqmnloeite+cNezb07Z4iNcQDU0t8bFGo2n\n" +
            "gOszMPAdSlj9mcuAjYusN3O9VqvpBJZDZQJANm5lqDChOAHj6YEgtuwDdk963IEc\n" +
            "d0KX12LhKPSJLQHk+pBEk2f+NjKE4Y6onH3qRliunAqUmoPaDxNi0dMKaQJBAK6W\n" +
            "JpK+uTn5pfmIJURUSIAkXuqrbhcbmCBtjgU5tU2cvtOPJGem0aGx7cwdBDykaFgC\n" +
            "2G+owpP7h5LLlhByPEECQQDNPea2ekU2oaSqSskrT0kontqhjgZiSVMGSrOuqSLt\n" +
            "nzlp32qNQQfLfL7p5P3YVQacodIddYOOvCy7+aQkXrY6\n" +
            "-----END RSA PRIVATE KEY-----\n";

        SSH ssh = new SSH("admin", keyfile.getBytes(), "localhost", junitSSHResource.getPort());
        ssh.attemptConnect();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        int rc;

        try {
            rc = ssh.exec(TEST_STRING, stdout, stderr);
        } finally {
            ssh.close();
        }

        String stringOut = new String(stdout.toByteArray());
        Assert.assertEquals(0,  rc);
        Assert.assertTrue(Arrays.equals(TEST_STRING.getBytes(), stringOut.trim().getBytes()));
        Assert.assertEquals(0, stderr.toByteArray().length);

        // Test reconnect
        Assert.assertTrue(ssh.reconnect());
        stdout = new ByteArrayOutputStream();
        stderr = new ByteArrayOutputStream();

        try {
            rc = ssh.exec(TEST_STRING2, stdout, stderr);
        } finally {
            ssh.close();
        }

        stringOut = new String(stdout.toByteArray());
        Assert.assertEquals(0,  rc);
        Assert.assertTrue(Arrays.equals(TEST_STRING2.getBytes(), stringOut.trim().getBytes()));
        Assert.assertEquals(0, stderr.toByteArray().length);
    }

    @Test
    public void testCompletionHandler()
        throws Exception
    {
        final HandlerStatusFuture future = new HandlerStatusFuture();
        SSH ssh = new SSH("admin", "M3sh@dmin!", "localhost", junitSSHResource.getPort());
        ssh.attemptConnect();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        HandlerStatus handlerStatus = null;

        try {
            ssh.exec(TEST_STRING, stdout, stderr, 0, (String)null, new Handler(future));
            handlerStatus = future.get();
        } finally {
            ssh.close();
        }

        Assert.assertNotNull(handlerStatus);
        Assert.assertEquals(0, handlerStatus.getStatus());

        Assert.assertNotNull(handlerStatus.getStderr());
        if (handlerStatus.getStderr().toString().length() != 0) {
            Assert.fail("stderr contained data: " + handlerStatus.getStderr().toString());
        }

        Assert.assertNotNull(handlerStatus.getStdout());
        if (!Arrays.equals(TEST_STRING.getBytes(), handlerStatus.getStdout().toString().trim().getBytes())) {
            Assert.fail("stdout output doesn't match: " + handlerStatus.getStdout().toString().trim());
        }
    }

    private static class HandlerStatus
    {
        final int _status;
        final OutputStream _stdout;
        final OutputStream _stderr;

        public HandlerStatus(final int status, final OutputStream stdout, final OutputStream stderr)
        {
            _status = status;
            _stdout = stdout;
            _stderr = stderr;
        }

        public int getStatus()
        {
            return _status;
        }

        public OutputStream getStdout()
        {
            return _stdout;
        }

        public OutputStream getStderr()
        {
            return _stderr;
        }
    }

    private static class HandlerStatusFuture
        extends AbstractFuture<HandlerStatus>
    {
        @Override
        public boolean set(final HandlerStatus result)
        {
            return super.set(result);
        }
    }

    private static class Handler
        implements CompletionHandler
    {
        private final HandlerStatusFuture _future;

        public Handler(HandlerStatusFuture future)
        {
            _future = future;
        }

        @Override
        public <T extends OutputStream> void exitStatus(int status, T stdout, T stderr)
            throws Exception
        {
            _future.set(new HandlerStatus(status, stdout, stderr));
        }

        @Override
        public <T extends OutputStream> void timerExpired(T stdout, T stderr)
        {
            _future.set(new HandlerStatus(-1, stdout, stderr));
        }
    }
}
