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
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionBufferStatus;

import com.google.common.base.Preconditions;
import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.ProxyByteChannel;
import com.servicemesh.io.util.ExpandableByteBuffer;

public class HttpProxyIOSession implements IOSession
{
    private final IOSession _ioSession;
    private final ProxyByteChannel _channel;
    private final PipelinedChannel _pipelinedChannel;

    public HttpProxyIOSession(final IOSession ioSession, final PipelinedChannel pipelinedChannel)
    {
        _ioSession = Preconditions.checkNotNull(ioSession, "Missing IOSession");
        _pipelinedChannel = pipelinedChannel;
        _channel = new HttpProxyInternalByteChannel();
    }

    @Override
    public ByteChannel channel()
    {
        return _channel;
    }

    @Override
    public SocketAddress getRemoteAddress()
    {
        return _ioSession.getRemoteAddress();
    }

    @Override
    public SocketAddress getLocalAddress()
    {
        return _ioSession.getLocalAddress();
    }

    @Override
    public int getEventMask()
    {
        return _ioSession.getEventMask();
    }

    @Override
    public void setEventMask(int ops)
    {
        _ioSession.setEventMask(ops);
    }

    @Override
    public void setEvent(int op)
    {
        _ioSession.setEvent(op);
    }

    @Override
    public void clearEvent(int op)
    {
        _ioSession.clearEvent(op);
    }

    @Override
    public void close()
    {
        _ioSession.close();
    }

    @Override
    public void shutdown()
    {
        _ioSession.shutdown();
    }

    @Override
    public int getStatus()
    {
        return _ioSession.getStatus();
    }

    @Override
    public boolean isClosed()
    {
        return _ioSession.isClosed();
    }

    @Override
    public int getSocketTimeout()
    {
        return _ioSession.getSocketTimeout();
    }

    @Override
    public void setSocketTimeout(int timeout)
    {
        _ioSession.setSocketTimeout(timeout);
    }

    @Override
    public void setBufferStatus(SessionBufferStatus status)
    {
        _ioSession.setBufferStatus(status);
    }

    @Override
    public boolean hasBufferedInput()
    {
        return _ioSession.hasBufferedInput() || _channel.hasBufferedInput();
    }

    @Override
    public boolean hasBufferedOutput()
    {
        return _ioSession.hasBufferedOutput() || _channel.hasBufferedOutput();
    }

    @Override
    public void setAttribute(String name, Object obj)
    {
        _ioSession.setAttribute(name, obj);
    }

    @Override
    public Object getAttribute(String name)
    {
        return _ioSession.getAttribute(name);
    }

    @Override
    public Object removeAttribute(String name)
    {
        return _ioSession.removeAttribute(name);
    }

    private class HttpProxyInternalByteChannel implements ProxyByteChannel
    {
        private final ExpandableByteBuffer _netDataIn;
        private final ExpandableByteBuffer _netDataOut;
        private final PipelinedChannelProducer _producer;
        private final PipelinedChannelConsumer _consumer;

        public HttpProxyInternalByteChannel()
        {
            _netDataIn = new ExpandableByteBuffer(8 * 1024);
            _netDataOut = new ExpandableByteBuffer(8 * 1024);
            _consumer = (_pipelinedChannel != null) ? new PipelinedChannelConsumer(_pipelinedChannel, _ioSession) : null;
            _producer = (_pipelinedChannel != null) ? new PipelinedChannelProducer(_pipelinedChannel, _ioSession) : null;
        }

        @Override
        public int write(final ByteBuffer src) throws IOException
        {
            if (_pipelinedChannel == null)
            {
                return _ioSession.channel().write(src);
            }
            else
            {
                // First flush any buffered data
                int bytesWritten = _netDataOut.flush(_consumer);

                // Wrap outgoing data and flush
                bytesWritten += _netDataOut.fill(new PipelinedSSLProducer(_pipelinedChannel, src));
                _netDataOut.flush(_consumer);

                return bytesWritten;
            }
        }

        @Override
        public int read(final ByteBuffer dst) throws IOException
        {
            if (_pipelinedChannel == null)
            {
                return _ioSession.channel().read(dst);
            }
            else
            {
                // Read any buffered data in the pipeline
                int totalBytesRead = _pipelinedChannel.readBuffered(dst);
                PipelinedSSLConsumer sslConsumer = new PipelinedSSLConsumer(_pipelinedChannel, dst);

                // Unwrap buffered data
                totalBytesRead += _netDataIn.flush(sslConsumer);

                // Read incoming and flush
                int bytesRead = _netDataIn.fill(_producer);
                if (bytesRead >= 0)
                {
                    totalBytesRead += _netDataIn.flush(sslConsumer);
                }
                else
                {
                    if (bytesRead == -1)
                    {
                        // Channel is closed, if we had buffered data send that.
                        // The next read should return a -1.
                        if (totalBytesRead == 0)
                        {
                            totalBytesRead = -1;
                        }
                    }
                }

                return totalBytesRead;
            }
        }

        @Override
        public void close() throws IOException
        {
            HttpProxyIOSession.this.close();
        }

        @Override
        public boolean isOpen()
        {
            return !isClosed();
        }

        @Override
        public boolean hasBufferedInput()
        {
            boolean rv = _netDataIn.hasData();

            if (!rv && (_pipelinedChannel != null))
            {
                rv = _pipelinedChannel.hasBufferedInput();
            }

            return rv;
        }

        @Override
        public boolean hasBufferedOutput()
        {
            boolean rv = _netDataOut.hasData();

            if (!rv && (_pipelinedChannel != null))
            {
                rv = _pipelinedChannel.hasBufferedOutput();
            }

            return rv;
        }
    }
}
