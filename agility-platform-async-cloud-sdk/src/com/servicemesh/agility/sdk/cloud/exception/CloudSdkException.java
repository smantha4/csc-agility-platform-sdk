package com.servicemesh.agility.sdk.cloud.exception;

public class CloudSdkException extends RuntimeException
{
    private static final long serialVersionUID = 20140114;

    String code;

    public CloudSdkException()
    {
    }

    public CloudSdkException(int code, String message)
    {
        this(Integer.toString(code), message);
    }

    public CloudSdkException(String code, String message)
    {
        super(message);
        this.code = code;
    }

    public CloudSdkException(String message)
    {
        super(message);
    }

    public CloudSdkException(Throwable arg0)
    {
        super(arg0);
    }

    public CloudSdkException(String message, Throwable arg1)
    {
        super(message, arg1);
    }

    public CloudSdkException(int code, String message, Throwable arg1)
    {
        this(Integer.toString(code), message, arg1);
    }

    public CloudSdkException(String code, String message, Throwable arg1)
    {
        super(message, arg1);
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

}
