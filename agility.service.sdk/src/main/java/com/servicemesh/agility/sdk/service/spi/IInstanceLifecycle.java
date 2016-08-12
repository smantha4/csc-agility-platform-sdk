/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.spi;

import com.servicemesh.agility.sdk.service.msgs.InstancePostBootRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostStartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostStopRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreBootRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreStartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreStopRequest;
import com.servicemesh.agility.sdk.service.msgs.InstanceResponse;
import com.servicemesh.core.async.Promise;

/**
 * Defines a set of lifecycle callouts that can be implemented by a service provider to take action when an instance bound to the
 * service changes state. As an example, a workload connected to a load balancer service should be registered/deregistered to/from
 * the load balancer as new instances of the workload are provisioned and/or released.
 */
public interface IInstanceLifecycle
{

    /**
     * Called by the platform on initial provisioning of a VM.
     * 
     * @param request
     *            Specifies the service binding and virtual machine settings
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> preProvision(InstancePreProvisionRequest request);

    /**
     * Called by the platform after initial provisioning of a VM completes. Enables the service provider to register the instance
     * with a service offering and/or allocate service resources for the virtual machine.
     * 
     * @param request
     *            Specifies the service binding and virtual machine settings
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> postProvision(InstancePostProvisionRequest request);

    /**
     * Called immediately prior to booting the instance. The VM has been cloned but not powered. Provides a hook for network
     * service providers to allocate network resources (e.g. IP address) for the virtual machine.
     * 
     * @param request
     *            Specifies the service binding and virtual machine to boot.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> preBoot(InstancePreBootRequest request);

    /**
     * Called immediately after the instance boots. At this point in the lifecycle the VM has an IP address but configuration
     * management has not run.
     * 
     * @param request
     *            Specifies the service binding and virtual machine that completed boot.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> postBoot(InstancePostBootRequest request);

    /**
     * Called to stop a virtual machine without releasing underlying resources. This is the equivalent of removing power from a
     * physical server after an orderly shutdown by the O/S.
     * 
     * @param request
     *            Specifies the service binding and virtual machine to stop.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> preStop(InstancePreStopRequest request);

    /**
     * Called to stop a virtual machine without releasing underlying resources. This is the equivalent of removing power from a
     * physical server after an orderly shutdown by the O/S. This provides the service provider the option to deregister/cleanup
     * resources associated with a stopped instance.
     * 
     * @param request
     *            Specifies the service binding and virtual machine that was stopped.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> postStop(InstancePostStopRequest request);

    /**
     * Called to "power-on" a virtual machine that is in the stopped state. Provides the service provider the option to validate
     * the virtual machines settings are compatible with the service offering.
     * 
     * @param request
     *            Specifies the service binding and virtual machine to start.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> preStart(InstancePreStartRequest request);

    /**
     * Called after "powering-on" a virtual machine that was in the stopped state. Proves the service provider the option to
     * register or provision services for the virtual machine instance.
     * 
     * @param request
     *            Specifies the service binding and virtual machine that was started.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> postStart(InstancePostStartRequest request);

    /**
     * Called to initiate a hard reboot of the virtual machine. This should be the equivalent of power-cycling a physical server.
     * 
     * @param request
     *            Specifies the service binding and virtual machine to reboot.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> preRestart(InstancePreRestartRequest request);

    /**
     * Called after a hard reboot of the virtual machine. This should be the equivalent of power-cycling a physical server.
     * 
     * @param request
     *            Specifies the service binding and virtual machine that was restarted.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> postRestart(InstancePostRestartRequest request);

    /**
     * Called to destroy a virtual machine and release all associated resources.
     * 
     * @param request
     *            Specifies the service binding and virtual machine to release.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> preRelease(InstancePreReleaseRequest request);

    /**
     * Called after the destruction of a virtual machine and release of all its associated resources.
     * 
     * @param request
     *            Specifies the service binding and virtual machine to release.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> postRelease(InstancePostReleaseRequest request);

    /**
     * Called on template resource change.
     * 
     * @param request
     *            Specifies the service binding and virtual machine to reconfigure.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> preReconfigure(InstancePreReconfigureRequest request);

    /**
     * Called after the completion of a template resource change.
     * 
     * @param request
     *            Specifies the service binding and virtual machine to reconfigure.
     * @return Promise to results on completion.
     */
    public Promise<InstanceResponse> postReconfigure(InstancePostReconfigureRequest request);

}
