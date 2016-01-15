package com.servicemesh.agility.sdk.service.operations;

import com.servicemesh.agility.sdk.service.msgs.AvailableAddressesRequest;
import com.servicemesh.agility.sdk.service.msgs.AvailableAddressesResponse;
import com.servicemesh.agility.sdk.service.msgs.ReleaseAddressRequest;
import com.servicemesh.agility.sdk.service.msgs.ReleaseAddressResponse;
import com.servicemesh.agility.sdk.service.msgs.ReserveAddressRequest;
import com.servicemesh.agility.sdk.service.msgs.ReserveAddressResponse;
import com.servicemesh.agility.sdk.service.msgs.UnreserveAddressRequest;
import com.servicemesh.agility.sdk.service.msgs.UnreserveAddressResponse;
import com.servicemesh.agility.sdk.service.spi.IAddressManagement;
import com.servicemesh.core.async.Promise;

public abstract class AddressManagementOperations implements IAddressManagement
{

    @Override
    public abstract Promise<AvailableAddressesResponse> getAvailableAddresses(AvailableAddressesRequest request);

    @Override
    public abstract Promise<ReleaseAddressResponse> releaseAddress(ReleaseAddressRequest request);

    @Override
    public abstract Promise<ReserveAddressResponse> reserveAddress(ReserveAddressRequest request);

    @Override
    public abstract Promise<UnreserveAddressResponse> unreserveAddress(UnreserveAddressRequest request);
}
