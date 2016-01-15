package com.servicemesh.agility.sdk.service.operations;

import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPingRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderSyncRequest;
import com.servicemesh.agility.sdk.service.spi.IServiceProvider;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Status;

public class ServiceProviderOperations implements IServiceProvider
{

    @Override
    public Promise<ServiceProviderResponse> preCreate(ServiceProviderPreCreateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postCreate(ServiceProviderPostCreateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preUpdate(ServiceProviderPreUpdateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postUpdate(ServiceProviderPostUpdateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preDelete(ServiceProviderPreDeleteRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postDelete(ServiceProviderPostDeleteRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> sync(ServiceProviderSyncRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> ping(ServiceProviderPingRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> start(ServiceProviderStartRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> stop(ServiceProviderStopRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

}
