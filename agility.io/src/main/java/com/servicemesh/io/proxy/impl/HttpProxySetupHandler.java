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

import javax.net.ssl.SSLException;
import javax.xml.bind.DatatypeConverter;

import com.servicemesh.io.http.HttpVersion;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.util.Args;

import com.servicemesh.io.proxy.PipelinedChannel;
import com.servicemesh.io.proxy.ProxySetupHandler;

public class HttpProxySetupHandler implements ProxySetupHandler
{
    private final ProxyHost _proxyHost;
    private final IOSession _ioSession;
    private final PipelinedChannel _pipelinedChannel;
    private ByteBuffer _appDataIn;
    private ByteBuffer _netDataIn;
    private ByteBuffer _netDataOut;

    protected static final String CRLF = "\r\n";

    public HttpProxySetupHandler(final ProxyHost proxyHost, final IOSession ioSession, final PipelinedChannel pipelinedChannel)
    {
        _proxyHost = Args.notNull(proxyHost, "proxy host");
        Args.notNull(proxyHost.getTargetHost(), "target host");
        _ioSession = Args.notNull(ioSession, "IOSession");
        _pipelinedChannel = pipelinedChannel;
    }

    @Override
    public PipelinedChannel initialize() throws IOException
    {
        HttpRequest request = generateConnectRequest();

        if (request != null)
        {
            writeRequest(request);

            HttpResponse response = readResponse();

            if (response != null)
            {
                int status = response.getStatusLine().getStatusCode();

                if ((status < 200) || (status > 299))
                {
                    throw new RuntimeException("Proxy connect failed: " + response.getStatusLine().getReasonPhrase());
                }
            }
            else
            {
                throw new RuntimeException("Proxy connect failed to return a valid response");
            }
        }

        return (_proxyHost.getTargetHost() instanceof ProxyHost) ? new PassThroughChannel() : null;
    }

    protected ProxyHost getProxyHost()
    {
        return _proxyHost;
    }

    protected HttpRequest generateConnectRequest() throws IOException
    {
        HttpRequest request = null;

        String uri = _proxyHost.getTargetHost().getHostName() + ":" + _proxyHost.getTargetHost().getPort();
        HttpVersion parsedHttpVersion = HttpVersion.parseHttpVersionValue(_proxyHost.getHttpVersion());
        if (parsedHttpVersion == null) {
            throw new RuntimeException("Invalid HttpVersion. some examples are HTTP/1.0, HTTP/1.1 ");
        }
        ProtocolVersion protocolVersion = new ProtocolVersion(parsedHttpVersion.getProtocol(), parsedHttpVersion.getMajorVersion(), parsedHttpVersion.getMinorVersion());
        request = new BasicHttpRequest("CONNECT", uri, protocolVersion);
        if(HttpVersion.isHostHeaderRequired(_proxyHost.getHttpVersion())) {
            request.addHeader("Host", _proxyHost.getTargetHost().getHostName() + ":" + _proxyHost.getTargetHost().getPort());
        }
        if ((_proxyHost.getPrincipal() != null) && !_proxyHost.getPrincipal().isEmpty())
        {
            StringBuilder builder = new StringBuilder(_proxyHost.getPrincipal());

            if ((_proxyHost.getCredentials() != null) && !_proxyHost.getCredentials().isEmpty())
            {
                builder.append(":");
                builder.append(_proxyHost.getCredentials());
            }

            String encodedAuthString = DatatypeConverter.printBase64Binary(builder.toString().getBytes("UTF-8"));
            request.addHeader("Proxy-Authorization", "Basic " + encodedAuthString);
        }

        request.addHeader("Pragma", "No-Cache");
        request.addHeader("Proxy-Connection", "Keep-Alive");
        request.addHeader("User-Agent", "Agility Proxy Chain Agent");

        return request;
    }

    private void writeRequest(final HttpRequest request) throws IOException
    {
        StringBuilder builder = new StringBuilder();
        RequestLine requestLine = request.getRequestLine();

        builder.append(requestLine.getMethod());
        builder.append(" ");
        builder.append(requestLine.getUri());
        builder.append(" ");
        builder.append(requestLine.getProtocolVersion().toString());
        builder.append(CRLF);

        for (Header header : request.getAllHeaders())
        {
            builder.append(header.toString());
            builder.append(CRLF);
        }

        builder.append(CRLF);

        ByteBuffer byteBuffer = ByteBuffer.wrap(builder.toString().getBytes());

        write(byteBuffer);
    }

