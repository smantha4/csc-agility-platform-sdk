/**
 *              COPYRIGHT (C) 2008-2015 SERVICEMESH, INC.
 *                        ALL RIGHTS RESERVED.
 *                   CONFIDENTIAL AND PROPRIETARY.
 *
 *  ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS
 *  (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT
 *  ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.
 *  ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE
 *  AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED
 *  IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY
 *  OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY
 *  SERVICEMESH IS STRICTLY PROHIBITED.
 */

package com.servicemesh.core.async;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import com.servicemesh.core.async.impl.SequencePromise;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerHandler;
import com.servicemesh.core.reactor.WorkHandler;

/**
 * 
 * A promise to produce a result of type &lt;T&gt; at some point in the future.
 * Enables a functional approach to transforming/completing work as intermediate
 * results complete.
 * 
 * @param <T>
 */
public abstract class Promise<T>
{
    protected final Synchronizer<T> _synchronizer = new Synchronizer<T>();
    protected List<Callback<T>> _onComplete = Collections.synchronizedList(new ArrayList<Callback<T>>());
    protected List<Callback<Throwable>> _onFailure = Collections.synchronizedList(new ArrayList<Callback<Throwable>>());
    private List<Callback<Void>> _onCancel = Collections.synchronizedList(new ArrayList<Callback<Void>>());
    protected List<Reactor> _reactors = Collections.synchronizedList(new ArrayList<Reactor>());

    /**
     *  Blocks until the promise is completed and result returned or 
     *  failure occurs. 
     */
    public T get()
        throws Throwable
    {
        return _synchronizer.get();
    }

    /**
     *  Blocks until the promise is completed and result returned, the timeout
     *  expires or failure occurs.
     *   
     */
    public T get(final long timeout, final TimeUnit unit)
        throws Throwable
    {
        return _synchronizer.get(timeout, unit);
    }

    /**
     *  Blocks execution until the promise is completed and result returned or 
     *  failure occurs. However, continues to process events on the supplied
     *  reactor while waiting for completion. Note that this may only be called
     *  from the context of the reactors dispatch thread. So, this would typically
     *  be used in the context of an async service message handler.
     */
    public T get(final Reactor reactor)
        throws Throwable
    {
        if (reactor.getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Must be called from reactor thread context");
        }

        _reactors.add(reactor);

        try {
            while (!_synchronizer.isDone()) {
                reactor.dispatch();
            }
        } finally {
            _reactors.remove(reactor);
        }

         return _synchronizer.getValue();
    }

    /**
     * Returns true if the request has completed.
     * @return true if the request has completed.
     */
    public boolean isCompleted()
    {
        return _synchronizer.isCompleted();
    }

    /**
     * Set a callback to be invoked on completion of the promise. The callback
     * is executed immediately if the promise has already been completed.
     */
     public void onComplete(final Callback<T> cb)
     {
         if (isCompleted()) {
             try {
                 // get() with no timeout should work
                 final T value = get(1, TimeUnit.SECONDS); 

                 cb.invoke(value);
             } catch (final Throwable th) {
                 // get() can throw an exception, shouldn't happen but allow for
                 // it anyway
                 throw new RuntimeException(th);
             }
         } else {
             _onComplete.add(cb);
         }
     }

     /**
      * @return true if the request has completed.
      */
     public boolean isFailed()
     {
         return _synchronizer.isFailed();
     }

    /**
     * Register a callback to be invoked if a failure condition occurs. The
     * callback is executed immediately if the promise has already been failed.
     */
     public void onFailure(final Callback<Throwable> cb)
     {
         if (isFailed()) {
             cb.invoke(_synchronizer.getFailure());
         } else {
             _onFailure.add(cb);
         }
     }

    /**
      * @return true if the request has cancelled.
     */
    public boolean isCancelled()
    {
        return _synchronizer.isCancelled();
    }

