package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.VolumeStorage;
import com.servicemesh.agility.api.Task;

public interface IStorageFromSnapshot 
{
	public Task createVolumeStorageFromSnapshot(VolumeStorage storage, int snapshotID) throws Exception;
}
