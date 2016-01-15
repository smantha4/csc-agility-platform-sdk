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

import javax.net.ssl.SSLException;

import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.PipelinedChannelResult;

public class PassThroughChannel implements PipelinedChannel
{
    private PipelinedChannel _downstream;

    public PassThroughChannel()
    {
    }

    @Override
    public PipelinedChannelResult read(final ByteBuffer dst, final ByteChannel channel) throws IOException
    {
        return (_downstream != null) ? _downstream.read(dst, channel) : new PipelinedChannelResult(dst, channel.read(dst));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readBuffered(final ByteBuffer dst)
    {
        return (_downstream != null) ? _downstream.readBuffered(dst) : 0;
    }

    @Override
    public PipelinedChannelResult write(final ByteBuffer src, final ByteChannel channel) throws IOException
    {
        return (_downstream != null) ? _downstream.write(src, channel) : new PipelinedChannelResult(src, channel.write(src));
    }

    @Override
    public PipelinedChannelResult wrap(final ByteBuffer src, final ByteBuffer dst) throws SSLException
    {
        PipelinedChannelResult channelResult;

        if (_downstream != null)
        {
            channelResult = _downstream.wrap(src, dst);
        }
        else
        {
            int count = transferBytes(src, dst);

            channelResult = new PipelinedChannelResult(dst, null, count);
        }

        return channelResult;
    }

    /**
     * Caller must call PipelinedChannelResult.getResultBuffer().compact()
     */
    @Override
    public PipelinedChannelResult unwrap(final ByteBuffer src, final ByteBuffer dst) throws SSLException
    {
        PipelinedChannelResult channelResult;

        if (_downstream != null)
        {
            channelResult = _downstream.unwrap(src, dst);
        }
        else
        {
            int count = transferBytes(src, dst);

            channelResult = new PipelinedChannelResult(dst, null, count);
        }

        return channelResult;
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
        return (_downstream != null) ? _downstream.hasBufferedInput() : false;
    }

    @Override
    public boolean hasBufferedOutput()
    {
        return (_downstream != null) ? _downstream.hasBufferedOutput() : false;
    }

    private int transferBytes(ByteBuffer src, final ByteBuffer dst)
    {
        int count = 0;

        count = Math.min(src.remaining(), dst.remaining());

        for (int i = 0; i < count; i++)
        {
            dst.put(src.get());
        }

        return count;
    }
}
