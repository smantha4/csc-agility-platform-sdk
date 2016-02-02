package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.ResourceMetric;

public interface IResource<T>
{

    public List<ResourceMetric> getUsage(T asset) throws Exception;
}
