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
import com.servicemesh.io.util.ContentProducer;

public class PipelinedSSLProducer
    implements ContentProducer
{
    private final ByteBuffer _src;
    private final PipelinedChannel _pipelinedChannel;

    public PipelinedSSLProducer(PipelinedChannel pipelinedChannel, ByteBuffer src)
    {
        Preconditions.checkNotNull(pipelinedChannel, "Missing PipelinedChannel");
        Preconditions.checkNotNull(src, "Missing source buffer");

        _pipelinedChannel = pipelinedChannel;
        _src = src;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public int produce(ByteBuffer dst)
        throws IOException
    {
        Preconditions.checkNotNull(dst, "Missing destination buffer");
        PipelinedChannelResult result = _pipelinedChannel.wrap(_src, dst);

        return result.getByteCount();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public int remaining()
    {
        return _src.hasRemaining() ? 1024 : 0;
    }
}
