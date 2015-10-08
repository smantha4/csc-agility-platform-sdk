/**
 * COPYRIGHT (C) 2008-2012 SERVICEMESH, INC.  ALL RIGHTS RESERVED.  CONFIDENTIAL AND PROPRIETARY. 
 * 
 * ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.     ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY SERVICEMESH IS STRICTLY PROHIBITED.
 * 
 */

package com.servicemesh.agility.api.service;

import java.util.List;
import java.util.Set;

import com.servicemesh.agility.api.AccessList;
import com.servicemesh.agility.api.Cloud;
import com.servicemesh.agility.api.ConfigurationResource;
import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.Script;
import com.servicemesh.agility.api.ScriptStatus;
import com.servicemesh.agility.api.Snapshot;
import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.Variable;

/**
 * Exposes operations on a virtual machine instance.
 */
public interface IInstance {
	
	/**
	 * Executes the specified script on a running virtual machine instance.
	 * @param instance 	The target instance
	 * @param script	The script to be executed
	 * @return Returns the script completion status along with stdout and stderr
	 * @throws Exception
	 */
	public ScriptStatus executeScript(Instance instance, Script script) throws Exception;
	
	/**
	 * Executes the specified script on a running virtual machine instance.
	 * @param instance 	The target instance
	 * @param script	The script to be executed
	 * @return Returns a task that can be monitored by the caller.
	 * @throws Exception
	 */
	public Task executeScriptTask(Instance instance, Script script) throws Exception;
	
	/**
	 * If supported by the underlying cloud adapter creates a named snapshot of the virtual machine state.
	 * @param instance 	The target instance
	 * @param snapshot	The requested snapshot
	 * @return Returns a task reference that can be polled for task completion.
	 * @throws Exception
	 */
	public Task createSnapshot(Instance instance, Snapshot snapshot) throws Exception;

	/**
	 * If supported by the underlying cloud adapter reverts the virtual machine 
	 * state to a previously created snapshot.
	 * @param instance 	The target instance
	 * @param snapshot	The requested snapshot
	 * @return Returns a task reference that can be polled for task completion.
	 * @throws Exception
	 */
	public Task revertSnapshot(Instance instance, Snapshot snapshot) throws Exception;

	/**
	 * Deletes the specified snapshot. 
	 * @param instance 	The target instance
	 * @param snapshot	The snapshot to be deleted
	 * @return Returns a task reference that can be polled for task completion.
	 * @throws Exception
	 */
	public Task deleteSnapshot(Instance instance, Snapshot snapshot) throws Exception;

	/**
	 * If the instance is an degraded state due to script failures during startup,
	 * provides the ability to acknowledge the error and transitions the state to running. 
	 * @param instance 	The target instance
	 * @throws Exception
	 */
	public void acknowledge(Instance instance) throws Exception;
	
	/**
	 * Creates a new stack from this Instance
	 * @param instance 	The target instance
	 * @param name	Name of new Stack
	 * @param description Description of new Stack
	 * @return Returns a task reference that can be polled for task completion.
	 * @throws Exception
	 */
	public Task buildStack(Instance instance, String name, String description) throws Exception;
	
	/**
	 * Checks to see if the current user has permission to connect to this instance
	 */
	public boolean checkAccess(String ipaddress, int port, String auth) throws Exception;

	/**
	 * Returns all instances matching the specified hostname.
   	 * @param	hostname	The instance hostname or fully qualified canonical name
   	 * @return				Array of matching instances
	 * @throws Exception
	 */
	public Instance[] lookupByHostname(String hostname) throws Exception;

	/**
	 * Returns all instances matching the specified uuid.
   	 * @param	uuid	The instance uuid
   	 * @return				Array of matching instances
	 * @throws Exception
	 */
	public Instance[] lookupByUuid(String uuid) throws Exception;

        /**
         * Returns all instances matching the specified instanceId.
         * @param cloud The cloud to be examined
         * @param  instanceId The requested instance id
         * @return                              Array of matching instances
         * @throws Exception
         */
        public Instance[] lookupByInstanceId(Cloud cloud, String instanceId) throws Exception;
	
	/**
	 * Returns the effective configuration policy for this instance.
	 * @param instance   The target instance
	 * @param type       Configuration artifact type (e.g. puppet/chef)
	 * @throws Exception
	 */
	public List<ConfigurationResource> getConfiguration(Instance instance, String type) throws Exception;
	
	/**
	 * Returns any defined variable values for the specified configuration resources.
	 * @param instance   The target instance
	 * @param resources  The requested configuration resources
	 * @throws Exception
	 */
	public List<Variable> getConfigurationVariables(Instance instance, List<ConfigurationResource> resources) throws Exception;
	
	/**
         * Generates and returns a unique hostname for the instance using the current hostname template.
         * @param instance   The target instance
         * @return           Unique hostname
         * @throws Exception
         */
 
        public String getUniqueHostName(Instance instance) throws Exception;
 
        /**
         * Returns the effective network access list for the instance.
         * @param instance   The target instance
         * @return           Network access list
         * @throws Exception
         */
 
        public List<AccessList> getEffectiveAccessList(Instance instance) throws Exception;
 
        /**
         * Provides ability to set administrative credentials for the instance.
         * @param instance   The target instance
         * @param username   Administrative account
         * @param password   Administrative password
         * @throws Exception
         */
        public void setCredentials(Instance instance, String username, String password) throws Exception;
     
