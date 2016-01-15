/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.spi;

import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.core.async.Promise;

/**
 * Defines a set of lifecycle callouts that can be implemented by a service provider to take action when a service instance bound
 * to the service changes state. As an example, a database service connected to a load balancer service should be
 * registered/deregistered to/from the load balancer as database service instances are provisioned and/or released.
 */
public interface IServiceInstanceLifecycle
{

    /**
     * Called by the platform on initial provisioning of a service instance.
     * 
     * @param request
     *            Specifies the service binding and service instance settings
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preProvision(ServiceInstancePreProvisionRequest request);

    /**
     * Called by the platform after initial provisioning of a service instance completes. Enables the service provider to register
     * the service instance with a service offering and/or allocate service resources for the specific service instance.
     * 
     * @param request
     *            Specifies the service binding and service instance settings
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postProvision(ServiceInstancePostProvisionRequest request);

    /**
     * Called to stop a service instance without releasing underlying resources.
     * 
     * @param request
     *            Specifies the service binding and service instance to stop.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preStop(ServiceInstancePreStopRequest request);

    /**
     * Called after a service instance has been stopped without releasing underlying resources. This provides the service provider
     * the option to deregister/cleanup resources associated with a stopped service instance.
     * 
     * @param request
     *            Specifies the service binding and service instance that was stopped.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postStop(ServiceInstancePostStopRequest request);

    /**
     * Called to start service instance that is in the stopped state. Provides the service provider the option to validate the
     * service instance settings are compatible with the service offering.
     * 
     * @param request
     *            Specifies the service binding and service instance to start.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preStart(ServiceInstancePreStartRequest request);

    /**
     * Called after starting a service instance that was in the stopped state. Proves the service provider the option to register
     * or provision services for the service instance.
     * 
     * @param request
     *            Specifies the service binding and service instance that was started.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postStart(ServiceInstancePostStartRequest request);

    /**
     * Called to initiate a restart of a service instance.
     * 
     * @param request
     *            Specifies the service binding and service instance to restart.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preRestart(ServiceInstancePreRestartRequest request);

    /**
     * Called after a restart of the service instance.
     * 
     * @param request
     *            Specifies the service binding and service instance that was restarted.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postRestart(ServiceInstancePostRestartRequest request);

    /**
     * Called to destroy a service instance and release all associated resources.
     * 
     * @param request
     *            Specifies the service binding and service instance to release.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preRelease(ServiceInstancePreReleaseRequest request);

    /**
     * Called after the destruction of a service instance and a release of all its associated resources.
     * 
     * @param request
     *            Specifies the service binding and service instance to release.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postRelease(ServiceInstancePostReleaseRequest request);
}
