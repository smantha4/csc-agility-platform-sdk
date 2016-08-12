package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.StorageCreateFromSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageResponse;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSnapshotCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSnapshotDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSnapshotResponse;
import com.servicemesh.core.async.ResponseHandler;

public interface IStorageSnapshot
{
    public ICancellable create(StorageSnapshotCreateRequest request, ResponseHandler<StorageSnapshotResponse> handler);

    public ICancellable delete(StorageSnapshotDeleteRequest request, ResponseHandler<StorageSnapshotResponse> handler);

    public ICancellable createFromSnapshot(StorageCreateFromSnapshotRequest request, ResponseHandler<StorageResponse> handler);
}
