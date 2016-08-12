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

package com.servicemesh.io.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.common.base.Preconditions;

/**
 * A buffer that can expand its capacity on demand. Internally, this class is backed by an instance of {@link ByteBuffer}.
 * <p>
 * This class is not thread safe.
 */
public class ExpandableByteBuffer extends TransferByteBuffer
{
    /**
     * Allocates a buffer of the specified size.
     *
     * @param bufSize
     *            Size of the buffer to allocate.
     */
    public ExpandableByteBuffer(int bufSize)
    {
        super(bufSize);
    }

    /**
     * Invokes producer to read data into the buffer. The backing buffer will be reallocated to allow for data the producer
     * supplies.
     *
     * @param producer
     *            Producer that will read content.
     * @return The number of bytes read.
     * @throws IOException
     */
    @Override
    public int fill(ContentProducer producer) throws IOException
    {
        Preconditions.checkNotNull(producer, "Missing producer");

        requiredIncrement(producer.remaining());

        // Do a read first in case the channel is closed
        int bytesRead = producer.produce(_buffer);
        int totalRead = (bytesRead > 0) ? 0 : bytesRead;

        while (bytesRead > 0)
        {
            totalRead += bytesRead;
            requiredIncrement(producer.remaining());
            bytesRead = producer.produce(_buffer);
        }

        return totalRead;
    }

    private void requiredIncrement(int increment)
    {
        Preconditions.checkArgument(increment >= 0, "Increment must be >= 0");

        if (increment > remaining())
        {
            expand(_buffer.capacity() + increment);
        }
    }

    private void expand(int bufSize)
    {
        if (bufSize > _buffer.capacity())
        {
            final ByteBuffer newBuffer = ByteBuffer.allocate(bufSize);

            _buffer.flip();
            newBuffer.put(_buffer);
            _buffer = newBuffer;
        }
    }
}
