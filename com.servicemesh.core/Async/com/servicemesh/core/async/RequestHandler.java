package com.servicemesh.core.async;

import com.servicemesh.core.messaging.Request;

public interface RequestHandler<T extends Request> {
    public void onRequest(T request);
    public void onCancel(long reqId);
}
