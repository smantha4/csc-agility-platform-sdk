/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.spi;

import com.servicemesh.agility.sdk.service.msgs.AvailableAddressesRequest;
import com.servicemesh.agility.sdk.service.msgs.AvailableAddressesResponse;
import com.servicemesh.agility.sdk.service.msgs.ReleaseAddressRequest;
import com.servicemesh.agility.sdk.service.msgs.ReleaseAddressResponse;
import com.servicemesh.agility.sdk.service.msgs.ReserveAddressRequest;
import com.servicemesh.agility.sdk.service.msgs.ReserveAddressResponse;
import com.servicemesh.agility.sdk.service.msgs.UnreserveAddressRequest;
import com.servicemesh.agility.sdk.service.msgs.UnreserveAddressResponse;
import com.servicemesh.core.async.Promise;

/**
 * Provides a hook for managing addresses
 * 
 * @see ServiceAdapter#registerValueProvider(String, IAddressProvider)
 */
public interface IAddressManagement
{

    /**
     * Used to retrieve current list of available addresses for a network
     *
     * @param request
     *            Specifies the network for which available addresses are desired
     * @return Promise to results on completion.
     */
    public Promise<AvailableAddressesResponse> getAvailableAddresses(AvailableAddressesRequest request);

    /**
     * Used to release an address
     * 
     * @param request
     *            Specifies the address to release, as well as network and networkInterface
     * @return Promise to results on completion
     */
    public Promise<ReleaseAddressResponse> releaseAddress(ReleaseAddressRequest request);

    /**
     * Used to reserve an address
     * 
     * @param request
     *            Specifies the address to reserve, as well as network and networkInterface
     * @return Promise to results on completion
     */
    public Promise<ReserveAddressResponse> reserveAddress(ReserveAddressRequest request);

    /**
     * Used to unreserve an address
     * 
     * @param request
     *            Specifies the address to unreserve, as well as network and networkInterface
     * @return Promise to results on completion
     */
    public Promise<UnreserveAddressResponse> unreserveAddress(UnreserveAddressRequest request);
}