    /**
     * Signal that this promise is cancelled and notify any pending threads or pending results
     */
    public void cancel()
    {
        final boolean cancelledNow = _synchronizer.cancel();

        if (cancelledNow) {
            final List<Callback<Void>> executeList = new ArrayList<Callback<Void>>();
            final List<Reactor> reactorList = new ArrayList<Reactor>();

            synchronized (_reactors) {
                reactorList.addAll(_reactors);
            }

            for (final Reactor nextReactor : reactorList) {
                // need to dispatch something on the reactor loop to wake up pending thread
                // blocked waiting on a Promise.get(Reactor);
                nextReactor.workCreate(new WorkHandler() {
                    @Override
                    public boolean workFire() {
                        return false;
                    }
                });
            }

            synchronized (_onCancel) {
                executeList.addAll(_onCancel);
            }

            for (final Callback<Void> cb : executeList) {
                cb.invoke(null);
            }
        } else {
            throw new IllegalStateException("Promise already completed.");
        }
    }

    /**
      * Set a callback to be invoked on cancellation of the promise. The callback
      * is executed immediately if the promise has already been cancelled.
      */
     public void onCancel(final Callback<Void> cb)
     {
         if (isCancelled()) {
             cb.invoke(null);
         } else {
             _onCancel.add(cb);
         }
     }

    /**
     * Return a new promise that on completion of the this promise completes by mapping the result
     * using the supplied function.
     */
     public <R> Promise<R> map(final Function<T, R> func)
     {
         // completion propagates from wrapped(this) to wrapper(promise)
         final CompletablePromise<R> promise = PromiseFactory.create();

         this.onComplete(new Callback<T>() {
             @Override
             public void invoke(final T arg)
             {
                 // if there is an exception mapping the result propagate the failure
                 try {
                     promise.complete(func.invoke(arg));
                 } catch (final Throwable th) {
                     promise.failure(th);
                 }
             }
         });

         // failures propagate from wrapped(this) to wrapper(promise)
         this.onFailure(new Callback<Throwable>() {
             @Override
             public void invoke(final Throwable th) {
                 promise.failure(th);
             }
         });

         // cancel propagate from wrapper(promise) to wrapped(this)
         promise.onCancel(new Callback<Void>() {
             @Override
             public void invoke(final Void arg) {
                 cancel();
             }
         });

         return promise;
     }

    /**
     * Return a new promise that on completion of the current promise completes by mapping the result
     * using the supplied function.
     */
     public <R> Promise<R> flatMap(final Function<T, Promise<R>> func)
     {
         // completion propagates from wrapped(this) to wrapper(promise)
         final CompletablePromise<R> promise = PromiseFactory.create();

         this.onComplete(new Callback<T>() {
             @Override
             public void invoke(final T tArg)
             {
                 // if there is an exception mapping the result propagate the failure
                 try {
                     // on completion of the result - complete the wrapper
                     final Promise<R> flatten = func.invoke(tArg);

                     flatten.onComplete(new Callback<R>() {
                         @Override
                         public void invoke(final R rArg) {
                             promise.complete(rArg);
                         }
                     });

                     // on failure of the result - fail the wrapper
                     flatten.onFailure(new Callback<Throwable>() {
                         @Override
                         public void invoke(final Throwable t) {
                             promise.failure(t);
                         }
                     });

                     // if the wrapper is canceled - cancel the result
                     promise.onCancel(new Callback<Void>() {
                         @Override
                         public void invoke(final Void arg) {
                             flatten.cancel();
                         }
                     });
                 } catch (final Throwable th) {
                     promise.failure(th);
                 }
             }
         });

         // failures propagate from wrapped(this) to wrapper(promise)
         this.onFailure(new Callback<Throwable>() {
             @Override
             public void invoke(final Throwable t) {
                 promise.failure(t);
             }
         });

         // cancel propagate from wrapper(promise) to wrapped(this)
         promise.onCancel(new Callback<Void>() {
             @Override
             public void invoke(final Void arg) {
                 cancel();
             }
         });

         // if the result is cancelled - fail the wrapper
         this.onCancel(new Callback<Void>() {
             @Override
             public void invoke(final Void arg) {
                 try {
                     promise.failure(new CancellationException("Wrapped promise cancelled"));
                 } catch (IllegalStateException ex) {
                     // Ignore, can happen if the flattened promise is already complete
                 }
             }
         });

         return promise;
     }

