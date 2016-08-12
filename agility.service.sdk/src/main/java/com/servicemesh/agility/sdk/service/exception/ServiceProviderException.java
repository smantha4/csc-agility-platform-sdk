package com.servicemesh.agility.sdk.service.exception;

public class ServiceProviderException extends RuntimeException
{
    private static final long serialVersionUID = 20140114;

    String code;

    public ServiceProviderException()
    {
    }

    public ServiceProviderException(int code, String message)
    {
        this(Integer.toString(code), message);
    }

    public ServiceProviderException(String code, String message)
    {
        super(message);
        this.code = code;
    }

    public ServiceProviderException(String message)
    {
        super(message);
    }

    public ServiceProviderException(Throwable arg0)
    {
        super(arg0);
    }

    public ServiceProviderException(String message, Throwable arg1)
    {
        super(message, arg1);
    }

    public ServiceProviderException(int code, String message, Throwable arg1)
    {
        this(Integer.toString(code), message, arg1);
    }

    public ServiceProviderException(String code, String message, Throwable arg1)
    {
        super(message, arg1);
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

}
