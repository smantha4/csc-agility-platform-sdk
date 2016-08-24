package com.servicemesh.agility.distributed.sync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

/**
 * Provides a distributed mutex.
 */
public class DistributedMutex
{

    private final static Logger logger = Logger.getLogger(DistributedMutex.class);
    private final static ThreadLocal<ConcurrentHashMap<String, LockEntry>> _locks =
            new ThreadLocal<ConcurrentHashMap<String, LockEntry>>();
    private String _path;

    private static class LockEntry implements LockListener
    {
        public StringBuilder _stack = new StringBuilder();
        public AtomicInteger _count;
        public WriteLock _lock;

        public LockEntry(String path)
        {
            _stack = getCurrentStackFrame();
            _count = new AtomicInteger(0);
            _lock = new WriteLock(DistributedConfig.getZooKeeper(), path, null, this);
        }

        @Override
        public void lockAcquired()
        {
            synchronized (this)
            {
                _count.incrementAndGet();
                LockEntry.this.notifyAll();
            }
        }

        @Override
        public void lockReleased()
        {
        }

        public void lock() throws Exception
        {
            if (_count.get() > 0)
            {
                _count.incrementAndGet();
            }
            else
            {
                DistributedConfig.create(_lock.getDir(), CreateMode.PERSISTENT);

                // create a lock node for this thread
                try
                {
                    // wait for that node to acquire the lock (become first in child list)
                    _lock.lock();
                    synchronized (this)
                    {
                        while (_count.get() == 0)
                        {
                            wait();
                        }
                    }
                }
                catch (InterruptedException ex)
                {
                    // remove our lock node and disable watcher from recreating lock
                    _lock.close();
                    throw ex;
                }
            }
        }

        public int unlock()
        {
            // if lock count goes to 0 release the lock
            int count;
            if ((count = _count.decrementAndGet()) <= 0)
            {
                _lock.unlock();
            }
            return count;
        }
    }

    /**
     * Constructs a DistributedMutex object
     * 
     * @param path
     *            The ZooKeeper path that serves as the mutex identifer, e.g., "/agility/cloud/lock".
     * @see #DistributedMutex(String)
     */
    public static final DistributedMutex createNewInstance(String path)
    {
        return new DistributedMutex(path);
    }

    /**
     * Constructs a DistributedMutex object
     * 
     * @param path
     *            The ZooKeeper path that serves as the mutex identifer, e.g., "/agility/cloud/lock".
     */
    public DistributedMutex(String path)
    {
        _path = path;
    }

    /**
     * Acquires the exclusive lock for this object's path.
     */
    public void lock() throws Exception
    {
        // check to see if the lock is already acquired
        ConcurrentHashMap<String, LockEntry> locks = _locks.get();
        LockEntry entry = null;
        if (locks != null)
        {
            if ((entry = locks.get(_path)) != null)
            {
                entry.lock();
                return;
            }
        }
        else
        {
            locks = new ConcurrentHashMap<String, LockEntry>();
            _locks.set(locks);
        }

        // keep track of all acquired locks
        entry = new LockEntry(_path);
        entry.lock();
        locks.put(_path, entry);
    }

    /**
     * Releases the lock.
     * DE2087: whc - when DistNodes.java runs a script on an instance, no lock is created. This cause an error
     * message to appear in the agility.log file. This was actually not an error condition since there were no locks to begin
     * with. I changed this to appear as a warning in the log.
     */
    public void unlock()
    {
        // lookup lock entry
        ConcurrentHashMap<String, LockEntry> locks = _locks.get();
        if (locks == null)
        {
            logger.warn("unlock(" + _path + "): An attempt was made to release the lock for '_path'; however, there are"
                    + " no locks registered.\n" + getCurrentStackFrame());
            return;
        }
        LockEntry lock = locks.get(_path);
        if (lock == null)
        {
            logger.warn("unlock(" + _path + ") attempt to release lock that was not acquired by calling thread\n"
                    + getCurrentStackFrame());
            return;
        }

        // if lock count goes to zero remove the lock entry
        if (lock.unlock() <= 0)
        {
            locks.remove(_path);
        }
    }

    /**
     * Returns true if an exclusive lock has been acquired.
     */
    public boolean isLocked() throws Exception
    {
        // check to see if the lock is already acquired
        ConcurrentHashMap<String, LockEntry> locks = _locks.get();
        if (locks != null)
        {
            if (locks.containsKey(_path))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Logs information for all locks created by the current thread.
     */
    public static void dumpAllThreadLocks()
    {
        ConcurrentHashMap<String, LockEntry> locks = _locks.get();
        if (locks != null)
        {
            for (Map.Entry<String, LockEntry> entry : locks.entrySet())
            {
                logger.debug("lock(" + entry.getKey() + ") acquired:\n" + entry.getValue()._stack);
            }
        }
    }

    /**
     * Releases all locks created by the current thread.
     */
    public static void releaseAllThreadLocks()
    {
        ConcurrentHashMap<String, LockEntry> locks = _locks.get();
        if (locks != null)
        {
            for (Map.Entry<String, LockEntry> entry : locks.entrySet())
            {
                LockEntry lock = entry.getValue();
                logger.error("lock(" + entry.getKey() + ") was not released, acquired by:\n" + lock._stack);
                try
                {
                    while (lock.unlock() > 0)
                    {
                        ;
                    }
                }
                catch (Exception ex)
                {
                    logger.error(ex.getMessage(), ex);
                }
            }
            _locks.set(null);
        }
    }

    private static StringBuilder getCurrentStackFrame()
    {
        StringBuilder stack = new StringBuilder();
        for (StackTraceElement element : Thread.currentThread().getStackTrace())
        {
            stack.append(element);
            stack.append("\n");
        }
        return stack;
    }
}
