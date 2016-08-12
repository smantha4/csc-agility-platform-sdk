package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.ImageCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ImageDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ImageResponse;
import com.servicemesh.core.async.ResponseHandler;

/**
 * Defines operations around image management within the cloud provider. Generally required functionality of any cloud adapter.
 */
public interface IImage
{
    /**
     * Called to delete a virtual machine template/image in the cloud provider.
     * 
     * @param request
     *            Specifies the image to delete.
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable delete(ImageDeleteRequest request, ResponseHandler<ImageResponse> handler);

    /**
     * Called to clone an existing virtual machine to a new virtual machine template/image from which new virtual machines may be
     * instantiated.
     * 
     * @param request
     *            Specifies the virtual machine to clone.
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable create(ImageCreateRequest request, ResponseHandler<ImageResponse> handler);
}
