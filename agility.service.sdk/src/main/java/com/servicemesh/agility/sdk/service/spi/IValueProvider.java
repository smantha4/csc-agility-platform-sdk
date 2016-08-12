/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.spi;

import com.servicemesh.agility.sdk.service.msgs.PropertyTypeValueRequest;
import com.servicemesh.agility.sdk.service.msgs.PropertyTypeValueResponse;
import com.servicemesh.core.async.Promise;

/**
 * Provides a hook for retrieving a property type value.
 * 
 * @see ServiceAdapter#registerValueProvider(String, IValueProvider)
 */
public interface IValueProvider
{

    /**
     * Property type value retriever exposed by the service adapter
     *
     * @param request
     *            Specifies the requested property type
     * @return Promise to results on completion.
     */
    public Promise<PropertyTypeValueResponse> getRootValues(PropertyTypeValueRequest request);
}