     /**
     * Wrap this promise with a promise that will handle exceptions throws.
     * If there is a runtime exception during the recover function then the promise is marked as failed.
     */
    public Promise<T> recover(final Function<Throwable, T> func)
    {
        final CompletablePromise<T> promise = PromiseFactory.create();

        this.onComplete(new Callback<T>() {
            @Override
            public void invoke(final T result)
            {
                // if there is an exception mapping the result propagate the failure
                try {
                    promise.complete(result);
                } catch (final Throwable t) {
                    promise.complete(func.invoke(t));
                }
            }
        });

        // failures propagate from wrapped(this) to wrapper(promise)
        this.onFailure(new Callback<Throwable>() {
            @Override
            public void invoke(final Throwable t) {
            	try {
            		promise.complete(func.invoke(t));
            	} catch (Throwable th) {
            		promise.failure(th);
            	}
            }
        });

        // cancel propagate from wrapper(promise) to wrapped(this)
        promise.onCancel(new Callback<Void>() {
            @Override
            public void invoke(final Void arg) {
                cancel();
            }
        });

        return promise;
    }

    /**
     * Returns a completed promise with the specified result.
     */
    public static <T> Promise<T> pure(final T result)
    {
        if (result == null) {
            throw new IllegalArgumentException("Null result argument");
        }

        CompletablePromise<T> promise = PromiseFactory.create();

        promise.complete(result);
        return promise;
    }

    /**
     * Returns a completed promise with the specified error result.
     */
    public static <T> Promise<T> pure(Throwable th)
    {
        if (th == null) {
            throw new IllegalArgumentException("Null Throwable argument");
        }

        CompletablePromise<T> promise = PromiseFactory.create();

        promise.failure(th);
        return promise;
    }

    /**
     * Returns a promise that completes when the supplied list of promises complete
     */
    public static <T> Promise<List<T>> sequence(final List<Promise<T>> promises)
    {
        if (promises == null) {
            throw new IllegalArgumentException("Null promises");
        }

        final SequencePromise<T> sequence = new SequencePromise<T>(promises.size());

        if (promises.size() > 0) {
            for (final Promise<T> promise : promises) {
                // completion propagates from wrapped to wrapper
                promise.onComplete(new Callback<T>() {
                    @Override
                    public void invoke(T result) {
                        sequence.add(result);
                    }
                });

                // failures propagate from wrapped to wrapper
                promise.onFailure(new Callback<Throwable>() {
                    @Override
                    public void invoke(Throwable t) {
                        sequence.failure(t);
                    }
                });

                // cancel propagates from wrapper to wrapped
                sequence.onCancel(new Callback<Void>() {
                    @Override
                    public void invoke(Void arg) {
                        promise.cancel();
                    }
                });

                // propagate wrapped cancels as failures to the sequenced promise
                promise.onCancel(new Callback<Void>() {
                    @Override
                    public void invoke(Void arg) {
                        try {
                            sequence.failure(new CancellationException("Wrapped promise cancelled"));
                        } catch (IllegalStateException ex) {
                            // Ignore, can happen if the sequenced promise is already complete
                        }
                    }
                });
            }
        } else {
            sequence.complete(new ArrayList<T>());
        }

        return sequence;
    }

