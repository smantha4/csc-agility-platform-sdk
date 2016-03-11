package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Assetlist;
import com.servicemesh.agility.api.AtomicInteger;
import com.servicemesh.agility.api.Task;

public interface IAtomicInteger
{
    /**
     * @param context
     * @return An Assetlist of all the AtomicIntegers visible to the current user.
     * @throws Exception
     */
    public Assetlist get(Context context) throws Exception;

    /**
     * @param key
     *            The primary key (pkey) associated with an AtomicInteger.
     * @param delta
     *            The value in base 10 by which the AtomicInteger will be incremented/decremented.
     * @param context
     * @return The AtomicInteger's current value expressed in its base.
     * @throws Exception
     */
    public String addAndGetValue(String key, int delta, Context context) throws Exception;

    /**
     * @param key
     *            The primary key (pkey) associated with an AtomicInteger.
     * @param delta
     *            The value in base 10 by which the AtomicInteger will be incremented/decremented.
     * @param retries
     *            The number of times to retry applying the delta to value.
     * @param context
     * @return The AtomicInteger's current value expressed in its base.
     * @throws Exception
     */
    public String addAndGetValueWithRetry(String key, int delta, int retries, Context context) throws Exception;

    /**
     * @param key
     *            The primary key (pkey) associated with an AtomicInteger.
     * @return A Task indicating whether the delete operation succeeded or not.
     * @throws Exception
     */
    public Task delete(String key) throws Exception;

    /**
     * @param key
     *            The primary key (pkey) associated with an AtomicInteger.
     * @param delta
     *            The value in base 10 by which the AtomicInteger will be incremented/decremented.
     * @param context
     * @return The AtomicInteger identified by key with delta applied to its value field.
     * @throws Exception
     */
    public AtomicInteger addAndGet(String key, int delta, Context context) throws Exception;

    /**
     * @param key
     *            The primary key (pkey) associated with an AtomicInteger.
     * @param delta
     *            The value in base 10 by which the AtomicInteger will be incremented/decremented.
     * @param retries
     *            The number of times to retry applying the delta to value.
     * @param context
     * @return The AtomicInteger identified by key with delta applied to its value field.
     * @throws Exception
     */
    public AtomicInteger addAndGetWithRetry(String key, int delta, int retries, Context context) throws Exception;

    /**
     * @param key
     *            The primary key (pkey) associated with an AtomicInteger.
     * @param patch
     *            The JSON patch to apply to the AtomicInteger identified by key.
     * @param context
     * @return The AtomicInteger identified by key with patch applied to it.
     * @throws Exception
     */
    public AtomicInteger patch(String key, String patch, Context context) throws Exception;

}
