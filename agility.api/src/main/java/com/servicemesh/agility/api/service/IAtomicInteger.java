package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Assetlist;
import com.servicemesh.agility.api.AtomicInteger;
import com.servicemesh.agility.api.Task;

public interface IAtomicInteger
{
    public AtomicInteger get(String key, Context context) throws Exception;

    public Assetlist get(Context context) throws Exception;

    public Integer getValue(String key, Context context) throws Exception;

    public Task delete(String key) throws Exception;

    public AtomicInteger addAndGet(String key, int delta, Context context) throws Exception;

    public AtomicInteger addAndGetWithRetry(String key, int delta, int retries, Context context) throws Exception;

    public AtomicInteger patch(String key, String patch, Context context) throws Exception;

}
