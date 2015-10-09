package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.AddressAllocateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.AddressAssociateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.AddressDisassociateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.AddressReleaseRequest;
import com.servicemesh.agility.sdk.cloud.msgs.AddressResponse;
import com.servicemesh.core.async.ResponseHandler;

public interface IAddress {
	
	public ICancellable allocate(AddressAllocateRequest request, ResponseHandler<AddressResponse> handler);
	public ICancellable release(AddressReleaseRequest request, ResponseHandler<AddressResponse> handler);
	public ICancellable associate(AddressAssociateRequest request, ResponseHandler<AddressResponse> handler);
	public ICancellable disassociate(AddressDisassociateRequest request, ResponseHandler<AddressResponse> handler);

}
