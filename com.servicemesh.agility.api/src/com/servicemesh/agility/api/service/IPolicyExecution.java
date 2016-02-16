/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Link;
import com.servicemesh.agility.api.Policy;
import com.servicemesh.agility.api.Task;

/**
 * Exposes operations for assets that support on-demand policy execution
 */
public interface IPolicyExecution<T>
{
    /**
     * Execute a policy against the specified asset.
     *
     * @param parent
     *            A parent task instance. May be null.
     * @param object
     *            An instance of the targeted Asset
     * @param policy
     *            The Policy to be executed.
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task executePolicy(Task parent, T object, Policy policy) throws Exception;

    /**
     * Indicates if policy execution is applicable for the specified asset.
     *
     * @param object
     *            An instance of the targeted Asset
     * @param policy
     *            The Policy to be executed.
     * @return True if the policy is applicable
     * @throws Exception
     */
    public boolean isPolicyApplicable(T object, Policy policy) throws Exception;

    /**
     * Gets the fixed order children assets against which policy execution is applicable
     *
     * @param object
     *            An instance of the targeted Asset
     * @param policy
     *            The Policy to be executed.
     * @return A list of fixed order children assets
     * @throws Exception
     */
    public List<Link> getFixedOrderForPolicy(T object, Policy policy) throws Exception;

    /**
     * Gets the any order children assets against which policy execution is applicable
     *
     * @param object
     *            An instance of the targeted Asset
     * @param policy
     *            The Policy to be executed.
     * @return A list of any order children assets
     * @throws Exception
     */
    public List<Link> getAnyOrderForPolicy(T object, Policy policy) throws Exception;
}
