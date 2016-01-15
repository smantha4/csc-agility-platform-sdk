package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Deployment;
import com.servicemesh.agility.api.Task;

/**
 * Exposes additional operations to manage the following types from com.servicemesh.agility.api package: SolutionDeployment
 */
public interface IDeployment<T extends Deployment> extends IWorkflow<T>
{

    /**
     * Allows to update name and description only
     * 
     * @param object
     * @return The updated deployment object
     * @throws Exception
     */
    public T updateName(T object) throws Exception;

    /**
     * Set deployment ready for promotion
     * 
     * @param object
     *            An instance of [SolutionDeployment]
     * @return A deployment instead set as ready for promotion
     * @throws Exception
     */
    public T setReadyForPromotion(T object) throws Exception;

    /**
     * Unset deployment ready for promotion
     * 
     * @param object
     *            An instance of [SolutionDeployment]
     * @return A deployment instead unset as ready for promotion
     * @throws Exception
     */
    public T unsetReadyForPromotion(T object) throws Exception;

    /**
     * Promote deployment to an environment
     * 
     * @param object
     *            An instance of [SolutionDeployment]
     * @return A copy or deployment instance promoted to an environment as specified by policy. Valid workflow applies.
     * @throws Exception
     */
    public T promote(T object, int locationId) throws Exception;

    /**
     * Promote deployment to an environment
     * 
     * @param object
     *            An instance of [SolutionDeployment]
     * @return A copy or deployment instance promoted to an environment as specified by policy. Valid workflow applies.
     * @throws Exception
     */
    public T submit(T object, String comment) throws Exception;

    /**
     * Request to start the specified asset.
     * 
     * @param object
     *            An instance of [SolutionDeployment]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task start(T object) throws Exception;

    /**
     * Request to stop the specified asset.
     * 
     * @param object
     *            An instance of [SolutionDeployment]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task stop(T object) throws Exception;

    /**
     * Request to restart the specified asset.
     * 
     * @param object
     *            An instance of [SolutionDeployment]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task restart(T object) throws Exception;

    /**
     * Request to release the specified asset.
     * 
     * @param object
     *            An instance of [SolutionDeployment]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task release(T object) throws Exception;

    /**
     * Request to deploy the specified asset.
     * 
     * @param object
     *            An instance of [SolutionDeployment]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task deploy(T object) throws Exception;
}
