package com.servicemesh.agility.sdk.service.operations;

import com.servicemesh.agility.sdk.service.msgs.PostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.PostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.PostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.PreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.PreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.PreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.spi.IAssetLifecycle;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Status;

public class AssetOperations implements IAssetLifecycle
{
    @Override
    public Promise<ServiceProviderResponse> preCreate(PreCreateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postCreate(PostCreateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preUpdate(PreUpdateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postUpdate(PostUpdateRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> preDelete(PreDeleteRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<ServiceProviderResponse> postDelete(PostDeleteRequest request)
    {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

}
