package com.servicemesh.core.async;

import java.util.ArrayList;
import java.util.List;

import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.WorkHandler;

public abstract class CompletablePromise<T> extends Promise<T>
{
    /**
     * Signal that this promise is complete and notify any pending threads or mapped results
     */
    public void complete(final T result)
    {
        final boolean completedNow = _synchronizer.complete(result);

        if (completedNow)
        {
            final List<Callback<T>> executeList = new ArrayList<Callback<T>>();
            final List<Reactor> reactorList = new ArrayList<Reactor>();

            synchronized (_reactors)
            {
                reactorList.addAll(_reactors);
            }

            for (final Reactor nextReactor : reactorList)
            {
                // need to dispatch something on the reactor loop to wake up pending thread
                // blocked waiting on a Promise.get(Reactor);
                nextReactor.workCreate(new WorkHandler() {
                    @Override
                    public boolean workFire()
                    {
                        return false;
                    }
                });
            }

            synchronized (_onComplete)
            {
                executeList.addAll(_onComplete);
            }

            for (final Callback<T> cb : executeList)
            {
                cb.invoke(result);
            }
        }
        else
        {
            throw new IllegalStateException("Promise already completed.");
        }
    }

    /**
     * Signal that this promise is completed with an error and notify any pending threads or mapped results
     */
    public void failure(final Throwable th)
    {
        final boolean failedNow = _synchronizer.fail(th);

        if (failedNow)
        {
            final List<Callback<Throwable>> executeList = new ArrayList<Callback<Throwable>>();
            final List<Reactor> reactorList = new ArrayList<Reactor>();

            synchronized (_reactors)
            {
                reactorList.addAll(_reactors);
            }

            for (final Reactor nextReactor : reactorList)
            {
                // need to dispatch something on the reactor loop to wake up pending thread
                // blocked waiting on a Promise.get(Reactor);
                nextReactor.workCreate(new WorkHandler() {
                    @Override
                    public boolean workFire()
                    {
                        return false;
                    }
                });
            }

            // If there is an exception propagating the failure - not much we
            // can do here - go ahead and allow it to bubble up to caller
            synchronized (_onFailure)
            {
                executeList.addAll(_onFailure);
            }

            for (final Callback<Throwable> cb : executeList)
            {
                cb.invoke(th);
            }
        }
        else
        {
            throw new IllegalStateException("Promise already completed.");
        }
    }
}
