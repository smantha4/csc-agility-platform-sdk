package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.CloudChangeRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CloudResponse;
import com.servicemesh.core.async.ResponseHandler;

public interface ICloudChanged
{

    public ICancellable cloudChanged(CloudChangeRequest request, ResponseHandler<CloudResponse> handler);

}
