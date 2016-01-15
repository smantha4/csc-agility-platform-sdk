package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.VolumeStorage;

public interface IVolumeStorage
{

    public Task attachVolumeStorage(Instance instance, VolumeStorage volume) throws Exception;

    public Task detachVolumeStorage(Instance instance, VolumeStorage volume) throws Exception;

}
