package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.VolumeStorage;
import com.servicemesh.agility.api.VolumeStorageSnapshot;

public interface IStorageSnapshot
{
    public Task createSnapshot(VolumeStorage storage, VolumeStorageSnapshot snapshot) throws Exception;

    public Task revertSnapshot(VolumeStorage storage, VolumeStorageSnapshot snapshot) throws Exception;
    
    public Task revertSnapshot(VolumeStorage storage, VolumeStorageSnapshot snapshot, String snapshotDeleteOptions) throws Exception;

    public Task deleteSnapshot(VolumeStorage storage, VolumeStorageSnapshot snapshot) throws Exception;
}
