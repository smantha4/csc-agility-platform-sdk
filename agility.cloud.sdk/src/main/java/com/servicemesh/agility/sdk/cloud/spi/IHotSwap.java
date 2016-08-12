package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.InstanceHotswapRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceResponse;
import com.servicemesh.core.async.ResponseHandler;

public interface IHotSwap
{

    public ICancellable reconfigure(InstanceHotswapRequest request, ResponseHandler<InstanceResponse> handler);

}
