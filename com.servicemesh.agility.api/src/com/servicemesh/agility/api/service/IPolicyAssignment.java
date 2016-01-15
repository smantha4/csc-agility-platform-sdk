package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Assetlist;
import com.servicemesh.agility.api.Policy;
import com.servicemesh.agility.api.Task;

public interface IPolicyAssignment<T>
{

    public Assetlist searchPolicyAssignments(T item, Context context) throws Exception;

    public boolean assignPolicy(T object, Policy acl) throws Exception;

    public Task removePolicy(T object, Policy acl) throws Exception;

    public List<Policy> getPolicies(T Object) throws Exception;

}
