package com.servicemesh.io.http;

public class HttpClientException extends RuntimeException
{

    private static final long serialVersionUID = 20131015;

    public HttpClientException()
    {
    }

    public HttpClientException(String msg)
    {
        super(msg);
    }

    public HttpClientException(Throwable t)
    {
        super(t);
    }

    public HttpClientException(String msg, Throwable t)
    {
        super(msg, t);
    }

}
