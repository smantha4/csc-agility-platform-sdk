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

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLEngineResult.Status;

import org.apache.log4j.Logger;

import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.PipelinedChannelResult;

public class SSLPipelinedChannel
    implements PipelinedChannel
{
    private static final Logger _logger = Logger.getLogger(SSLPipelinedChannel.class);

    private final SSLEngine _sslEngine;
    private final ByteBuffer _netDataIn;
    private final ByteBuffer _netDataOut;
    private final ByteBuffer _appDataIn;
    private final ByteBuffer _appDataOut;
    private PipelinedChannel _downstream;

    public SSLPipelinedChannel(final SSLEngine sslEngine)
    {
        if (sslEngine == null) {
            throw new IllegalArgumentException("Missing SSLEngine");
        }

        _sslEngine = sslEngine;

        // Allocate buffers for net (encrypted) data
        final int netBuffersize = _sslEngine.getSession().getPacketBufferSize();
        _netDataIn = ByteBuffer.allocate(netBuffersize);
        _netDataOut = ByteBuffer.allocate(netBuffersize);

        // Allocate buffers for application (unencrypted) data
        final int appBuffersize = _sslEngine.getSession().getApplicationBufferSize();
        _appDataIn = ByteBuffer.allocate(appBuffersize);
        _appDataOut = ByteBuffer.allocate(appBuffersize);
    }

    @Override
    public PipelinedChannelResult read(final ByteBuffer dst, final ByteChannel channel)
        throws IOException
    {
        return (_downstream != null) ? _downstream.read(dst, channel) : new PipelinedChannelResult(dst, channel.read(dst));
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public int readBuffered(final ByteBuffer dst)
    {
        return transferInput(dst);
    }

    @Override
    public PipelinedChannelResult write(final ByteBuffer src, final ByteChannel channel)
        throws IOException
    {
        return (_downstream != null) ? _downstream.write(src, channel) : new PipelinedChannelResult(src, channel.write(src));
    }

    private int transferBytes(final ByteBuffer src, final ByteBuffer dst)
    {
        int count = Math.min(src.remaining(), dst.remaining());

        for (int i = 0; i < count; i++) {
            dst.put(src.get());
        }

        return count;
    }

    private SSLEngineResult wrapOutput()
        throws SSLException
    {
        _logger.trace("wrapOutput before wrap _appDataOut" + _appDataOut.toString());
        _logger.trace("wrapOutput before wrap _netDataOut" + _netDataOut.toString());
        SSLEngineResult engineResult;

        _appDataOut.flip();
        engineResult = _sslEngine.wrap(_appDataOut, _netDataOut);
        _appDataOut.compact();
        _logger.trace("wrapOutput after wrap _appDataOut" + _appDataOut.toString());
        _logger.trace("wrapOutput after wrap _netDataOut" + _netDataOut.toString());

        return engineResult;
    }

    private int transferOutput(final ByteBuffer dst)
    {
        int count = 0;

        if (_netDataOut.position() > 0) {
            _netDataOut.flip();
            count = transferBytes(_netDataOut, dst);
            _netDataOut.compact();
        }

        return count;
    }

    @Override
    public PipelinedChannelResult wrap(final ByteBuffer src, final ByteBuffer dst)
        throws SSLException
    {
        _logger.trace("wrap on entry src " + src.toString());
        _logger.trace("wrap on entry dst " + dst.toString());
        _logger.trace("wrap on entry _appDataOut " + _appDataOut.toString());
        _logger.trace("wrap on entry _netDataOut " + _netDataOut.toString());

        int byteCount = transferOutput(dst);
        _logger.trace("wrap after first transfer dst " + dst.toString());
        _logger.trace("wrap after first transfer _netDataOut " + _netDataOut.toString());
        SSLEngineResult engineResult = null;

        if (_downstream == null) {
            boolean needsWrap = true;
            boolean isFirst = true;

            while (needsWrap) {
                SSLEngineResult wrapResult = _sslEngine.wrap(src, _netDataOut);
                _logger.trace("wrap after first wrap src " + src.toString());
                _logger.trace("wrap after first wrap _netDataOut " + _netDataOut.toString());

                if (isFirst) {
                    engineResult = wrapResult;
                    isFirst = false;
                }

                if (wrapResult.getStatus() == Status.OK) {
                    //byteCount += transferOutput(dst);
                    transferOutput(dst);
                    byteCount += wrapResult.bytesConsumed();
                    _logger.trace("wrap after second transfer dst " + dst.toString());
                    _logger.trace("wrap after second transfer _netDataOut " + _netDataOut.toString());
                    needsWrap = src.remaining() > 0;
                } else {
                    needsWrap =false;
                }
            }
        } else {
            if (_appDataOut.position() > 0) {
                engineResult = wrapOutput();

                if (engineResult.getStatus() == Status.OK) {
                    byteCount += transferOutput(dst);
                    _logger.trace("wrap after transfer 3 dst " + dst.toString());
                    _logger.trace("wrap after transfer 3 _netDataOut " + _netDataOut.toString());
                }
            }

            PipelinedChannelResult channelResult = _downstream.wrap(src, _appDataOut);
            _logger.trace("wrap after wrap 3 src " + src.toString());
            _logger.trace("wrap after wrap 3 _appDataOut " + _appDataOut.toString());
            if ((channelResult.getEngineResult() == null ) || (channelResult.getEngineResult().getStatus() == Status.OK)) {
                boolean needsWrap = true;
                boolean isFirst = true;

                while (needsWrap) {
                    SSLEngineResult wrapResult = wrapOutput();

                    if (isFirst) {
                        engineResult = wrapResult;
                        isFirst = false;
                    }

                    if (wrapResult.getStatus() == Status.OK) {
                        transferOutput(dst);
                        byteCount += wrapResult.bytesConsumed();
                        needsWrap = _appDataOut.position() > 0;
                        _logger.trace("wrap after transfer 4 dst " + dst.toString());
                        _logger.trace("wrap after transfer 4 _netDataOut " + _netDataOut.toString());
                    } else {
                        needsWrap = false;
                    }
                }
            }
        }

        return new PipelinedChannelResult(dst, engineResult, byteCount);
    }

    private int transferInput(final ByteBuffer dst)
    {
        int count = 0;

        if (_appDataIn.position() > 0) {
            _appDataIn.flip();
            count += transferBytes(_appDataIn, dst);
            _appDataIn.compact();
        }

        return count;
    }

    /**
     * Caller must call PipelinedChannelResult.getResultBuffer().compact()
     */
    @Override
    public PipelinedChannelResult unwrap(final ByteBuffer src, final ByteBuffer dst)
        throws SSLException
    {
        _logger.trace("unwrap on entry src " + src.toString());
        _logger.trace("unwrap on entry dst " + dst.toString());
        _logger.trace("unwrap on entry _appDataIn " + _appDataIn.toString());
        _logger.trace("unwrap on entry _netDataIn " + _netDataIn.toString());

        int byteCount = transferInput(dst);
        _logger.trace("unwrap after transfer 1 dst " + dst.toString());
        _logger.trace("unwrap after transfer 1 _appDataIn " + _appDataIn.toString());
        SSLEngineResult engineResult = null;

        if (_downstream == null) {
            boolean needsUnwrap = true;
            boolean isFirst = true;

            while (needsUnwrap) {
                SSLEngineResult unwrapResult = _sslEngine.unwrap(src, _appDataIn);
                _logger.trace("unwrap after unwrap 1 src " + src.toString());
                _logger.trace("unwrap after unwrap 1 _appDataIn " + _appDataIn.toString());
                _logger.trace("unwrap after unwrap 1 engine result status = " + unwrapResult.getStatus() + ", bytes consumed = " + unwrapResult.bytesConsumed() + ", bytes produced = " + unwrapResult.bytesProduced());

                if (isFirst) {
                    engineResult = unwrapResult;
                    isFirst = false;
                }

                if (unwrapResult.getStatus() == Status.OK) {
                    byteCount += transferInput(dst);
                    _logger.trace("unwrap after transfer 2 dst " + dst.toString());
                    _logger.trace("unwrap after transfer 2 _appDataIn " + _appDataIn.toString());
                    needsUnwrap = src.remaining() > 0;
                } else {
                    needsUnwrap = false;
                }
            }
        } else {
            if (_netDataIn.position() > 0) {
                _netDataIn.flip();
                _downstream.unwrap(_netDataIn, _appDataIn);
                _netDataIn.compact();
                _logger.trace("unwrap after unwrap 2 _netDataIn " + _netDataIn.toString());
                _logger.trace("unwrap after unwrap 2 _appDataIn " + _appDataIn.toString());
                byteCount += transferInput(dst);
                _logger.trace("unwrap after transfer 3 dst " + dst.toString());
                _logger.trace("unwrap after transfer 3 _appDataIn " + _appDataIn.toString());
            }

            boolean needsUnwrap = true;
            boolean isFirst = true;

            while (needsUnwrap) {
                SSLEngineResult unwrapResult = _sslEngine.unwrap(src, _netDataIn);
                _logger.trace("unwrap after unwrap 3 src " + src.toString());
                _logger.trace("unwrap after unwrap 3 _netDataIn " + _netDataIn.toString());
                _logger.trace("unwrap after unwrap 3 engine result status = " + unwrapResult.getStatus() + ", bytes consumed = " + unwrapResult.bytesConsumed() + ", bytes produced = " + unwrapResult.bytesProduced());

                if (isFirst) {
                    engineResult = unwrapResult;
                    isFirst = false;
                }

                if (unwrapResult.getStatus() == Status.OK) {
                    _netDataIn.flip();
                    _downstream.unwrap(_netDataIn, _appDataIn);
                    _netDataIn.compact();
                    _logger.trace("unwrap after unwrap 3 _netDataIn " + _netDataIn.toString());
                    _logger.trace("unwrap after unwrap 3 _appDataIn " + _appDataIn.toString());
                    byteCount += transferInput(dst);
                    _logger.trace("unwrap after transfer 4 dst " + dst.toString());
                    _logger.trace("unwrap after transfer 4 _appDataIn " + _appDataIn.toString());
                    needsUnwrap = src.remaining() > 0;
                } else {
                    needsUnwrap = false;
                }
            }
        }

        return new PipelinedChannelResult(dst, engineResult, byteCount);
    }

    @Override
    public PipelinedChannel downstream()
    {
        return _downstream;
    }

    @Override
    public void setDownstream(final PipelinedChannel downstream)
    {
        _downstream = downstream;
    }

    @Override
    public boolean hasBufferedInput()
    {
        boolean rv = (_appDataIn.position() > 0) || (_netDataIn.position() > 0);

        if (!rv && (_downstream != null)) {
            rv = _downstream.hasBufferedInput();
        }

        return rv;
    }

    @Override
    public boolean hasBufferedOutput()
    {
        boolean rv = (_appDataOut.position() > 0) || (_netDataOut.position() > 0);

        if (!rv && (_downstream != null)) {
            rv = _downstream.hasBufferedOutput();
        }

        return rv;
    }

    protected SSLEngine getSSLEngine()
    {
        return _sslEngine;
    }
}
