package com.servicemesh.agility.sdk.service.operations;

import com.servicemesh.agility.sdk.service.msgs.ConnectionPostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.spi.IConnection;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Status;

public class ConnectionOperations implements IConnection
{

    @Override
    public Promise<ServiceProviderResponse> preCreate(ConnectionPreCreateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postCreate(ConnectionPostCreateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preUpdate(ConnectionPreUpdateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postUpdate(ConnectionPostUpdateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preDelete(ConnectionPreDeleteRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postDelete(ConnectionPostDeleteRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

}
