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
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class SSHCopyTest
    extends SSHBase
{
    @ClassRule
    public static JunitSSHResource junitSSHResource = new JunitSSHResource(true);

    @Test
    public void testSSHCopyLocal()
        throws Exception
    {
        final String fileData = "SMFY!";
        final String rootDir = junitSSHResource.getRoot().getAbsolutePath();
        final File localFile = junitSSHResource.newFile("junitLocalLocal.txt");
        final String remoteFileName = rootDir + "/junitLocalRemote.txt";
        File remoteFile = new File(remoteFileName);

        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            fos.write(fileData.getBytes());
        }
        Assert.assertEquals(fileData.length(), localFile.length());
        Assert.assertFalse(remoteFile.exists());

        try (FileInputStream fis = new FileInputStream(localFile)) {
        	SSH ssh = new SSH("admin", "M3sh@dmin!", "localhost", junitSSHResource.getPort());
            ssh.attemptConnect();
            
            try  {
                Assert.assertTrue(ssh.copy(fis, localFile.length(), remoteFileName));
            } finally {
                try {
                    ssh.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }

        Assert.assertTrue(remoteFile.exists());
        byte[] copiedBytes = new byte[(int)remoteFile.length()];

        try (FileInputStream istream = new FileInputStream(remoteFile)) {
            istream.read(copiedBytes);
        }

        Assert.assertEquals(fileData, new String(copiedBytes));
    }

    @Test
    public void testSSHCopyRemote()
        throws Exception
    {
        final String fileData = "!SMFY";
        final String rootDir = junitSSHResource.getRoot().getAbsolutePath();
        final File remoteFile = junitSSHResource.newFile("junitRemoteRemote.txt");
        final String localFileName = rootDir + "/junitRemoteLocal.txt";
        File localFile = new File(localFileName);

        try (FileOutputStream fos = new FileOutputStream(remoteFile)) {
            fos.write(fileData.getBytes());
        }
        Assert.assertEquals(fileData.length(), remoteFile.length());
        Assert.assertFalse(localFile.exists());

        try (FileOutputStream fis = new FileOutputStream(localFile)) {
            SSH ssh = new SSH("admin", "M3sh@dmin!", "localhost", junitSSHResource.getPort());
            ssh.attemptConnect();	

            try  {
                Assert.assertTrue(ssh.copy(remoteFile.getAbsolutePath(), fis));
            } finally {
                try {
                    ssh.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }

        Assert.assertTrue(localFile.exists());
        byte[] copiedBytes = new byte[(int)localFile.length()];

        try (FileInputStream fis = new FileInputStream(localFile)) {
            fis.read(copiedBytes);
        }

        Assert.assertEquals(fileData, new String(copiedBytes));
    }
}
