/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.spi;

import com.servicemesh.agility.sdk.service.msgs.PostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.PostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.PostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.PreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.PreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.PreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.core.async.Promise;

/**
 * This interface provides generic methods to receive notifications of asset creation, modification, and deletion. An adapter
 * should request receipt of notifications for desired asset types in the adapter registration message.
 */
public interface IAssetLifecycle
{

    /**
     * Create validation of an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preCreate(PreCreateRequest request);

    /**
     * Notification on create of an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postCreate(PostCreateRequest request);

    /**
     * Update validation on an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preUpdate(PreUpdateRequest request);

    /**
     * Notification on update to an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postUpdate(PostUpdateRequest request);

    /**
     * Delete validation on an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preDelete(PreDeleteRequest request);

    /**
     * Notification on delete of an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postDelete(PostDeleteRequest request);

}
