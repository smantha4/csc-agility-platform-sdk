package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.StorageAttachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageDetachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageResponse;
import com.servicemesh.core.async.ResponseHandler;

/**
 * Defines block storage operations which may be optionally supported by an adapter. If this interface is supported it should be
 * specified in the adapter capabilities sent in the RegistrationRequest message when the adapter is loaded.
 */
public interface IStorage
{
    /**
     * Called to carve out/reserve block storage which may be later attached to a virtual machine.
     * 
     * @param request
     *            Specifies the parameters of the storage volume to create
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable create(StorageCreateRequest request, ResponseHandler<StorageResponse> handler);

    /**
     * Called to attach the specified block storage to a virtual machine. This operation is responsible for mapping the device
     * into the virtual machine and if possible returning the corresponding device at the O/S level from which the raw device can
     * be mounted.
     * 
     * @param request
     *            Specifies the parameters of the storage volume to create
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable attach(StorageAttachRequest request, ResponseHandler<StorageResponse> handler);

    /**
     * Called to detach the specified block storage from a virtual machine. In some cloud providers the storage can be detached
     * and persisted independent of a virtual machine. In others, detaching will destroy the corresponding disk/volume. The
     * capabilities of the cloud provider should be indicated in the RegistrationRequest message used to register the adapter.
     * 
     * @param request
     *            Specifies the block storage to detach
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable detach(StorageDetachRequest request, ResponseHandler<StorageResponse> handler);

    /**
     * Called to delete block storage that has already been detached from (or never attached to) a virtual machine. Higher level
     * orchestration code will assure that the storage is not attached when this operation is invoked.
     * 
     * @param request
     *            Specifies the block storage to detach
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable delete(StorageDeleteRequest request, ResponseHandler<StorageResponse> handler);
}
