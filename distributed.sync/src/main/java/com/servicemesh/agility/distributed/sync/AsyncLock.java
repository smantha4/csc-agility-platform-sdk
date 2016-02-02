package com.servicemesh.agility.distributed.sync;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import com.servicemesh.core.async.CompletablePromise;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.async.PromiseFactory;

/**
 * Provides distributed locking around an asynchronous operation.
 */
public class AsyncLock
{

    private final static Logger logger = Logger.getLogger(AsyncLock.class);
    private LockEntry _entry;

    private AsyncLock(String path, CompletablePromise<AsyncLock> promise)
    {
        _entry = new LockEntry(path, promise, this);
    }

    /**
     * Obtains a distributed lock for the specified path
     * 
     * @param path
     *            The ZooKeeper path that serves as the lock identifier, e.g. "/agility/my-service/my-service-instance-name/lock".
     * @return A failed Promise if unable to obtain lock. Otherwise, a Promise that can invoke its asynchronous operation via a
     *         compositional method such as flatMap().
     */
    public static Promise<AsyncLock> lock(String path)
    {
        CompletablePromise<AsyncLock> promise = PromiseFactory.create();
        AsyncLock parent = new AsyncLock(path, promise);
        try
        {
            parent._entry.lock();
        }
        catch (Exception ex)
        {
            logger.error("lock() failed: " + ex.getMessage(), ex);
            return Promise.pure(ex);
        }
        return promise;
    }

    /**
     * Frees the distributed lock
     */
    public void unlock()
    {
        _entry.unlock();
    }

    private static class LockEntry implements LockListener
    {
        private CompletablePromise<AsyncLock> _promise;
        private WriteLock _lock;
        private AsyncLock _parent;

        public LockEntry(String path, CompletablePromise<AsyncLock> promise, AsyncLock parent)
        {
            _lock = new WriteLock(DistributedConfig.getZooKeeper(), path, null, this);
            _promise = promise;
            _parent = parent;
        }

        @Override
        public void lockAcquired()
        {
            synchronized (this)
            {
                _promise.complete(_parent);
            }
        }

        @Override
        public void lockReleased()
        {
        }

        public void lock() throws Exception
        {
            DistributedConfig.create(_lock.getDir(), CreateMode.PERSISTENT);
            _lock.lock();
        }

        public void unlock()
        {
            _lock.unlock();
        }
    }
}