        /**
	 * Returns the effective/overloaded variables for this instance.
	 * @param instance   The target instance
	 * @throws Exception
	 */
	public List<Variable> getVariables(Instance instance) throws Exception;
	
	/**
	 * Set asset property on the instance.
	 * @param instance   The target instance
	 * @return The modified instance object
	 * @throws Exception
	 */
	public Instance setAssetProperty(Instance instance, String name, String value) throws Exception;

	/**
	 * Set asset property on the instance.
	 * @param instance   The target instance
	 * @return The modified instance object
	 * @throws Exception
	 */
	public Instance setAssetProperty(Instance instance, String name, int value) throws Exception;

	/**
	 * Set asset property on the instance.
	 * @param instance   The target instance
	 * @return The modified instance object
	 * @throws Exception
	 */
	public Instance setAssetProperty(Instance instance, String name, boolean value) throws Exception;

	/**
	 * Workflow related activities
	 */
	
	/**
	 * Mount all volumes associated with instance
	 * @param instance   The target instance
	 * @throws Exception
	 */
    public void mountVolumes(Task task, Instance instance, boolean provisioning) throws Exception;

	/**
	 * Mount all volumes associated with instance
	 * @param instance   The target instance
	 * @param detach_volumes - Designates whether or not the volumes should be detached.  This flag should
	 * be passed as true when releasing an instance and passed as false when stopping an instance.
	 * @throws Exception
	 */
    public void unmountVolumes(Task task, Instance instance, boolean detach_volumes) throws Exception;

	/**
	 * Execute all startup scripts associated with instance
	 * @param instance   The target instance
	 * @throws Exception
	 */
    public void executeStartupScripts(Task task, Instance instance, boolean provisioning) throws Exception;

	/**
	 * Execute all shutdown scripts associated with instance
	 * @param instance   The target instance
	 * @throws Exception
	 */
    public void executeShutdownScripts(Task task, Instance instance) throws Exception;

	/**
	 * Signal any pending instances that startup is complete
	 * @param instance   The target instance
	 * @throws Exception
	 */
    public void signalConnections(Task task, Instance instance) throws Exception;

	/**
	 * Returns a list of connections dependent on the current instance.
	 * @param instance   The target instance
	 * @throws Exception
	 */
    public Set<Integer> pendingConnections(Task task, Instance instance) throws Exception;	
    
    /**
	 * Writes an audit log entry.
	 * @param userId The user's id
	 * @param instance   The target instance
	 * @param auditCategory Category string
	 */ 
    public void writeAuditLog(Integer userId, Instance instance, String auditCategory);
    
    /**
 	 * Remove All Snapshots attached to an instance
 	 * @param task The parent task
 	 * @param instance   The target instance
 	 */ 
    public void removeAllSnapshots(Task task, Instance instance) throws Exception;
    /**
 	 * Remove All Storage recrods attached to an instance
 	 * @param task The parent task
 	 * @param instance   The target instance
 	 */ 
    public void removeStorageRecords(Task task, Instance instance) throws Exception;
    /**
 	 * Remove All network services attached to an instance
 	 * @param task The parent task
 	 * @param instance   The target instance
 	 */ 
    public void removeNetworkServices(Task task, Instance instance) throws Exception;
    /**
 	 * Remove association between Instance & Storage
 	 * @param task The parent task
 	 * @param instance   The target instance
 	 */ 
    public void removeAssociationFromInstance2Storage(Task task, Instance instance) throws Exception;
    
    /**
 	 * Cleanup Credentials for a given instance
 	 * @param task The parent task
 	 * @param instance   The target instance
 	 */ 
    public void cleanupCredentials(Task task, Instance instance) throws Exception;
    /**
 	 * Cleanup Snapshots
 	 * @param task The parent task
 	 * @param instance   The target instance
 	 */ 
    public void cleanupSnapshots(Task task, Instance instance) throws Exception;
    /**
 	 * Release Mapped IPs
 	 * @param task The parent task
 	 * @param instance   The target instance
 	 */ 
    public void releaseMappedIPs(Task task, Instance instance) throws Exception;
    /**
 	 * Remove instance
 	 * @param task The parent task
 	 * @param instance   The target instance
 	 */ 
    public void removeInstance(Task task, Instance instance) throws Exception;
    
    /**
     * Cleanup any task connections that the instance is waiting on.
 	 * @param task The parent task
 	 * @param instance   The target instance
     * @throws Exception
     */
    public void cleanupTaskConnections(Task task, Instance instance) throws Exception;
    
    /**
     * Set startup pending
 	 * @param instance   The target instance
     * @param startupPending Value to be assigned to the instance's startup pending flag
     * @throws Exception
     */
    public void setStartupPending(Instance instance, boolean startupPending) throws Exception;

    /**
     * Cancel the start task if the instance is in starting state
 	 * @param task The parent task
 	 * @param instance   The target instance
     * @throws Exception
     */
    public void cancelStarting(Task task, Instance instance) throws Exception;
    
    /**
     * Detach All Volumes
 	 * @param instance   The target instance
     * @return Task handles that can be polled for task completion.
     * @throws Exception
     */
    public List<Task> detachAllVolumes(Instance instance) throws Exception;
    
    /**
     * Snapshot All volumes
 	 * @param instance   The target instance
     * @return Task handles that can be polled for task completion.
     * @throws Exception
     */
    public List<Task> snapshotAllVolumes(Instance instance) throws Exception;
}
