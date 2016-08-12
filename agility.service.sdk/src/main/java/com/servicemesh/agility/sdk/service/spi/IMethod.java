/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.spi;

import java.util.Map;

import com.servicemesh.agility.sdk.service.msgs.MethodRequest;
import com.servicemesh.agility.sdk.service.msgs.MethodResponse;
import com.servicemesh.agility.sdk.service.msgs.MethodVariable;
import com.servicemesh.core.async.Promise;

/**
 * Provides a hook for executing a named method.
 * 
 * @see ServiceAdapter#registerMethod(String, IMethod)
 */
public interface IMethod
{

    /**
     * Method call exposed by the service adapter
     * 
     * @param request
     *            Specifies the specific method parameters
     * @return Promise to results on completion.
     */
    public Promise<MethodResponse> execute(MethodRequest request, Map<String, MethodVariable> params);
}
