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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.servicemesh.io.http.IHttpHeader;
import com.servicemesh.io.http.IHttpResponse;
import com.servicemesh.io.http.HttpStatus;

public class DefaultHttpResponse
    implements IHttpResponse
{
    private static final Logger _logger = Logger.getLogger(DefaultHttpResponse.class);

    private List<IHttpHeader> _headers = new ArrayList<IHttpHeader>();
    private HttpStatus _status;
    private byte[] _value;
    private InputStream _stream = null;

    public DefaultHttpResponse()
    {
    }

    @Override
    public void close()
    {
        // Make sure the stream is closed
        if (_stream != null) {
            byte[] bytes = new byte[4096];

            try {
                while (_stream.read(bytes) > 0) {
                    // Nothing to do, just draining
                }
            } catch (IOException ex) {
                // Ignore
            } finally {
                try {
                    _stream.close();
                } catch (IOException ex) {
                    // Ignore
                }

                _stream = null;
            }
        }

        _value = null;
    }

    @Override
    public void finalize()
    {
        close();
    }

    @Override
    public List<IHttpHeader> getHeaders()
    {
        return _headers;
    }

    @Override
    public IHttpHeader getHeader(String name)
    {
        return findFirstHeader(name);
    }

    @Override
    public List<IHttpHeader> getHeaders(final String name)
    {
        final List<IHttpHeader> headers = findHeaders(name);

        return Collections.unmodifiableList(headers);
    }

    public void addHeader(final IHttpHeader header)
    {
        if (header != null) {
            _headers.add(header);
        }
    }

    @Override
    public HttpStatus getStatus()
    {
        return _status;
    }

    public void setStatus(HttpStatus status)
    {
        _status = status;
    }

    @Override
    public int getStatusCode()
    {
        return (_status != null) ? _status.getStatusCode() : -1;
    }

    @Override
    public String getContent()
    {
    	//  First, convert from InputStream is that has not been done already:
    	if (_stream != null)
    	{
    		try
    		{
	    		ByteArrayOutputStream os = new ByteArrayOutputStream();  // For large streams, this will blow up
	    		int n;
	    		byte[] data = new byte[16384];
	    		while ((n = _stream.read(data, 0, data.length))!= -1) 
	    			os.write(data, 0, n);
	    		os.flush();
	    		
	    		_value = os.toByteArray();
	    		_stream.close();
	    		_stream = null;  //  Don't do this again...
    		}
    		catch (IOException ex)
    		{
    		}
    	}
        return ((_value != null) && (_value.length > 0)) ? new String(_value) : null;
    }

    @Override
    public byte[] getContentAsByteArray()
    {
    	getContent();
        return _value;
    }

    @Override
    public InputStream getContentAsStream()
    {
    	if (_stream != null)
    		return _stream;
    	
    	getContent();
        return ((_value != null) && (_value.length > 0)) ? new ByteArrayInputStream(_value) : null;
    }

    @Override
    public long getContentLength()
    {
    	getContent();
        return (_value != null) ? _value.length : 0;
    }

    public void setContent(InputStream stream)
    {
        _stream = stream;
        _value = null;
    }
    
    //  Preserved for junit testing
    public void setContent(byte[] content)
    {
        _stream = null;
        _value = content;
    }

    @Override
    public String toString()
    {
        return (_status != null) ? _status.toString() : "HttpResponse in progress";
    }

    private IHttpHeader findFirstHeader(final String name)
    {
        IHttpHeader found = null;

        for (IHttpHeader candidate : _headers) {
            if (candidate.getName().equalsIgnoreCase(name)) {
                found = candidate;
                break;
            }
        }

        return found;
    }

    private List<IHttpHeader> findHeaders(final String name)
    {
        List<IHttpHeader> found = new ArrayList<IHttpHeader>();

        for (IHttpHeader candidate : _headers) {
            if (candidate.getName().equalsIgnoreCase(name)) {
                found.add(candidate);
            }
        }

        return found;
    }
}
