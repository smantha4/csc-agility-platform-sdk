package com.servicemesh.io.http;

import java.io.Serializable;

public abstract class UrlContext implements Serializable
{
    private static final long serialVersionUID = 20131116;

    public static int DEFAULT_PORT = 80;

    private String protocol;
    private int port;
    private String host;
    private String path;
    private QueryParams queryParams;

    public UrlContext()
    {
        port = getDefaultPort();
    }

    public UrlContext(String protocol, String host, int port, String path, QueryParams queryParams)
    {
        this.protocol = (protocol != null ? protocol.toLowerCase() : null);
        this.port = port;
        this.host = host;
        this.path = path;
        this.queryParams = queryParams;
    }

    public UrlContext(String protocol, String host, int port, String path)
    {
        this.protocol = (protocol != null ? protocol.toLowerCase() : null);
        this.port = port;
        this.host = host;
        this.path = path;
    }

    public UrlContext(String protocol, String host, String path, QueryParams queryParams)
    {
        this();
        this.protocol = protocol;
        this.host = host;
        this.path = path;
        this.queryParams = queryParams;
    }

    public UrlContext(String protocol, String host, String path)
    {
        this();
        this.protocol = protocol;
        this.host = host;
        this.path = path;
    }

    public UrlContext(String protocol, String host)
    {
        this();
        this.protocol = protocol;
        this.host = host;
        path = "/";
    }

    public UrlContext(String protocol, String host, int port)
    {
        this(protocol, host, port, "/");
    }

    public abstract boolean isSecure();

    public String toString(boolean encode)
    {
        return buildString(encode);
    }

    @Override
    public String toString()
    {
        return buildString(true);
    }

    public String getUrl(boolean includeProtocol, boolean includeHost, boolean encode)
    {
        StringBuilder sb = new StringBuilder();

        if (includeProtocol)
        {
            sb.append(protocol + "://");
        }

        if (includeHost)
        {
            sb.append(host + ":" + port);
        }

        sb.append((path.startsWith("/") ? path : ("/" + path)) + (queryParams != null ? queryParams.asQueryString(encode) : ""));

        return sb.toString();
    }

    private String buildString(boolean encode)
    {
        return protocol + "://" + host + ":" + port + (path.startsWith("/") ? path : ("/" + path))
                + (queryParams != null ? queryParams.asQueryString(encode) : "");
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public QueryParams getQueryParams()
    {
        return queryParams;
    }

    public void setQueryParams(QueryParams queryParams)
    {
        this.queryParams = queryParams;
    }

    public int getDefaultPort()
    {
        return DEFAULT_PORT;
    }

}
