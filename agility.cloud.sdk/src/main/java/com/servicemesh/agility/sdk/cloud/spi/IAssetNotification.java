package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.CloudResponse;
import com.servicemesh.agility.sdk.cloud.msgs.PostCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PostDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PostUpdateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PreCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PreDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PreUpdateRequest;
import com.servicemesh.core.async.ResponseHandler;

/**
 * This interface provides methods to receive messages about asset creation, modification, and deletion.
 */
public interface IAssetNotification
{

    /**
     * Create validation of an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @param handler
     *            Response handler for the request
     */
    public ICancellable preCreate(PreCreateRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Notification on create of an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @param handler
     *            Response handler for the request
     */
    public ICancellable postCreate(PostCreateRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Update validation on an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @param handler
     *            Response handler for the request
     */
    public ICancellable preUpdate(PreUpdateRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Notification on update to an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @param handler
     *            Response handler for the request
     */
    public ICancellable postUpdate(PostUpdateRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Delete validation on an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @param handler
     *            Response handler for the request
     */
    public ICancellable preDelete(PreDeleteRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Notification on delete of an Asset
     * 
     * @param request
     *            Contains information about the asset
     * @param handler
     *            Response handler for the request
     */
    public ICancellable postDelete(PostDeleteRequest request, ResponseHandler<CloudResponse> handler);

}
