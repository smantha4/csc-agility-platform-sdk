package com.servicemesh.agility.sdk.service.exception;

public class FeatureNotSupportedException extends ServiceProviderException
{
    private static final long serialVersionUID = 20140114;

    public FeatureNotSupportedException()
    {
    }

    public FeatureNotSupportedException(String code, String message)
    {
        super(code, message);
    }

    public FeatureNotSupportedException(String message)
    {
        super(message);
    }

    public FeatureNotSupportedException(Throwable arg0)
    {
        super(arg0);
    }

    public FeatureNotSupportedException(String message, Throwable arg1)
    {
        super(message, arg1);
    }

    public FeatureNotSupportedException(String code, String message, Throwable arg1)
    {
        super(code, message, arg1);
    }

}
