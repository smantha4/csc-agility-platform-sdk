package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Cloud;
import com.servicemesh.agility.api.Model;
import com.servicemesh.agility.api.NetworkSubscription;
import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.Variable;

/**
 * Exposes operations on the cloud provider.
 */
public interface ICloud
{

    /**
     * Request a sync of the cloud provider.
     * 
     * @param cloud
     *            The cloud provider to sync
     * @return Returns a task reference that can be used to poll for completion.
     * @throws Exception
     */
    public Task resync(Cloud cloud) throws Exception;

    /**
     * Request a sync of the cloud provider.
     * 
     * @param cloud
     *            The cloud provider to sync
     * @param exclusions
     *            a comma-separated list of sync aspects to not perform (currently supported are "images" and "repo")
     * @return Returns a task reference that can be used to poll for completion.
     * @throws Exception
     */
    public Task resyncWithExclude(Cloud cloud, String exclusions) throws Exception;

    /**
     * Return a list of effective models for the cloud. Optional search parameters can be included.
     */
    public List<Model> getModels(Cloud cloud, Context context) throws Exception;

    /**
     * Return the decrypted value of a requested encrypted variable
     */
    public Variable decryptVariable(int assetId, int variableId, Context context) throws Exception;

    /**
     * Add subscription to network provider.
     * 
     * @param cloudId
     * @param subscription
     *            network subscription
     * @param context
     * @throws Exception
     */
    public NetworkSubscription subscribe(int cloudId, NetworkSubscription subscription, Context context) throws Exception;

    /**
     * Remove network provider subscription from cloud
     * 
     * @param cloudId
     * @param subscriptionId
     * @param context
     * @throws Exception
     */
    public Task unsubscribe(int cloudId, int subscriptionId, Context context) throws Exception;

}
