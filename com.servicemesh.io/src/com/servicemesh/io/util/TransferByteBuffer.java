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
 * A buffer used to transfer data. Internally, this class is backed by an
 * instance of {@link ByteBuffer}.
 * <p>
 * This class is not thread safe.
 */
public class TransferByteBuffer
{
    protected ByteBuffer _buffer;

    /**
     * Allocates a buffer of the specified size.
     * 
     * @param bufSize The size of the buffer to allocate.
     */
    public TransferByteBuffer(int bufSize)
    {
        Preconditions.checkArgument(bufSize > 0, "Buffer size must be > 0");

        _buffer = ByteBuffer.allocate(bufSize);
    }

    /**
     * Returns the backing buffer.
     * 
     * @return Backing buffer.
     */
    public ByteBuffer getBuffer()
    {
        return _buffer;
    }

    /**
     * Returns the remaining capacity.
     * 
     * @return The remaining capacity.
     */
    public int remaining()
    {
        return _buffer.remaining();
    }

    /**
     * Queries if the buffer has data ready for reading.
     * 
     * @return True if the buffer contains readable data, false otherwise.
     */
    public boolean hasData()
    {
        return _buffer.position() > 0;
    }

    /**
     * Invokes producer to read data into the buffer.
     * 
     * @param producer Producer that will read content.
     * @return The number of bytes read.
     * @throws IOException
     */
    public int fill(ContentProducer producer)
        throws IOException
    {
        Preconditions.checkNotNull(producer, "Missing producer");
        int bytesRead = 0;
        int totalRead = 0;

        while ((bytesRead = producer.produce(_buffer)) > 0) {
            totalRead += bytesRead;
        }

        return totalRead;
    }

    /**
     * Invokes the consumer to output data from the buffer.
     * 
     * @param consumer Consumer that will output data.
     * @return The number of bytes written.
     * @throws IOException
     */
    public int flush(ContentConsumer consumer)
        throws IOException
    {
        Preconditions.checkNotNull(consumer, "Missing consumer");
        int bytesWritten = 0;

        if (hasData()) {
            _buffer.flip();
            bytesWritten += consumer.consume(_buffer);
            _buffer.compact();
        }

        return bytesWritten;
    }
}
