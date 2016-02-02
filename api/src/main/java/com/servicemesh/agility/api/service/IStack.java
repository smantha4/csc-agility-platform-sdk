package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.PolicyAssignment;
import com.servicemesh.agility.api.Stack;
import com.servicemesh.agility.api.Task;

public interface IStack
{

    public Task deleteAllVersions(Stack stack) throws Exception;

    public List<Task> buildStack(int stackId, boolean shutdownOnSuccess, boolean shutdownOnFailure) throws Exception;

    /**
     * Return the set of policy assignments associated with the given stack and container
     * 
     * @param stackId
     *            the stack to search for policy assignments. Technically, the stack's template is queried for its policy
     *            assignments. The user must have VMStack.view permission in order to get the policy assignments.
     * @param containerId
     *            the container to search for policy assignments, recursively up to root
     * @param includeBaseStacks
     *            if true, then the stack and all of its base stacks are examined instead of only the given stack
     * @param policyTypeName
     *            if present, only policy assignments for policies of this type are returned. If not present, then all policy
     *            assignments are returned.
     * @throws Exception
     */
    public List<PolicyAssignment> getPolicyAssignments(int stackId, int containerId, boolean includeBaseStacks,
            String policyTypeName) throws Exception;
}