    private HttpResponse readResponse() throws IOException
    {
        StatusLine statusLine = processStatusLine();
        HttpResponse response = new BasicHttpResponse(statusLine);

        processHeaders(response);

        return response;
    }

    private StatusLine processStatusLine() throws IOException
    {
        String statusLine = readLine();

        while (statusLine.trim().length() == 0)
        {
            statusLine = readLine();
        }
        statusLine = statusLine.trim();

        String[] tokens = statusLine.split(" ");
        ProtocolVersion protocolVersion = processProtocolVersion(tokens[0]);
        int statusCode = Integer.parseInt(tokens[1]);
        String reason = rebuildReason(tokens);

        return new BasicStatusLine(protocolVersion, statusCode, reason);
    }

    private String rebuildReason(final String[] statusTokens)
    {
        StringBuilder builder = new StringBuilder();

        if ((statusTokens != null) && (statusTokens.length > 2))
        {
            for (int i = 2; i < statusTokens.length; i++)
            {
                builder.append(statusTokens[i]);
                builder.append(" ");
            }
        }

        return builder.toString().trim();
    }

    private ProtocolVersion processProtocolVersion(final String httpVersion)
    {
        String[] tokens = httpVersion.split("/");
        String[] versionTokens = tokens[1].split("\\.");
        int major = Integer.parseInt(versionTokens[0]);
        int minor = Integer.parseInt(versionTokens[1]);

        return new ProtocolVersion(tokens[0], major, minor);
    }

    private void processHeaders(final HttpResponse response) throws IOException
    {
        String headerLine = readLine();

        while (headerLine.length() > 0)
        {
            int index = headerLine.indexOf(":");
            if (index == -1)
            {
                throw new IOException("Corrupt header-field: '" + headerLine + "'");
            }

            response.addHeader(headerLine.substring(0, index).trim(), headerLine.substring(index + 1).trim());
            headerLine = readLine();
        }

        // Read last \n
        ByteBuffer buffer = ByteBuffer.allocate(1);
        //buffer.rewind();
        read(buffer);
    }

    private String readLine() throws IOException
    {
        StringBuilder lineBuilder = new StringBuilder();
        int c;
        ByteBuffer buffer = ByteBuffer.allocate(1);

        while (true)
        {
            buffer.rewind();
            read(buffer);
            buffer.rewind();
            c = buffer.get() & 0xff;

            if (c == -1)
            {
                throw new IOException("HttpResponse corrupt, input stream closed from " + _proxyHost.getHostName());
            }

            if (c == '\n')
            {
                continue;
            }

            if (c != '\r')
            {
                lineBuilder.append((char) c);
            }
            else
            {
                break;
            }
        }

        return new String(lineBuilder);
    }

    private int read(final ByteBuffer dst) throws IOException
    {
        int bytesRead = 0;

        if (_pipelinedChannel == null)
        {
            while (bytesRead == 0)
            {
                bytesRead = _ioSession.channel().read(dst);
            }
        }
        else
        {
            if (_netDataIn == null)
            {
                _netDataIn = ByteBuffer.allocate(8192);
            }

            if (_appDataIn == null)
            {
                _appDataIn = ByteBuffer.allocate(8192);
            }

            if (_appDataIn.position() == 0)
            {
                while (bytesRead == 0)
                {
                    bytesRead = _ioSession.channel().read(_netDataIn);
                }

                _netDataIn.flip();
                _pipelinedChannel.unwrap(_netDataIn, _appDataIn);
                _netDataIn.compact();
            }

            if (_appDataIn.position() > 0)
            {
                _appDataIn.flip();

                int count = Math.min(dst.remaining(), _appDataIn.remaining());
                for (int i = 0; i < count; i++)
                {
                    dst.put(_appDataIn.get());
                }

                _appDataIn.compact();
                bytesRead = count;
            }
        }

        return bytesRead;
    }

    private int write(final ByteBuffer src) throws SSLException, IOException
    {
        int bytesWritten = 0;

        if (_pipelinedChannel == null)
        {
            bytesWritten = _ioSession.channel().write(src);
        }
        else
        {
            if (_netDataOut == null)
            {
                _netDataOut = ByteBuffer.allocate(8192);
            }

            _pipelinedChannel.wrap(src, _netDataOut);
            if (_netDataOut.position() > 0)
            {
                _netDataOut.flip();
                bytesWritten = _ioSession.channel().write(_netDataOut);
                _netDataOut.compact();
            }
        }

        return bytesWritten;
    }
}
