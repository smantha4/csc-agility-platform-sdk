package com.servicemesh.core.async;

import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;

public interface ResponseHandler<T extends Response>
{
    public boolean onResponse(T response);

    public void onError(Request request, Throwable t);
}
