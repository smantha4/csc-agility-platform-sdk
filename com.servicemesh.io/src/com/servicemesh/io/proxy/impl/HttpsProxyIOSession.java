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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;

import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionBufferStatus;
import org.apache.http.nio.reactor.ssl.SSLIOSession;
import org.apache.http.nio.reactor.ssl.SSLMode;
import org.apache.http.nio.reactor.ssl.SSLSetupHandler;
import org.apache.log4j.Logger;

import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.PipelinedChannelResult;
import com.servicemesh.io.proxy.ProxyByteChannel;

public class HttpsProxyIOSession
    extends SSLIOSession
{
    private static final Logger _logger = Logger.getLogger(HttpsProxyIOSession.class);

    private final PipelinedChannel _pipelinedChannel;
    private final ProxyByteChannel _internalChannel;
    private final IOSession _ioSession;
    private final ByteBuffer _appDataIn;
    private final ByteBuffer _appDataOut;
    private final ByteBuffer _netDataIn;
    private final ByteBuffer _netDataOut;

    private SessionBufferStatus _appBufferStatus;

    public HttpsProxyIOSession(final IOSession ioSession, final SSLMode sslMode,
                               final SSLContext sslContext, final SSLSetupHandler handler,
                               final PipelinedChannel pipelinedChannel)
    {
        super(ioSession, sslMode, sslContext, handler);
        _pipelinedChannel = pipelinedChannel;
        _internalChannel = new HttpsProxyInternalByteChannel();
        _ioSession = ioSession;
        _ioSession.setBufferStatus(this);

        _appDataIn = ByteBuffer.allocate(getSSLEngine().getSession().getApplicationBufferSize());
        _appDataOut = ByteBuffer.allocate(getSSLEngine().getSession().getApplicationBufferSize());
        _netDataIn = ByteBuffer.allocate(getSSLEngine().getSession().getPacketBufferSize());
        _netDataOut = ByteBuffer.allocate(getSSLEngine().getSession().getPacketBufferSize());
    }

    @Override
    public synchronized void initialize()
        throws SSLException
    {
        super.initialize();
    }

    @Override
    public ByteChannel channel()
    {
        return _internalChannel;
    }

    @Override
    protected int receiveEncryptedData()
        throws IOException
    {
        _logger.trace("receiveEncryptedData: _appDataIn = " + _appDataIn.toString());
        _logger.trace("receiveEncryptedData: _netDataIn = " + _netDataIn.toString());

        int totalBytesRead = 0;

        if (!isEndOfStream()) {
            if (_appDataIn.position() > 0) {
                _appDataIn.flip();
                totalBytesRead += receiveEncryptedData(_appDataIn);
                _appDataIn.compact();
                _logger.trace("receiveEncryptedData after first receive: _appDataIn = " + _appDataIn.toString());
            }

            if (_netDataIn.position() > 0) {
                _netDataIn.flip();

                if (_pipelinedChannel != null) {
                    _pipelinedChannel.unwrap(_netDataIn, _appDataIn);
                } else {
                    transferBytes(_netDataIn, _appDataIn);
                }

                _netDataIn.compact();
                _logger.trace("receiveEncryptedData after first unwrap: _netDataIn = " + _netDataIn.toString());
                _logger.trace("receiveEncryptedData after first unwrap: _appDataIn = " + _appDataIn.toString());

                if (_appDataIn.position() > 0) {
                    _appDataIn.flip();
                    totalBytesRead += receiveEncryptedData(_appDataIn);
                    _appDataIn.compact();
                    _logger.trace("receiveEncryptedData after second receive: _appDataIn = " + _appDataIn.toString());
                }
            }

            int bytesRead = _ioSession.channel().read(_netDataIn);
            if (bytesRead > 0) {
                _logger.trace("receiveEncryptedData after channel read: _netDataIn = " + _netDataIn.toString());

                if (_netDataIn.position() > 0) {
                    _netDataIn.flip();

                    if (_pipelinedChannel != null) {
                        _pipelinedChannel.unwrap(_netDataIn, _appDataIn);
                    } else {
                        transferBytes(_netDataIn, _appDataIn);
                    }

                    _netDataIn.compact();
                    _logger.trace("receiveEncryptedData after second unwrap: _netDataIn = " + _netDataIn.toString());
                    _logger.trace("receiveEncryptedData after second unwrap: _appDataIn = " + _appDataIn.toString());

                    if (_appDataIn.position() > 0) {
                        _appDataIn.flip();
                        totalBytesRead += receiveEncryptedData(_appDataIn);
                        _appDataIn.compact();
                        _logger.trace("receiveEncryptedData after third receive: _appDataIn = " + _appDataIn.toString());
                    }
                }
            } else {
                if (bytesRead == -1) {
                    // Channel is closed, if we had buffered data send that.
                    // The next read should return a -1.
                    if (totalBytesRead == 0) {
                        totalBytesRead = -1;
                    }
                }
            }
        } else {
            totalBytesRead = -1;
        }

        return totalBytesRead;
    }

    @Override
    protected int sendEncryptedData()
        throws IOException
    {
        _logger.trace("sendEncryptedData: _appDataOut = " + _appDataOut.toString());
        _logger.trace("sendEncryptedData: _netDataOut = " + _netDataOut.toString());

        int bytesWritten = 0;

        if (_netDataOut.position() > 0) {
            _netDataOut.flip();
            bytesWritten = _ioSession.channel().write(_netDataOut);
            _netDataOut.compact();
            _logger.trace("sendEncryptedData after first write: _netDataOut = " + _netDataOut.toString());
        }

        if (_appDataOut.position() > 0) {
            _appDataOut.flip();

            if (_pipelinedChannel != null) {
                _pipelinedChannel.wrap(_appDataOut, _netDataOut);
            } else {
                transferBytes(_appDataOut, _netDataOut);
            }

            _appDataOut.compact();
            _logger.trace("sendEncryptedData after first wrap: _appDataOut = " + _appDataOut.toString());
            _logger.trace("sendEncryptedData after first wrap: _netDataOut = " + _netDataOut.toString());

            if (_netDataOut.position() > 0) {
                _netDataOut.flip();
                bytesWritten = _ioSession.channel().write(_netDataOut);
                _netDataOut.compact();
                _logger.trace("sendEncryptedData after second write: _netDataOut = " + _netDataOut.toString());
            }
        }

        sendEncryptedData(_appDataOut);
        _logger.trace("sendEncryptedData after send: _appDataOut = " + _appDataOut.toString());
        if (_appDataOut.position() > 0) {
            _appDataOut.flip();

            if (_pipelinedChannel != null) {
                _pipelinedChannel.wrap(_appDataOut, _netDataOut);
            } else {
                transferBytes(_appDataOut, _netDataOut);
            }

            _appDataOut.compact();
            _logger.trace("sendEncryptedData after second wrap: _appDataOut = " + _appDataOut.toString());
            _logger.trace("sendEncryptedData after second wrap: _netDataOut = " + _netDataOut.toString());

            if (_netDataOut.position() > 0) {
                _netDataOut.flip();
                bytesWritten = _ioSession.channel().write(_netDataOut);
                _netDataOut.compact();
                _logger.trace("sendEncryptedData after third write: _netDataOut = " + _netDataOut.toString());
            }
        }

        return bytesWritten;
    }

    /**
     * Reads encrypted data and returns whether the channel associated with
     * this session has any decrypted inbound data available for reading.
     *
     * @throws IOException in case of an I/O error.
     */
    @Override
    public synchronized boolean isAppInputReady()
        throws IOException
    {
        do {
            final int bytesRead = receiveEncryptedData();

            if (bytesRead == -1) {
                setEndOfStream(true);
            }

            doHandshake();

            final HandshakeStatus status = super.getSSLEngine().getHandshakeStatus();
            if (status == HandshakeStatus.NOT_HANDSHAKING || status == HandshakeStatus.FINISHED) {
                decryptData();
            }
        } while (super.getSSLEngine().getHandshakeStatus() == HandshakeStatus.NEED_TASK);

        // Some decrypted data is available or at the end of stream
        return ((getEventMask() & SelectionKey.OP_READ) > 0)
                && (hasBufferedInput()
                    || (_appBufferStatus != null && _appBufferStatus.hasBufferedInput())
                    || (isEndOfStream() && getStatus() == ACTIVE));
    }

    @Override
    public synchronized void setBufferStatus(final SessionBufferStatus status)
    {
        _appBufferStatus = status;
    }

    @Override
    public synchronized boolean hasBufferedInput()
    {
        return super.hasBufferedInput() || ((_appBufferStatus != null) && _appBufferStatus.hasBufferedInput()) || (_appDataIn.position() > 0) || (_netDataIn.position() > 0) || _internalChannel.hasBufferedInput();
    }

    @Override
    public synchronized boolean hasBufferedOutput()
    {
        return super.hasBufferedOutput() || ((_appBufferStatus != null) && _appBufferStatus.hasBufferedOutput()) || (_appDataOut.position() > 0) || (_netDataOut.position() > 0) || _internalChannel.hasBufferedOutput();
    }

    private int transferBytes(ByteBuffer src, final ByteBuffer dst)
    {
        int count = 0;

        count = Math.min(src.remaining(), dst.remaining());

        for (int i = 0; i < count; i++) {
            dst.put(src.get());
        }

        return count;
    }

    private class HttpsProxyInternalByteChannel
        implements ProxyByteChannel
    {
        @Override
        public int write(final ByteBuffer src)
            throws IOException
        {
            if (_pipelinedChannel == null) {
                return HttpsProxyIOSession.super.channel().write(src);
            } else {
                PipelinedChannelResult result = _pipelinedChannel.write(src, HttpsProxyIOSession.super.channel());

                return result.getByteCount();
            }
        }

        @Override
        public int read(final ByteBuffer dst)
            throws IOException
        {
            if (_pipelinedChannel == null) {
                return HttpsProxyIOSession.super.channel().read(dst);
            } else {
                PipelinedChannelResult result = _pipelinedChannel.read(dst, HttpsProxyIOSession.super.channel());

                return result.getByteCount();
            }
        }

        @Override
        public void close()
            throws IOException
        {
            HttpsProxyIOSession.this.close();
        }

        @Override
        public boolean isOpen()
        {
            return !HttpsProxyIOSession.this.isClosed();
        }

        @Override
        public boolean hasBufferedInput()
        {
            return (_pipelinedChannel != null) ? _pipelinedChannel.hasBufferedInput() : false;
        }

        @Override
        public boolean hasBufferedOutput()
        {
            return (_pipelinedChannel != null) ? _pipelinedChannel.hasBufferedOutput() : false;
        }
    }
}
