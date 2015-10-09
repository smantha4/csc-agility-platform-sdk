package com.servicemesh.agility.api.service;

import java.util.Set;

import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.ServiceInstance;
import com.servicemesh.agility.api.ServiceState;
import com.servicemesh.agility.api.Task;

/**
 * Exposes operations on a service instance.
 */
public interface IServiceInstance {
	
	/**
	 * Signal any pending instances that startup is complete
	 * @param instance   An instance of com.servicemesh.agility.api.ServiceInstance
	 * @throws Exception
	 */
    public void signalConnections(Task task, ServiceInstance instance) throws Exception;

	/**
	 * Returns a list of connections dependent on the current instance.
	 * @param instance   An instance of com.servicemesh.agility.api.ServiceInstance
	 * @throws Exception
	 */
    public Set<Integer> pendingConnections(Task task, ServiceInstance instance) throws Exception;	

    /**
     * Cancels a start task if the service instance is in starting state
     * @param parentTask task that initialized the request
     * @param instance   An instance of com.servicemesh.agility.api.ServiceInstance
     * @throws Exception
     */
    public void cancelStarting(Task parentTask, ServiceInstance instance) throws Exception;

    /**
     * Finalizes the deletion of a service instance
     *
     * @param parentTask task that initialized the request
     * @param instance   An instance of com.servicemesh.agility.api.ServiceInstance
     * @throws Exception
     */
    public void finalizeDelete(Task parentTask, ServiceInstance instance) throws Exception;
    
    /**
	 * If the service instance is an degraded state due to different reasons like not 
	 * being able to register with load balancer service,
	 * provides the ability to acknowledge the error and transitions the state to running. 
	 * @param instance 	A service instance of com.servicemesh.agility.api.ServiceInstance
	 * @throws Exception
	 */
	public void acknowledge(ServiceInstance instance) throws Exception;
	
	/**
	 * Put service instance into degraded state
	 * @param instance
	 * @param degradeReason
	 * @throws Excepiton
	 */
	public void degrade(ServiceInstance instance, String degradeReason) throws Exception;
	
	/**
	 * Put service instance into the specified state
	 * @param instance
	 * @param state
	 * @throws Exception
	 */
	public void setState(ServiceInstance instance, ServiceState state) throws Exception;
}
