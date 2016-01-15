package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Address;
import com.servicemesh.agility.api.AddressRange;
import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.Network;
import com.servicemesh.agility.api.NetworkInterface;
import com.servicemesh.agility.api.Port;
import com.servicemesh.agility.api.Subnet;
import com.servicemesh.agility.api.Task;

/**
 * Exposes additional operations to manage the following types from com.servicemesh.agility.api package: Project, Environment,
 * Topology Template, Instance
 */
public interface INetwork
{

    /**
     * Return the list of interfaces configured on this network.
     * 
     * @param object
     *            An instance of type Network
     * @return Collection of network interfaces
     * @throws Exception
     */
    public List<NetworkInterface> getNetworkInterfaces(Network object) throws Exception;

    /**
     * Return the list of address ranges configured on this network.
     * 
     * @param object
     *            An instance of type Network
     * @return Collection of address ranges
     * @throws Exception
     */
    public List<AddressRange> getAddressRanges(Network object) throws Exception;

    /**
     * Return the list of available addresses (using an IPAM provider if available) for static assignment on this network.
     * 
     * @param object
     *            An instance of type Network
     * @return Collection of addresses
     * @throws Exception
     */
    public List<Address> getAvailableAddresses(Network object) throws Exception;

    /**
     * Return the list of available addresses (natively managed) for static assignment on this network.
     * 
     * @param object
     *            An instance of type Network
     * @return Collection of addresses
     * @throws Exception
     */
    public List<Address> getAvailableAddressesNative(Network object) throws Exception;

    /**
     * Returns ports owned by network
     * 
     * @param networkId
     *            network id
     * @param context
     * @return Collection of ports
     * @throws Exception
     */
    public List<Port> getPorts(int networkId, Context context) throws Exception;

    /**
     * Returns subnets owned by network
     * 
     * @param networkId
     *            network id
     * @param context
     * @return Collection of subnets
     * @throws Exception
     */
    public List<Subnet> getSubnets(int networkId, Context context) throws Exception;

    /**
     * Delete Network with option to release its resources
     * 
     * @param asset
     *            The network to be deleted
     * @param parent
     *            The parent asset
     * @param release
     *            If true, release network resources
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task delete(Network asset, Asset parent, boolean release) throws Exception;

    /**
     * Reserves an address for a NIC
     * 
     * @param network
     *            The network where the address is allocated from
     * @param nic
     *            The NIC where the address will be allocated to
     * @param address
     *            The address being reserved
     * @return NetworkInterface The updated NIC
     * @throws Exception
     */
    public NetworkInterface reserveAddress(Network network, NetworkInterface nic, Address address) throws Exception;

    /**
     * Un-reserves an address for a NIC
     * 
     * @param network
     *            The network where the address is de-allocated from
     * @param nic
     *            The NIC where the address will be de-allocated from
     * @return NetworkInterface The updated NIC
     * @throws Exception
     */
    public NetworkInterface unreserveAddress(Network network, NetworkInterface nic) throws Exception;

    /**
     * Remove address if available
     * 
     * @param network
     *            The network where the address might reside
     * @param address
     *            The address to remove if available
     * @throws Exception
     */
    public void removeAddressIfAvailable(Network network, String address) throws Exception;

    /**
     * Allocate an address to a NIC
     * 
     * @param network
     *            The network where the address is allocated from
     * @param nic
     *            The NIC where the address will be allocated to
     * @param instance
     *            The instance the address is being reserved for - can be null
     * @return NetworkInterface The updated NIC
     * @throws Exception
     */
    public NetworkInterface allocateAddress(Network network, NetworkInterface nic, Instance instance) throws Exception;

    /**
     * Releases an address from a NIC
     * 
     * @param network
     *            The network where the address is released from
     * @param nic
     *            The NIC where the address will be released from
     * @return NetworkInterface The updated NIC
     * @throws Exception
     */
    public NetworkInterface releaseAddress(Network network, NetworkInterface nic) throws Exception;

    /**
     * Checks if an address can be assigned to the specified instance
     * 
     * @param address
     *            Address to assign
     * @param instance
     *            Instance to assign the address too
     * @return Address The address if it can be assigned, null otherwise
     * @throws Exception
     */
    public Address acquireAddressIfAvailable(Address address, Instance instance) throws Exception;
}
