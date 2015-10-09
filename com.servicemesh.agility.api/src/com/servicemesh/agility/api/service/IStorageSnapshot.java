package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.VolumeStorage;
import com.servicemesh.agility.api.VolumeStorageSnapshot;
import com.servicemesh.agility.api.Task;

public interface IStorageSnapshot 
{
	public Task createSnapshot(VolumeStorage storage, VolumeStorageSnapshot snapshot) throws Exception;
	public Task revertSnapshot(VolumeStorage storage, VolumeStorageSnapshot snapshot) throws Exception;
	public Task deleteSnapshot(VolumeStorage storage, VolumeStorageSnapshot snapshot) throws Exception;
}
