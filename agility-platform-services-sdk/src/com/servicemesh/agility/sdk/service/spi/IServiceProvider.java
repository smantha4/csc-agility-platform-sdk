/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.spi;

import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPingRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderSyncRequest;
import com.servicemesh.core.async.Promise;

/**
 * Provides a standard set of operations for managing the service provider. Additionally provides lifecycle callouts on changes to
 * the service provider configuration/definition.
 */
public interface IServiceProvider
{

    /**
     * Can be used by the service provider to validate the configuration of the adapter and allow/reject the settings.
     * 
     * @param request
     *            Contains the desired configuration
     * @return Promise to accept/reject the configuration.
     */
    public Promise<ServiceProviderResponse> preCreate(ServiceProviderPreCreateRequest request);

    /**
     * Called after successful configuration of the service provider. An adapter may require some sync of the initial
     * configuration with the actual service provider.
     * 
     * @param request
     *            Contains the initial configuration.
     * @return Promise to results on completion..
     */
    public Promise<ServiceProviderResponse> postCreate(ServiceProviderPostCreateRequest request);

    /**
     * Can be used by the adapter to validate the configuration of the service provider and allow/reject the settings.
     * 
     * @param request
     *            Contains the desired configuration
     * @return Promise to results on completion..
     */
    public Promise<ServiceProviderResponse> preUpdate(ServiceProviderPreUpdateRequest request);

    /**
     * Called after successful configuration of the service provider. An adapter may require some sync of the new configuration
     * with the actual service provider.
     * 
     * @param request
     *            Contains the initial configuration.
     * @return Promise to results on completion..
     */
    public Promise<ServiceProviderResponse> postUpdate(ServiceProviderPostUpdateRequest request);

    /**
     * Can be used by the service provider to validate the system is in a valid state to perform a delete.
     * 
     * @param request
     *            Specifies the service provider instance.
     * @return Promise to results on completion..
     */
    public Promise<ServiceProviderResponse> preDelete(ServiceProviderPreDeleteRequest request);

    /**
     * Should be used to clean up any resources associated with the service provider instance.
     * 
     * @param request
     *            Specifies the service provider instance.
     * @return Promise to results on completion..
     */
    public Promise<ServiceProviderResponse> postDelete(ServiceProviderPostDeleteRequest request);

    /**
     * Called from Agility platform to validate connectivity with the service provider.
     * 
     * @param request
     *            Specifies the service provider instance.
     * @return Promise to results on completion..
     */
    public Promise<ServiceProviderResponse> ping(ServiceProviderPingRequest request);

    /**
     * Called from Agility platform to request that the current service configuration be synced with the provider.
     * 
     * @param request
     *            Specifies the desirned configuration.
     * @return Promise to results on completion..
     */
    public Promise<ServiceProviderResponse> sync(ServiceProviderSyncRequest request);

    /**
     * Called from Agility platform to request that the service provider be started. This may not apply to many service providers.
     * In this case the adapter should just indicate successful completion of the request.
     * 
     * @param request
     *            Specifies the desirned configuration.
     * @return Promise to results on completion..
     */
    public Promise<ServiceProviderResponse> start(ServiceProviderStartRequest request);

    /**
     * Called from Agility platform to request that the service provider be stopped. This may not apply to many service providers.
     * In this case the adapter should just indicate successful completion of the request.
     * 
     * @param request
     *            Specifies the desirned configuration.
     * @return Promise to results on completion..
     */
    public Promise<ServiceProviderResponse> stop(ServiceProviderStopRequest request);

}
