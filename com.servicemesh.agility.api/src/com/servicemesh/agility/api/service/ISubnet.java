package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Subnet;
import com.servicemesh.agility.api.Task;

public interface ISubnet
{
    /**
     * Delete Subnet with option to release its resources
     * 
     * @param asset
     *            The subnet to be deleted
     * @param parent
     *            The containing asset
     * @param release
     *            if true, release subnet resources
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task delete(Subnet asset, Asset parent, boolean release) throws Exception;
}
