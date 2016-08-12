package com.servicemesh.agility.sdk.service.operations;

import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceValidateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.spi.IServiceInstance;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Status;

public class ServiceInstanceOperations implements IServiceInstance
{

    @Override
    public Promise<ServiceProviderResponse> validate(ServiceInstanceValidateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> provision(ServiceInstanceProvisionRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> reconfigure(ServiceInstanceReconfigureRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> start(ServiceInstanceStartRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> stop(ServiceInstanceStopRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> release(ServiceInstanceReleaseRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }
}
