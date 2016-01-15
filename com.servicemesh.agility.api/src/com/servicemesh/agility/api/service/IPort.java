package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Port;
import com.servicemesh.agility.api.Task;

public interface IPort
{
    /**
     * Attaches instance network interface to port
     * 
     * @param instanceId
     *            Instance identifier
     * @param networkInterfaceId
     *            Network interface identifier
     * @param portId
     *            Port identifier
     * @return A task handle that can be polled for task completion.
     * @throws VMException
     */
    public Task attachPort(Integer instanceId, Integer networkInterfaceId, Integer portId) throws Exception;

    /**
     * Detaches port from instance network interface
     * 
     * @param instanceId
     *            Instance identifier
     * @param networkInterfaceId
     *            Identifier for network interface having a port to detach
     * @return A task handle that can be polled for task completion.
     * @throws VMException
     */
    public Task detachPort(Integer instanceId, Integer networkInterfaceId) throws Exception;

    /**
     * Delete Port with option to release its resources
     * 
     * @param asset
     *            The port to be deleted
     * @param parent
     *            The containing asset
     * @param release
     *            if true, release port resources
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task delete(Port asset, Asset parent, boolean release) throws Exception;
}
