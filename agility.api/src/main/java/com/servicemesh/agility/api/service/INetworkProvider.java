package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.FloatingIP;
import com.servicemesh.agility.api.Network;
import com.servicemesh.agility.api.NetworkProvider;
import com.servicemesh.agility.api.Router;
import com.servicemesh.agility.api.Task;

public interface INetworkProvider
{

    /**
     * Returns networks owned by network provider
     * 
     * @param networkProviderId
     *            network provider id
     * @param context
     * @throws Exception
     */
    public List<Network> getNetworks(int networkProviderId, Context context) throws Exception;

    /**
     * Returns routers owned by network provider
     * 
     * @param networkProviderId
     *            network provider id
     * @param context
     * @throws Exception
     */
    public List<Router> getRouters(int networkProviderId, Context context) throws Exception;

    /**
     * Returns floating IPs owned by network provider
     * 
     * @param networkProviderId
     *            network provider id
     * @param context
     * @throws Exception
     */
    public List<FloatingIP> getFloatingIPs(int networkProviderId, Context context) throws Exception;

    /**
     * Sync all network provider networks and related resources.
     * 
     * @param networkProviderId
     * @param context
     * @throws Exception
     */
    public Task sync(int networkProviderId, Context context) throws Exception;

    /**
     * Delete Network Provider with option to release its resources
     * 
     * @param asset
     * @param parent
     * @param release
     *            if true, release all of network provider resources
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task delete(NetworkProvider asset, Asset parent, boolean release) throws Exception;

}
