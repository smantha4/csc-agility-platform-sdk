package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.CloudPropertyRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CloudPropertyResponse;
import com.servicemesh.agility.sdk.cloud.msgs.StorageDetachableRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageResponse;
import com.servicemesh.core.async.ResponseHandler;

public interface ICloudProperties
{

    public ICancellable getCloudProperty(CloudPropertyRequest request, ResponseHandler<CloudPropertyResponse> handler);

    public ICancellable isDetachable(StorageDetachableRequest request, ResponseHandler<StorageResponse> handler);

}
