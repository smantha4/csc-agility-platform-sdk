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

package com.servicemesh.io.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import javax.net.ssl.SSLException;

public interface PipelinedChannel
{
    /**
     * Read incoming data through the pipeline.
     * 
     * @param dst Buffer to hold incoming data.
     * @param channel I/O channel to read from.
     * @return Object containing the number of bytes read and any SSL engine result.
     * @throws IOException Throws exception on I/O failures.
     */
    public PipelinedChannelResult read(final ByteBuffer dst, final ByteChannel channel) throws IOException;

    /**
     * Reads buffered data in the pipeline into the destination buffer.
     * 
     * @param dst Buffer to hold incoming data.
     * @return The number of bytes placed into the destination buffer.
     */
    public int readBuffered(final ByteBuffer dst);

    /**
     * Writes outgoing data through the pipeline.
     * 
     * @param src Buffer holding the data to be written.
     * @param channel I/O channel to write to.
     * @return Object containing the number of bytes written and any SSL engine result.
     * @throws IOException Throws exception on I/O failures.
     */
    public PipelinedChannelResult write(final ByteBuffer src, final ByteChannel channel) throws IOException;

    /**
     * Encodes data using pipelined SSL engines.
     * 
     * @param src Buffer holding the data to be encoded.
     * @param dst Buffer to hold the encoded data.
     * @return Object containing the number of bytes encoded and the SSL engine result.
     * @throws SSLException
     */
    public PipelinedChannelResult wrap(final ByteBuffer src, final ByteBuffer dst) throws SSLException;

    /**
     * Decodes data using pipelined SSL engines.
     * 
     * @param src Buffer holding the data to be decoded.
     * @param dst Buffer to hold the decoded data.
     * @return Object containing the number of bytes decoded and the SSL engine result.
     * @throws SSLException
     */
    public PipelinedChannelResult unwrap(final ByteBuffer src, final ByteBuffer dst) throws SSLException;

    /**
     * Retrieve the next downstream pipeline.
     * 
     * @return The next downstream pipeline.
     */
    public PipelinedChannel downstream();

    /**
     * Sets the downstream pipeline.
     * 
     * @param downstream The downstream pipeline to set.
     */
    public void setDownstream(final PipelinedChannel downstream);

    /**
     * Test to see if the pipeline contains any buffered input.
     * 
     * @return True if the pipeline contains buffered input, otherwise false.
     */
    public boolean hasBufferedInput();

    /**
     * Test to see if the pipeline contains any buffered output.
     * 
     * @return True if the pipeline contains buffered output, otherwise false.
     */
    public boolean hasBufferedOutput();
}
