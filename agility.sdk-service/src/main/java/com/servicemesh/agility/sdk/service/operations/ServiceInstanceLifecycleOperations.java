package com.servicemesh.agility.sdk.service.operations;

import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.spi.IServiceInstanceLifecycle;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Status;

public class ServiceInstanceLifecycleOperations implements IServiceInstanceLifecycle
{

    @Override
    public Promise<ServiceProviderResponse> preProvision(ServiceInstancePreProvisionRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postProvision(ServiceInstancePostProvisionRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preStop(ServiceInstancePreStopRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postStop(ServiceInstancePostStopRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preStart(ServiceInstancePreStartRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postStart(ServiceInstancePostStartRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preRestart(ServiceInstancePreRestartRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postRestart(ServiceInstancePostRestartRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preRelease(ServiceInstancePreReleaseRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postRelease(ServiceInstancePostReleaseRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }
}
