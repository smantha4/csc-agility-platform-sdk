package com.servicemesh.io.http;

import java.util.concurrent.Future;

public interface IHttpResponseFuture<T>
    extends Future<T>
{
    /**
     * Registers an {@linkplain IHttpCallback} listener to be run on the executor of
     * the future. The listener will run when the computation for the future is
     * complete or immediately if the future's computation is already complete.
     * The order of execution for registered listeners is not guaranteed.
     * 
     * @param listener - the listener to run
     * @throws IllegalArgumentException - if the listener is null or is already registered
     */
    public void addListener(final IHttpCallback<T> listener) throws IllegalArgumentException;
}
