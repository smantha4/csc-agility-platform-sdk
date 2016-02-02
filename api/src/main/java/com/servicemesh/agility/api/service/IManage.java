package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Task;

/**
 * Exposes additional operations to manage the following types from com.servicemesh.agility.api package: Project, Environment,
 * Topology Template, Instance
 */
public interface IManage<T>
{

    /**
     * Request to start the specified asset.
     * 
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task start(T object) throws Exception;

    /**
     * Request to start the specified asset.
     * 
     * @param parent
     *            A parent task instance.
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task start(Task parent, T object) throws Exception;

    /**
     * Request to stop the specified asset.
     * 
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task stop(T object) throws Exception;

    /**
     * Request to stop the specified asset.
     * 
     * @param parent
     *            A parent task instance.
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task stop(Task parent, T object) throws Exception;

    /**
     * Request to restart the specified asset.
     * 
     * @param parent
     *            A parent task instance.
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task restart(Task parent, T object) throws Exception;

    /**
     * Request to restart the specified asset.
     * 
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task restart(T object) throws Exception;

    /**
     * Request to release the specified asset.
     * 
     * @param parent
     *            A parent task instance.
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task release(Task parent, T object) throws Exception;

    /**
     * Request to release the specified asset.
     * 
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task release(T object) throws Exception;

    /**
     * Request to clone the specified asset to a new parent container.
     * 
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @param parent
     *            An instance of [Container|Project|Environment|Topology]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task clone(T object, Asset parent) throws Exception;

    /**
     * Request to update cpu/memory of a running instance
     * 
     * @param object
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task hotSwap(T object) throws Exception;
}
