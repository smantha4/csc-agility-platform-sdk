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

import java.nio.ByteBuffer;

import javax.net.ssl.SSLEngineResult;

public class PipelinedChannelResult
{
    private final int _byteCount;
    private final ByteBuffer _resultBuffer;
    private final SSLEngineResult _engineResult;

    public PipelinedChannelResult(final ByteBuffer resultBuffer, final int byteCount)
    {
        this(resultBuffer, (SSLEngineResult) null, byteCount);
    }

    public PipelinedChannelResult(final ByteBuffer resultBuffer, final SSLEngineResult engineResult, final int byteCount)
    {
        _resultBuffer = resultBuffer;
        _engineResult = engineResult;
        _byteCount = byteCount;
    }

    public ByteBuffer getResultBuffer()
    {
        return _resultBuffer;
    }

    public int getByteCount()
    {
        return _byteCount;
    }

    public SSLEngineResult getEngineResult()
    {
        return _engineResult;
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer("Byte count = ");

        sb.append(_byteCount);

        if (_engineResult != null)
        {
            sb.append(", ");
            sb.append(_engineResult.toString());
        }

        if (_resultBuffer != null)
        {
            sb.append(", ");
            sb.append(_resultBuffer.toString());
        }

        return sb.toString();
    }
}
