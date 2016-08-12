package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.core.async.ResponseHandler;
import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;

/**
 * Common interface used to dispatch all sync requests to the adapter.
 *
 * @param <REQ>
 *            A subclass of com.servicemesh.core.messaging.Request
 * @param <RSP>
 *            A subclass of com.servicemesh.core.messaging.Response
 */
public interface ISync<REQ extends Request, RSP extends Response>
{

    public ICancellable sync(REQ request, ResponseHandler<RSP> handler);
}
