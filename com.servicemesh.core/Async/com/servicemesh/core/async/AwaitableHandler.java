package com.servicemesh.core.async;

import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;
import com.servicemesh.core.messaging.Status;

public class AwaitableHandler<T extends Response> implements ResponseHandler<T>
{

    /** This is true when the request has completed. */
    protected volatile boolean _isCompleted;
    protected volatile T _response;
    protected volatile Throwable _error;

    /** Resets to an uncompleted state. */
    public void reset()
    {
        _isCompleted = false;
        _response = null;
        _error = null;
    }

    /** Blocks until the request has completed. */
    public synchronized T get()
    {
        while (!_isCompleted)
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
            }
        }
        if (_error != null)
        {
            throw new RuntimeException(_error.getMessage(), _error);
        }
        return _response;
    }

    /**
     * Returns true if the request has completed.
     * 
     * @return true if the request has completed.
     */
    public boolean isCompleted()
    {
        return _isCompleted;
    }

    @Override
    public boolean onResponse(T response)
    {
        synchronized (this)
        {
            _response = response;
            if (response.getStatus() == Status.FAILURE)
            {
                _error = new Exception(response.getMessage());
            }
            if (response.getStatus() != Status.CONTINUE)
            {
                _isCompleted = true;
            }
            notifyAll();
        }
        return false;
    }

    @Override
    public void onError(Request request, Throwable t)
    {
        synchronized (this)
        {
            _error = t;
            _isCompleted = true;
            notifyAll();
        }
    }

}
