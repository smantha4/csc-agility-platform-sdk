package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.CloudRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CloudResponse;
import com.servicemesh.agility.sdk.cloud.msgs.PostUpdateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PreDeleteRequest;
import com.servicemesh.core.async.ResponseHandler;

@Deprecated
public interface IFirewall
{
    /**
     * Create validation of an AccessList Policy
     * 
     * @param request
     *            Request specifying the target cloud
     * @param handler
     *            The handler for the response.
     */
    public ICancellable preCreate(CloudRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Notification on create of an AccessList Policy
     * 
     * @param request
     *            Request specifying the target cloud
     * @param handler
     *            The handler for the response.
     */
    public ICancellable postCreate(CloudRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Update validation on an AccessList Policy
     * 
     * @param request
     *            Request specifying the target cloud
     * @param handler
     *            The handler for the response.
     */
    public ICancellable preUpdate(CloudRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Notification on update to an AccessList Policy
     * 
     * @param request
     *            Request specifying the target cloud
     * @param handler
     *            The handler for the response.
     */
    public ICancellable postUpdate(PostUpdateRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Delete validation on an AccessList Policy
     * 
     * @param request
     *            Request specifying the target cloud
     * @param handler
     *            The handler for the response.
     */
    public ICancellable preDelete(PreDeleteRequest request, ResponseHandler<CloudResponse> handler);

    /**
     * Notification on delete of an AccessList Policy
     * 
     * @param request
     *            Request specifying the target cloud
     * @param handler
     *            The handler for the response.
     */
    public ICancellable postDelete(CloudRequest request, ResponseHandler<CloudResponse> handler);
}