    /**
     * Returns a promise that completes when the supplied list of promises complete
     */
    @SuppressWarnings("unchecked")
    public static Promise<List<Object>> sequenceAny(List<Promise<?>> promises)
    {
        if (promises == null) {
            throw new IllegalArgumentException("Null promises");
        }

        final SequencePromise<Object> sequence = new SequencePromise<Object>(promises.size());

        if (promises.size() > 0) {
            for (final Promise<?> promise : promises) {
                // completion propagates from wrapped to wrapper
                promise.onComplete(new Callback() {
                    @Override
                    public void invoke(Object result) {
                        sequence.add(result);
                    }
                });

                // failures propagate from wrapped to wrapper
                promise.onFailure(new Callback<Throwable>() {
                    @Override
                    public void invoke(Throwable t) {
                        sequence.failure(t);
                    }
                });

                // cancel propagates from wrapper to wrapped
                sequence.onCancel(new Callback<Void>() {
                    @Override
                    public void invoke(Void arg) {
                        promise.cancel();
                    }
                });

                // propagate wrapped cancels as failures to the sequenced promise
                promise.onCancel(new Callback<Void>() {
                    @Override
                    public void invoke(Void arg) {
                        try {
                            sequence.failure(new CancellationException("Wrapped promise cancelled"));
                        } catch (IllegalStateException ex) {
                            // Ignore, can happen if the sequenced promise is already complete
                        }
                    }
                });
            }
        } else {
            sequence.complete(new ArrayList<Object>());
        }

        return sequence;
    }

    /**
     * Returns a promise that will execute work at some point in the future using
     * the specified reactor
     */
    public static <T> Promise<T> promise(final Reactor reactor, final Function0<T> func0)
    {
        if (reactor == null) {
            throw new IllegalArgumentException("Null reactor");
        }

        if (func0 == null) {
            throw new IllegalArgumentException("Null func argument");
        }

        final CompletablePromise<T> promise = PromiseFactory.create();

        reactor.workCreate(new WorkHandler() {
            @Override
            public boolean workFire() {
                try {
                    promise.complete(func0.exec());
                } catch (Throwable th) {
                    promise.failure(th);
                }

                return false;
            }
        });

        return promise;
    }

    /**
    * Returns a promise that will execute at some point in the future using the
    * specified reactor
    */
    public static <T> Promise<T> delayed(final Reactor reactor, final long delta, final Function0<T> func)
    {
        if (reactor == null) {
            throw new IllegalArgumentException("Null reactor");
        }

        if (func == null) {
            throw new IllegalArgumentException("Null func argument");
        }

        final CompletablePromise<T> promise = PromiseFactory.create();

        reactor.timerCreateRel(delta, new TimerHandler() {
            @Override
            public long timerFire(final long scheduledTime, final long actualTime)
            {
                try {
                    promise.complete(func.exec());
                } catch (Throwable th) {
                    promise.failure(th);
                }

                return 0;
            }
        });

        return promise;
    }

    /**
     * Returns a promise that will complete at some point in the future using the
     * specified reactor
     */
    public static <T> Promise<T> timeout(final Reactor reactor, final long delta, final T message)
    {
        if (reactor == null) {
            throw new IllegalArgumentException("Null reactor");
        }

        if (message == null) {
            throw new IllegalArgumentException("Null message argument");
        }

        final CompletablePromise<T> promise = PromiseFactory.create();

        reactor.timerCreateRel(delta, new TimerHandler() {
            @Override
            public long timerFire(final long scheduledTime, final long actualTime) 
            {
                try {
                    promise.complete(message);
                } catch (Throwable th) {
                    promise.failure(th);
                }

                return 0;
            }
        });

        return promise;
    }

