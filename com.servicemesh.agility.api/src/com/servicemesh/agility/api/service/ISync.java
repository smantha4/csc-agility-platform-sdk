package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Task;

public interface ISync
{
    /**
     * Synchronize asset
     * 
     * @param assetId
     *            Identifies the asset to be synchronized
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task sync(int assetId) throws Exception;
}
