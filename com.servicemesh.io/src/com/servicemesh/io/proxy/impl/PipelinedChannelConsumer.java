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

import org.apache.http.nio.reactor.IOSession;

import com.google.common.base.Preconditions;
import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.PipelinedChannelResult;
import com.servicemesh.io.util.ContentConsumer;

public class PipelinedChannelConsumer implements ContentConsumer
{
    private final IOSession _ioSession;
    private final PipelinedChannel _pipelinedChannel;

    public PipelinedChannelConsumer(PipelinedChannel pipelinedChannel, IOSession ioSession)
    {
        Preconditions.checkNotNull(pipelinedChannel, "Missing PipelinedChannel");
        Preconditions.checkNotNull(ioSession, "Missing IOSession");

        _pipelinedChannel = pipelinedChannel;
        _ioSession = ioSession;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int consume(ByteBuffer src) throws IOException
    {
        Preconditions.checkNotNull(src, "Missing source buffer");
        PipelinedChannelResult result = _pipelinedChannel.write(src, _ioSession.channel());

        return result.getByteCount();
    }
}