    static final class Synchronizer<T>
        extends AbstractQueuedSynchronizer
    {
        private static final long serialVersionUID = 0L;

        /**
         * Valid states
         */
        private static final int RUNNING = 0;
        private static final int COMPLETING = 1;
        private static final int COMPLETED = 2;
        private static final int FAILED = 3;
        private static final int CANCELLED = 4;

        private T _value;
        private Throwable _exception;

        /**
         * Acquisition succeeds if the future is done, otherwise it fails.
         */
        @Override
        protected int tryAcquireShared(final int ignored)
        {
            return isDone() ? 1 : -1;
        }

        /**
         * Always allow a release to go through, this means the state has been
         * successfully changed and the result is available.
         */
        @Override
        protected boolean tryReleaseShared(final int finalState)
        {
            setState(finalState);
            return true;
        }

        /**
         * Blocks until the task is complete or the timeout expires.  Throws a
         * {@link TimeoutException} if the timer expires, otherwise behaves like
         * {@link #get()}.
         */
        // timeout in milliseconds
        protected T get(final long timeout, final TimeUnit unit)
            throws TimeoutException, CancellationException, InterruptedException, Throwable
        {
            // Attempt to acquire the shared lock with a timeout.
            if (!tryAcquireSharedNanos(-1, unit.toNanos(timeout))) {
                throw new TimeoutException("Timeout waiting for task.");
            }

            return getValue();
        }

        /**
         * Blocks until {@link #complete(Object, Throwable, int)} has been
         * successfully called.  Throws a {@link CancellationException} if the task
         * was cancelled, or a {@link ExecutionException} if the task completed with
         * an error.
         */
        protected T get()
            throws CancellationException, InterruptedException, Throwable
        {
            // Acquire the shared lock allowing interruption.
            acquireSharedInterruptibly(-1);

            return getValue();
        }

        /**
         * Implementation of the actual value retrieval.  Will return the value
         * on success, an exception on failure, a cancellation on cancellation, or
         * an illegal state if the synchronizer is in an invalid state.
         */
        private T getValue()
            throws CancellationException, Throwable
        {
            final int state = getState();

            switch (state) {
                case COMPLETED:
                    if (_exception != null) {
                        throw _exception;
                    } else {
                        return _value;
                    }
                case FAILED:
                    if (_exception != null) {
                        throw _exception;
                    } else {
                        throw new IllegalStateException("Promise in FAILED state without set exception");
                    }
                case CANCELLED:
                    throw new CancellationException("Promise was cancelled");

                default:
                    throw new IllegalStateException("Error, Promise synchronizer in invalid state: " + state);
            }
        }

        /**
         * Checks if the state is {@link #COMPLETED}.
         */
        protected boolean isCompleted()
        {
            return getState() == COMPLETED;
        }

        /**
         * Checks if the state is {@link #COMPLETED}, {@link #CANCELLED}, or {@link
         * FAILED}.
         */
        protected boolean isDone()
        {
            boolean done;

            switch(getState()) {
                case COMPLETED:
                case CANCELLED:
                case FAILED:
                    done = true;
                    break;
                default:
                    done = false;
            }

            return done;
        }

        /**
         * Checks if the state is {@link #CANCELLED}.
         */
        protected boolean isCancelled()
        {
            return getState() == CANCELLED;
        }

        /**
         * Checks if the state is {@link #FAILED}.
         */
        protected boolean isFailed()
        {
            return getState() == FAILED;
        }

        /**
         * Transition to the COMPLETED state and set the value.
         */
        protected boolean complete(final T value)
        {
            return complete(value, null, COMPLETED);
        }

        /**
         * Transition to the COMPLETED state and set the exception.
         */
        protected boolean fail(final Throwable th)
        {
            return complete(null, th, FAILED);
        }

        /**
         * Transition to the CANCELLED state.
         */
        protected boolean cancel()
        {
            return complete(null, null, CANCELLED);
        }

        /**
         * Implementation of completing a task.  Either {@code v} or {@code t} will
         * be set but not both.  The {@code finalState} is the state to change to
         * from {@link #RUNNING}.  If the state is not in the RUNNING state we
         * return {@code false} after waiting for the state to be set to a valid
         * final state ({@link #COMPLETED}, {@link #CANCELLED}, or {@link
         * #FAILED}).
         *
         * @param v the value to set as the result of the computation.
         * @param t the exception to set as the result of the computation.
         * @param finalState the state to transition to.
         */
        private boolean complete(final T value, final Throwable th, final int finalState)
        {
            final boolean doCompletion = compareAndSetState(RUNNING, COMPLETING);

            if (doCompletion) {
                // If this thread successfully transitioned to COMPLETING, set the value
                // and exception and then release to the final state.
                _value = value;

                if (th != null) {
                    _exception = th;
                }

                releaseShared(finalState);
            } else if (getState() == COMPLETING) {
                // If some other thread is currently completing the future, block until
                // they are done so we can guarantee completion.
                acquireShared(-1);
            }

            return doCompletion;
        }

        protected Throwable getFailure()
        {
            return _exception;
        }
    }
}
