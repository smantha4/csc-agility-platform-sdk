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

package com.servicemesh.io.http.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.servicemesh.io.http.HttpMethod;
import com.servicemesh.io.http.HttpVersion;
import com.servicemesh.io.http.IHttpHeader;
import com.servicemesh.io.http.IHttpRequest;

public class DefaultHttpRequest implements IHttpRequest
{
    private static final Logger _logger = Logger.getLogger(DefaultHttpRequest.class);

    private HttpVersion _httpVersion;
    final private HttpMethod _method;
    private URI _uri;
    final private List<IHttpHeader> _headers = new ArrayList<IHttpHeader>();
    private String _stringContent;
    private byte[] _byteContent;
    private InputStream _streamContent;
    private long _contentLength = 0;
    private Integer _requestTimeout = null;

    public DefaultHttpRequest(final HttpMethod method)
    {
        if (method == null)
        {
            throw new IllegalArgumentException("Missing HttpMethod");
        }

        _method = method;
        _httpVersion = (_method != HttpMethod.CONNECT) ? HttpVersion.HTTP_1_1 : HttpVersion.HTTP_1_0;
    }

    public DefaultHttpRequest(final HttpMethod method, final URI uri)
    {
        this(method);

        if (uri == null)
        {
            throw new IllegalArgumentException("Missing URI");
        }

        _uri = uri;
    }

    public DefaultHttpRequest(final HttpMethod method, final String uri)
    {
        this(method);

        if ((uri == null) || uri.isEmpty())
        {
            throw new IllegalArgumentException("Invalid uri: " + uri);
        }

        try
        {
            _uri = new URI(uri);
        }
        catch (URISyntaxException ex)
        {
            throw new IllegalArgumentException("Invalid uri: " + uri);
        }
    }

    @Override
    public HttpVersion getHttpVersion()
    {
        return _httpVersion;
    }

    @Override
    public HttpMethod getMethod()
    {
        return _method;
    }

    @Override
    public void setUri(final String uri)
    {
    }

    @Override
    public void setUri(final URI uri)
    {
    }

    @Override
    public URI getUri()
    {
        return _uri;
    }

    @Override
    public void addHeader(final IHttpHeader header)
    {
        _headers.add(header);
    }

    @Override
    public void setHeader(final IHttpHeader header)
    {
        if (header != null)
        {
            removeHeaders(header.getName());
            _headers.add(header);
        }
    }

    @Override
    public void setHeaders(final List<IHttpHeader> headers)
    {
        if (headers != null)
        {
            for (IHttpHeader header : headers)
            {
                setHeader(header);
            }
        }
    }

    @Override
    public IHttpHeader getHeader(final String name)
    {
        return findFirstHeader(name);
    }

    @Override
    public List<IHttpHeader> getHeaders(final String name)
    {
        return findHeaders(name);
    }

    @Override
    public List<IHttpHeader> getHeaders()
    {
        return _headers;
    }

    @Override
    public IHttpHeader removeHeader(final String name)
    {
        IHttpHeader current = findFirstHeader(name);

        if (current != null)
        {
            _headers.remove(current);
        }

        return current;
    }

    @Override
    public List<IHttpHeader> removeHeaders(final String name)
    {
        List<IHttpHeader> current = findHeaders(name);

        for (IHttpHeader nextHeader : current)
        {
            _headers.remove(nextHeader);
        }

        return current;
    }

    @Override
    public void setContent(final String content)
    {
        _stringContent = content;
        _byteContent = null;
        _streamContent = null;
    }

    @Override
    public void setContent(final byte[] content)
    {
        if ((content != null) && (content.length > 0))
        {
            _byteContent = new byte[content.length];
            System.arraycopy(content, 0, _byteContent, 0, content.length);
        }
        else
        {
            _byteContent = content;
        }

        _stringContent = null;
        _streamContent = null;
    }

    @Override
    public void setContent(final InputStream content)
    {
        _streamContent = content;
        _byteContent = null;
        _stringContent = null;
        _contentLength = -1; //  Correct setting for stream of unknown length
    }

    @Override
    public void setContent(final InputStream content, long size)
    {
        _streamContent = content;
        _contentLength = size;
        _byteContent = null;
        _stringContent = null;
    }

    @Override
    public String getContent()
    {
        String content = null;

        if (_stringContent != null)
        {
            content = _stringContent;
        }
        else if (_byteContent != null)
        {
            content = new String(_byteContent);
        }
        else if (_streamContent != null)
        {
            try
            {
                content = convertInputStream(_streamContent);
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex.getLocalizedMessage(), ex);
            }
            finally
            {
                // Can only do this once
                _streamContent = null;
            }
        }

        return content;
    }

    @Override
    public byte[] getContentAsByteArray()
    {
        byte[] content = null;

        if (_stringContent != null)
        {
            content = _stringContent.getBytes();
        }
        else if (_byteContent != null)
        {
            content = _byteContent;
        }
        else if (_streamContent != null)
        {
            try
            {
                content = ByteStreams.toByteArray(_streamContent);
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex.getLocalizedMessage(), ex);
            }
            finally
            {
                try
                {
                    _streamContent.close();
                }
                catch (IOException ex)
                {
                    // Ignore
                }

                // Can only do this once
                _streamContent = null;
            }
        }

        return content;
    }

    @Override
    public InputStream getContentAsStream()
    {
        InputStream is = null;

        if (_streamContent != null)
        {
            is = _streamContent;
        }
        else if (_stringContent != null)
        {
            byte[] stringBytes = _stringContent.getBytes();

            _contentLength = stringBytes.length;
            is = new ByteArrayInputStream(stringBytes);
        }
        else if (_byteContent != null)
        {
            _contentLength = _byteContent.length;
            is = new ByteArrayInputStream(_byteContent);
        }

        return is;
    }

    @Override
    public long getContentLength()
    {
        return _contentLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequestTimeout(final int timeout)
    {
        _requestTimeout = new Integer(timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getRequestTimeout()
    {
        return _requestTimeout;
    }

    private IHttpHeader findFirstHeader(final String name)
    {
        IHttpHeader found = null;

        for (IHttpHeader candidate : _headers)
        {
            if (candidate.getName().equalsIgnoreCase(name))
            {
                found = candidate;
                break;
            }
        }

        return found;
    }

    private List<IHttpHeader> findHeaders(final String name)
    {
        List<IHttpHeader> headersFound = new ArrayList<IHttpHeader>();

        for (IHttpHeader candidate : _headers)
        {
            if (candidate.getName().equalsIgnoreCase(name))
            {
                headersFound.add(candidate);
            }
        }

        return headersFound;
    }

    private String convertInputStream(final InputStream stream) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        try
        {
            return CharStreams.toString(reader);
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException ex)
            {
                // Ignore
            }
        }
    }
}
