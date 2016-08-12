package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.InstanceCreateSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceRemoveAllSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceRemoveSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceRevertSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceSnapshotResponse;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceUpdateSnapshotRequest;
import com.servicemesh.core.async.ResponseHandler;

/**
 * Defines basic instance operations expected to be supported by all adapters:
 */
public interface ISnapshot
{

    public ICancellable createSnapshot(InstanceCreateSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler);

    public ICancellable updateSnapshot(InstanceUpdateSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler);

    public ICancellable removeSnapshot(InstanceRemoveSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler);

    public ICancellable removeAllSnapshots(InstanceRemoveAllSnapshotRequest request,
            ResponseHandler<InstanceSnapshotResponse> handler);

    public ICancellable revertSnapshot(InstanceRevertSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler);

}
