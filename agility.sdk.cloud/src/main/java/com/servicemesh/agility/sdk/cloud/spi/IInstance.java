package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.InstanceBootRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceRebootRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceReleaseRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceResponse;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceStartRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceStopRequest;
import com.servicemesh.core.async.ResponseHandler;

/**
 * Defines basic instance operations expected to be supported by all adapters:
 */
public interface IInstance
{

    /**
     * Called by the platform to initiate provisioning of a VM. This generally corresponds to a clone image operation within the
     * cloud provider. On completion the adapter should populate instance attributes such as: - unique identifier within the
     * provider, - state - mac address
     * 
     * @param request
     *            Specifies the virtual machine settings
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable create(InstanceCreateRequest request, ResponseHandler<InstanceResponse> handler);

    /**
     * Called after the initial create/clone operation to boot the image. On completion the adapter should populate instance
     * attributes such as: - state - hostname - IP address
     * 
     * @param request
     *            Specifies the virtual machine to boot.
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable boot(InstanceBootRequest request, ResponseHandler<InstanceResponse> handler);

    /**
     * Called to stop a virtual machine without releasing underlying resources. This is the equivalent of removing power from a
     * physical server after an orderly shutdown by the O/S.
     * 
     * @param request
     *            Specifies the virtual machine to stop.
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable stop(InstanceStopRequest request, ResponseHandler<InstanceResponse> handler);

    /**
     * Called to "power-on" a virtual machine that is in the stopped state.
     * 
     * @param request
     *            Specifies the virtual machine to start.
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable start(InstanceStartRequest request, ResponseHandler<InstanceResponse> handler);

    /**
     * Called to initiate a hard reboot of the virtual machine. This should be the equivalent of power-cycling a physical server.
     * 
     * @param request
     *            Specifies the virtual machine to reboot.
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable reboot(InstanceRebootRequest request, ResponseHandler<InstanceResponse> handler);

    /**
     * Called to destroy a virtual machine and release all associated resources.
     * 
     * @param request
     *            Specifies the virtual machine to release.
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable release(InstanceReleaseRequest request, ResponseHandler<InstanceResponse> handler);
}
