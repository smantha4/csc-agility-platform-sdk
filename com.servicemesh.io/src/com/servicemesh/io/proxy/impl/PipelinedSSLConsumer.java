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

import com.google.common.base.Preconditions;
import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.PipelinedChannelResult;
import com.servicemesh.io.util.ContentConsumer;

public class PipelinedSSLConsumer implements ContentConsumer
{
    private final ByteBuffer _dest;
    private final PipelinedChannel _pipelinedChannel;

    public PipelinedSSLConsumer(PipelinedChannel pipelinedChannel, ByteBuffer dest)
    {
        Preconditions.checkNotNull(pipelinedChannel, "Missing PipelinedChannel");
        Preconditions.checkNotNull(dest, "Missing buffer");

        _pipelinedChannel = pipelinedChannel;
        _dest = dest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int consume(ByteBuffer src) throws IOException
    {
        Preconditions.checkNotNull(src, "Missing source buffer");

        // src should already be flipped ready for reading
        PipelinedChannelResult result = _pipelinedChannel.unwrap(src, _dest);

        return result.getByteCount();
    }
}
