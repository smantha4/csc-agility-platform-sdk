package com.servicemesh.core.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.servicemesh.core.collections.hash.HashMapG;
import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;
import com.servicemesh.core.messaging.Status;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.WorkHandler;

public class AsyncService
{
    /** Logger for this class. */
    private final static Logger s_logger = Logger.getLogger(AsyncService.class);

    protected Reactor _reactor;

    protected AtomicLong nextRequestId = new AtomicLong(1L);

    protected HashMapLongHandlerInfo responseHandlers = new HashMapLongHandlerInfo();

    protected HashMapG<Class<? extends Request>, RequestHandler> requestHandlers =
            new HashMapG<Class<? extends Request>, RequestHandler>();

    public AsyncService(Reactor reactor)
    {
        _reactor = reactor;
    }

    public Reactor getReactor()
    {
        return _reactor;
    }

    public void registerRequest(Class<? extends Request> cls, RequestHandler handler)
    {
        requestHandlers.put(cls, handler);
    }

    public <T extends Request> void dispatch(T request)
    {
        RequestHandler<T> handler = requestHandlers.get(request.getClass(), null);
        if (handler != null)
        {
            handler.onRequest(request);
        }
        else
        {
            String msg = "Unregistered Request class: " + request.getClass().getName();
            s_logger.error(msg);
            int entry = responseHandlers.getEntry(request.getReqId());
            if (entry != -1)
            {
                ResponseHandler rh = responseHandlers.getEntryResponseHandler(entry);
                rh.onError(request, new Exception(msg));
                responseHandlers.removeEntry(entry);
            }
        }
    }

    public <REQ extends Request, RSP extends Response> Promise<RSP> promise(final REQ request)
    {
        final CompletablePromise<RSP> promise = PromiseFactory.create();
        sendRequest(request, new ResponseHandler<RSP>() {
            @Override
            public boolean onResponse(RSP response)
            {
                Status status = response.getStatus();
                if (status == null)
                {
                    status = Status.COMPLETE;
                }
                switch (status)
                {
                    case CONTINUE:
                        return true;
                    case COMPLETE:
                        promise.complete(response);
                        break;
                    case FAILURE:
                        promise.failure(new Throwable(response.getMessage()));
                        break;
                }
                return false;
            }

            @Override
            public void onError(Request request, Throwable t)
            {
                promise.failure(t);
            }
        });
        return promise;
    }

    public <REQ extends Request, RSP extends Response> Promise<List<RSP>> sequence(final REQ request)
    {
        final CompletablePromise<List<RSP>> promise = PromiseFactory.create();
        sendRequest(request, new ResponseHandler<RSP>() {
            List<RSP> _responses = new ArrayList<RSP>();

            @Override
            public boolean onResponse(RSP response)
            {
                Status status = response.getStatus();
                if (status == null)
                {
                    status = Status.COMPLETE;
                }
                switch (status)
                {
                    case CONTINUE:
                        _responses.add(response);
                        return true;
                    case COMPLETE:
                        _responses.add(response);
                        promise.complete(_responses);
                        break;
                    case FAILURE:
                        promise.failure(new Throwable(response.getMessage()));
                        break;
                }
                return false;
            }

            @Override
            public void onError(Request request, Throwable t)
            {
                promise.failure(t);
            }
        });
        return promise;
    }

    public long sendRequest(final Request request, final ResponseHandler handler)
    {
        long reqId = nextRequestId.getAndIncrement();
        request.setReqId(reqId);
        request.setTimestamp(System.currentTimeMillis());
        _reactor.workCreate(new WorkHandler() {
            @Override
            public boolean workFire()
            {
                responseHandlers.put(request.getReqId(), handler, request.getClass());
                dispatch(request);
                return false;
            }
        });
        s_logger.info("Sent request id=" + reqId);
        return reqId;
    }

    public <T extends Response> T sendRequest(final Request request)
    {
        try
        {
            Promise<T> promise = promise(request);
            return promise.get();
        }
        catch (Throwable t)
        {
            throw new RuntimeException(t.getMessage(), t);
        }
    }

    public void cancelRequest(final long reqId)
    {
        _reactor.workCreate(new WorkHandler() {
            @Override
            public boolean workFire()
            {
                int respEntry = responseHandlers.getEntry(reqId);
                if (respEntry == -1)
                {
                    s_logger.error("Cancel can't find registered handler: " + reqId);
                    return false;
                }
                Class<? extends Request> requestClass = responseHandlers.getEntryRequestClass(respEntry);

                int reqEntry = requestHandlers.getEntry(requestClass);
                if (reqEntry != -1)
                {
                    RequestHandler handler = requestHandlers.getEntryValue(reqEntry);
                    handler.onCancel(reqId);
                }
                // XXX - do we care if entry == -1?

                responseHandlers.removeEntry(respEntry);
                return false;
            }
        });
    }

    public void sendResponse(final Request req, final Response resp)
    {
        resp.setReqId(req.getReqId());
        resp.setTimestamp(System.currentTimeMillis());
        _reactor.workCreate(new WorkHandler() {
            @Override
            public boolean workFire()
            {
                long reqId = req.getReqId();
                int entry = responseHandlers.getEntry(reqId);
                if (entry != -1)
                {
                    ResponseHandler handler = responseHandlers.getEntryResponseHandler(entry);
                    if (!handler.onResponse(resp))
                    {
                        responseHandlers.removeEntry(entry);
                    }
                }
                else
                {
                    s_logger.error("Response being dispatched but no handler registered: " + resp.getClass().getName());
                }
                return false;
            }
        });
    }

    public void onError(final Request req, final Throwable t)
    {
        _reactor.workCreate(new WorkHandler() {
            @Override
            public boolean workFire()
            {
                long reqId = req.getReqId();
                int entry = responseHandlers.getEntry(reqId);
                if (entry != -1)
                {
                    ResponseHandler<? extends Response> handler = responseHandlers.getEntryResponseHandler(entry);
                    handler.onError(req, t);
                    responseHandlers.removeEntry(entry);
                }
                else
                {
                    s_logger.error("Error being dispatched but no handler registered");
                }
                return false;
            }
        });
    }

    public void sendResponse(final Request req, final Response resp, Throwable t)
    {
        resp.setStatus(Status.FAILURE);
        resp.setMessage(t.getMessage());
        sendResponse(req, resp);
    }

    public void sendResponse(final Request req, final Response resp, String message)
    {
        resp.setStatus(Status.FAILURE);
        resp.setMessage(message);
        sendResponse(req, resp);
    }

}
