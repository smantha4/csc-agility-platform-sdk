package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.FloatingIP;
import com.servicemesh.agility.api.Task;

public interface IFloatingIP
{
    /**
     * Delete FloatingIP with option to release its resources
     * 
     * @param asset
     * @param parent
     * @param release
     *            if true, release floating IP resources
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task delete(FloatingIP asset, Asset parent, boolean release) throws Exception;
}
