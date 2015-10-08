package com.servicemesh.core.async;

import java.util.Arrays;

import com.servicemesh.core.messaging.Request;

import com.servicemesh.core.collections.hash.AbstractHashLong;

public class HashMapLongHandlerInfo extends AbstractHashLong {
    protected ResponseHandler[] m_responseHandlers;
    protected Class<? extends Request>[] m_requestClasses;

    public HashMapLongHandlerInfo() {}

    public HashMapLongHandlerInfo(int initialCapacity) {
        super(initialCapacity);
    }

    public HashMapLongHandlerInfo(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public int put(long reqId, ResponseHandler handler, Class<? extends Request> requestClass) {
        int i = intern(reqId);
        m_responseHandlers[i] = handler;
        m_requestClasses[i] = requestClass;
        return i;
    }

    public void putEntryValue(int entry, ResponseHandler handler, Class<? extends Request> requestClass) {
        m_responseHandlers[entry] = handler;
        m_requestClasses[entry] = requestClass;
    }

    public ResponseHandler getResponseHandler(long reqId) {
        int i = getEntry(reqId);
        if (-1 == i) {
            throw new IndexOutOfBoundsException("No such reqId " + reqId);
        }
        return m_responseHandlers[i];
    }

    public Class<? extends Request> getRequestClass(long reqId) {
        int i = getEntry(reqId);
        if (-1 == i) {
            throw new IndexOutOfBoundsException("No such reqId " + reqId);
        }
        return m_requestClasses[i];
    }

    public ResponseHandler getResponseHandler(long key, ResponseHandler missingValue) {
        int i = getEntry(key);
        return (i == -1) ? missingValue : m_responseHandlers[i];
    }

    public Class<? extends Request> getRequestClass(long key, Class<? extends Request> missingValue) {
        int i = getEntry(key);
        return (i == -1) ? missingValue : m_requestClasses[i];
    }

    public ResponseHandler getEntryResponseHandler(int entry) {
        return m_responseHandlers[entry];
    }

    public Class<? extends Request> getEntryRequestClass(int entry) {
        return m_requestClasses[entry];
    }

    protected void growValues(int size) {
        ResponseHandler[] newResponseHandlers = new ResponseHandler[size];
        if (null != m_responseHandlers) {
            System.arraycopy(m_responseHandlers, 0, newResponseHandlers, 0, m_responseHandlers.length);
        }
        m_responseHandlers = newResponseHandlers;

        Class<? extends Request>[] newRequestClasses = new Class[size];
        if (null != m_requestClasses) {
            System.arraycopy(m_requestClasses, 0, newRequestClasses, 0, m_requestClasses.length);
        }
        m_requestClasses = newRequestClasses;
    }

    protected void clearValues() {
        Arrays.fill(m_responseHandlers, null);
        Arrays.fill(m_requestClasses, null);
    }

    protected void removeValue(int entry) {
        m_responseHandlers[entry] = null;
        m_requestClasses[entry] = null;
    }
}
