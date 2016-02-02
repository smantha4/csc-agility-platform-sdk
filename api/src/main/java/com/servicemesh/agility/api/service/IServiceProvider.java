package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.ServiceProvider;
import com.servicemesh.agility.api.Task;

/**
 * Exposes operations on a service provider.
 */
public interface IServiceProvider
{

    /**
     * Delete service provider optionally releasing service instances
     * 
     * @param provider
     * @param parent
     * @param release
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    Task delete(ServiceProvider provider, Asset parent, boolean release) throws Exception;

    /**
     * Tests a connection to a Service Provider
     * 
     * @return true if connection is successful, otherwise false
     * @throws Exception
     */
    public boolean testConnection(ServiceProvider provider) throws Exception;

    /**
     * Synchronize a Service Provider
     * 
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task synchronize(ServiceProvider provider) throws Exception;

    /**
     * Stop a service provider
     * 
     * @param provider
     *            the provider to stop
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task stop(ServiceProvider provider) throws Exception;

    /**
     * Start a service provider
     * 
     * @param provider
     *            the service provider
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task start(ServiceProvider provider) throws Exception;
}
