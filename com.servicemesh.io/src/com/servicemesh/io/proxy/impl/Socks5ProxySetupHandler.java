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

package com.servicemesh.io.proxy.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLException;

import org.apache.http.nio.reactor.IOSession;
import org.apache.http.util.Args;

import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.ProxySetupHandler;

public class Socks5ProxySetupHandler
    implements ProxySetupHandler
{
    private final static String[] STATUS_MESSAGE = {
        "Success",
        "General SOCKS server failure",
        "Connection not allowed by ruleset",
        "Network unreachable",
        "Host unreachable",
        "Connection refused",
        "TTL expired",
        "Command not supported",
        "Address type not supported"
    };

    private final ProxyHost _proxyHost;
    private final IOSession _ioSession;
    private final PipelinedChannel _pipelinedChannel;
    private ByteBuffer _appDataIn;
    private ByteBuffer _netDataIn;
    private ByteBuffer _netDataOut;

    public Socks5ProxySetupHandler(final ProxyHost proxyHost, final IOSession ioSession, final PipelinedChannel pipelinedChannel)
    {
        _proxyHost = Args.notNull(proxyHost, "proxy host");
        Args.notNull(proxyHost.getTargetHost(), "target host");
        _ioSession = Args.notNull(ioSession, "IOSession");
        _pipelinedChannel = pipelinedChannel;
    }

    @Override
    public PipelinedChannel initialize()
        throws IOException
    {
        int authMethod = initiateConnect();

        switch (authMethod) {
            case 0x00:
                break;
            case 0x02:
                authenticate();
                break;
            default:
                throw new IOException("Unsupported authentication method requested by Socks5 server: " + authMethod);
        }

        connectToTarget();

        return (_proxyHost.getTargetHost() instanceof ProxyHost) ? new PassThroughChannel() : null;
    }

    private int initiateConnect()
        throws IOException
    {
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        ByteBuffer inBuffer = ByteBuffer.allocate(1);

        outBuf.reset();
        outBuf.write((byte)0x05);      // Version

        if ((_proxyHost.getPrincipal() != null) && !_proxyHost.getPrincipal().isEmpty()) {
            outBuf.write((byte)0x02);      // Number of methods
            outBuf.write((byte)0x00);      // Support noauth
            outBuf.write((byte)0x02);      // Support username auth
        } else {
            outBuf.write((byte)0x01);      // Number of methods
            outBuf.write((byte)0x00);      // Support no auth
        }

        ByteBuffer outBuffer = ByteBuffer.wrap(outBuf.toByteArray());
        if (write(outBuffer) <= 0) {
            throw new RuntimeException("Write failed, unable to initiate connection");
        }

        // Read version response to auth support
        read(inBuffer);
        inBuffer.rewind();

        int inByte = inBuffer.get() & 0xff;
        if (inByte == -1) {
            throw new IOException("Socks5 input stream closed from " + _proxyHost.getHostName());
        }

        if (inByte != 0x05) {
            throw new IOException("Invalid response '" + inByte + "' from Socks5 server " + _proxyHost.getHostName());
        }

        // Read requested auth method
        inBuffer.rewind();
        read(inBuffer);
        inBuffer.rewind();

        return inBuffer.get() & 0xff;
    }

    private void authenticate()
        throws IOException
    {
        String principal = _proxyHost.getPrincipal();
        String password = _proxyHost.getCredentials();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.reset();
        buf.write(0x01);
        buf.write(principal.length());
        buf.write(principal.getBytes());
        buf.write(password.length());
        buf.write(password.getBytes());

        ByteBuffer outBuffer = ByteBuffer.wrap(buf.toByteArray());

        if (write(outBuffer) <= 0) {
            throw new RuntimeException("Write failed, unable to authenticate connection");
        }

        ByteBuffer inBuffer = ByteBuffer.allocate(1);

        read(inBuffer);
        inBuffer.rewind();

        int response = inBuffer.get() & 0xff;

        /*
         * We accept both 0x05 and 0x01 as version in the response here. 0x01
         * is the right response but some buggy servers will respond with 0x05
         * (i.e. not complying with rfc1929).
         */
        if ((response != 0x01) && (response != 0x05)) {
            throw new IOException("Invalid response '" + response + "' from SOCKS5 server " + _proxyHost.getHostName());
        }

        inBuffer.rewind();
        read(inBuffer);
        inBuffer.rewind();

        if ((inBuffer.get() & 0xff) != 0x00) {
            throw new IOException("Proxy authentication failed");
        }
    }

    private void connectToTarget()
        throws IOException
    {
        String targetHost = _proxyHost.getTargetHost().getHostName();
        int targetPort = _proxyHost.getTargetHost().getPort();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        buf.reset();
        buf.write((byte)0x05);
        buf.write((byte)0x01);
        buf.write((byte)0x00);
        buf.write((byte)0x03);
        buf.write(targetHost.length());
        buf.write(targetHost.getBytes());
        buf.write((targetPort >>> 8) & 0xff);
        buf.write(targetPort & 0xff);

        ByteBuffer outBuffer = ByteBuffer.wrap(buf.toByteArray());

        if (write(outBuffer) <= 0) {
            throw new RuntimeException("Write failed, unable to initiate connection");
        }

        ByteBuffer inBuffer = ByteBuffer.allocate(1);

        read(inBuffer);
        inBuffer.rewind();

        int response = inBuffer.get() & 0xff;
        if (response != 0x05) {
            throw new IOException("Invalid response '" + response + "' from SOCKS5 server " + _proxyHost.getHostName());
        }

        // Read status
        inBuffer.rewind();
        read(inBuffer);
        inBuffer.rewind();

        int status = inBuffer.get() & 0xff;
        if (status != 0x00) {
            if ((status > 0) && (status < 9)) {
                throw new IOException("SOCKS5 server unable to connect, reason: " + STATUS_MESSAGE[status]);
            } else {
                throw new IOException("SOCKS5 server unable to connect, reason: " + status);
            }
        }

        // Read reserved byte
        inBuffer.rewind();
        read(inBuffer);
        inBuffer.rewind();
        inBuffer.get();

        // Read address type
        inBuffer.rewind();
        read(inBuffer);
        inBuffer.rewind();

        int addressType = inBuffer.get() & 0xff;
        // Read address in but we don't currently do anything with this
        switch (addressType) {
            case 0x01:
                for (int i = 0; i < 4; i++) {
                    inBuffer.rewind();
                    read(inBuffer);
                    inBuffer.rewind();
                    inBuffer.get();
                }
                break;
            case 0x03:
                inBuffer.rewind();
                read(inBuffer);
                inBuffer.rewind();

                int size = inBuffer.get() & 0xff;
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        inBuffer.rewind();
                        read(inBuffer);
                        inBuffer.rewind();
                        inBuffer.get();
                    }
                } else {
                    throw new IOException("Error reading address");
                }
                break;
            default:
                throw new IOException("Invalid address type");
        }

        // Two more bytes
        for (int i = 0; i < 2; i++) {
            inBuffer.rewind();
            read(inBuffer);
            inBuffer.rewind();
            inBuffer.get();
        }
    }

    private int read(final ByteBuffer dst)
        throws IOException
    {
        int bytesRead = 0;

        if (_pipelinedChannel == null) {
            while (bytesRead == 0) {
                bytesRead = _ioSession.channel().read(dst);
            }
        } else {
            if (_netDataIn == null) {
                _netDataIn = ByteBuffer.allocate(8192);
            }

            if (_appDataIn == null) {
                _appDataIn = ByteBuffer.allocate(8192);
            }

            if (_appDataIn.position() == 0) {
                while (bytesRead == 0) {
                    bytesRead = _ioSession.channel().read(_netDataIn);
                }

                _netDataIn.flip();
                _pipelinedChannel.unwrap(_netDataIn, _appDataIn);
                _netDataIn.compact();
            }

            if (_appDataIn.position() > 0) {
                _appDataIn.flip();

                int count = Math.min(dst.remaining(), _appDataIn.remaining());
                for (int i = 0; i < count; i++) {
                    dst.put(_appDataIn.get());
                }

                _appDataIn.compact();
                bytesRead = count;
            }
        }

        return bytesRead;
    }

    private int write(final ByteBuffer src)
        throws SSLException, IOException
    {
        int bytesWritten = 0;

        if (_pipelinedChannel == null) {
            bytesWritten = _ioSession.channel().write(src);
        } else {
            if (_netDataOut == null) {
                _netDataOut = ByteBuffer.allocate(8192);
            }

            _pipelinedChannel.wrap(src, _netDataOut);
            if (_netDataOut.position() > 0) {
                _netDataOut.flip();
                bytesWritten = _ioSession.channel().write(_netDataOut);
                _netDataOut.compact();
            }
        }

        return bytesWritten;
    }
}
