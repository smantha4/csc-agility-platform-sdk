package com.servicemesh.core.async;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;
import com.servicemesh.core.messaging.Status;

public class QueueingHandler<T extends Response> implements ResponseHandler<T>
{

    /** This is true when the request has completed. */
    protected volatile boolean _isCompleted;
    protected volatile Throwable _error;
    protected ConcurrentLinkedQueue<T> _responses = new ConcurrentLinkedQueue<T>();

    /** Resets to an uncompleted state. */
    public synchronized void reset()
    {
        _isCompleted = false;
        _responses.clear();
    }

    /** Blocks until the request has completed. */
    public synchronized T get()
    {
        while (!_isCompleted && _responses.size() == 0)
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
            throw new RuntimeException(_error);
        }
        return (_responses.size() > 0) ? _responses.remove() : null;
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
            _responses.add(response);
            if (response.getStatus() == Status.FAILURE)
            {
                _error = new Throwable(response.getMessage());
            }
            if (response.getStatus() != Status.CONTINUE)
            {
                _isCompleted = true;
            }
            notifyAll();
        }
        return (_isCompleted == false);
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
