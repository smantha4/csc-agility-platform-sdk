/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.spi;

import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceValidateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.core.async.Promise;

/**
 * A service instance is created to map a service definition in a blueprint to an available service provider. It relates the
 * service to the service provider and captures the configuration of the service when the blueprint is deployed. The service
 * instance additionally relates the service to any dependent services or workloads (e.g. templates after deployment). The
 * following lifecycle operations enable the adapter to manage resources associated with the service instance within the
 * corresponding provider. As an example, a blueprint containing a dbaas service (rds) will require an rds instance/account. On
 * creation of the service instance the adapter can provision the rds instance and associated the instance identifier/connection
 * attributes with the service instance.
 */
public interface IServiceInstance
{

    /**
     * Can be used by the adapter to validate the configuration of the service and prevent deployment of the blueprint from
     * proceeding in the event of an invalid configuration.
     * 
     * @param request
     *            Desired configuration of the service.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> validate(ServiceInstanceValidateRequest request);

    /**
     * Would typically be used by the adapter to provision resources associated with the service. This would typically be a saas
     * account instance that is provisioned and the associated identifier/attributes persisted on the service instance.
     * 
     * @param request
     *            Desired configuration of the service.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> provision(ServiceInstanceProvisionRequest request);

    /**
     * When the service provider exposes configuration settings that may be updated after creation of the service, the adapter
     * should implement reconfigure to push the configuration change into the provider.
     * 
     * @param request
     *            Desired configuration of the service.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> reconfigure(ServiceInstanceReconfigureRequest request);

    /**
     * If the service supports start/stop operations, may be used to restart the service from a stopped/suspended state.
     * 
     * @param request
     *            Desired configuration of the service.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> start(ServiceInstanceStartRequest request);

    /**
     * If the service supports start/stop operations, may be used to put the service in a stopped/suspended state.
     * 
     * @param request
     *            Desired configuration of the service.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> stop(ServiceInstanceStopRequest request);

    /**
     * Should release/clean up any resources associated with the service instance.
     * 
     * @param request
     *            Desired configuration of the service.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> release(ServiceInstanceReleaseRequest request);
}
