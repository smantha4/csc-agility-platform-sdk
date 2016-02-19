/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.spi;

import com.servicemesh.agility.sdk.service.msgs.ConnectionPostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.core.async.Promise;

/**
 * Defines generic methods for an adapter to process notifications of creation, modification, and deletion of an Agility asset
 * connection.
 */
public interface IConnection
{

    /**
     * Can be used by the adapter to validate the configuration of the connection prior to creation and allow/reject the settings.
     * 
     * @param request
     *            Contains the desired configuration
     * @return Promise to accept/reject the configuration.
     */
    public Promise<ServiceProviderResponse> preCreate(ConnectionPreCreateRequest request);

    /**
     * Called after successful creation of the connection. An adapter may need to push this configuration to the actual service
     * provider.
     * 
     * @param request
     *            Contains the initial configuration.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postCreate(ConnectionPostCreateRequest request);

    /**
     * Can be used by the adapter to validate the configuration of the connection prior to an update and allow/reject the
     * settings.
     * 
     * @param request
     *            Contains the desired configuration
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preUpdate(ConnectionPreUpdateRequest request);

    /**
     * Called after successful update of the connection. An adapter may need to push this configuration to the actual service
     * provider.
     * 
     * @param request
     *            Contains the initial configuration.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postUpdate(ConnectionPostUpdateRequest request);

    /**
     * Can be used by the adapter to validate the system is in a valid state to perform a delete.
     * 
     * @param request
     *            Specifies the service provider instance.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> preDelete(ConnectionPreDeleteRequest request);

    /**
     * Should be used to clean up any actual service provider resources associated with the connection.
     * 
     * @param request
     *            Specifies the service provider instance.
     * @return Promise to results on completion.
     */
    public Promise<ServiceProviderResponse> postDelete(ConnectionPostDeleteRequest request);

}
