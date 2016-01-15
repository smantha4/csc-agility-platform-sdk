package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Task;

/**
 * Exposes operations around workflow
 */
public interface IWorkflow<T extends Asset>
{

    public final String ASSET_INPROGRESS = "In Progress";
    public final String ASSET_REJECTED = "Rejected";
    public final String ASSET_APPROVED = "Approved";
    public final String ASSET_PENDING = "Pending";
    public final String ASSET_FAILED = "Failed";

    /**
     * Approve an asset.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.Asset
     * @param comment
     *            Comments on approval
     * @return The instance of Asset after approval.
     * @throws Exception
     */
    @Deprecated
    public T approve(T object, String comment) throws Exception;

    /**
     * Approve an asset.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.Asset
     * @param comment
     *            Comments on approval
     * @return The workflow Task associated with the asset approval.
     * @throws Exception
     */
    public Task approvePerTask(T object, String comment) throws Exception;

    /**
     * Reject an asset.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.Asset
     * @param comment
     *            Reason for rejection.
     * @return The instance of Asset after rejection.
     * @throws Exception
     */
    @Deprecated
    public T reject(T object, String comment) throws Exception;

    /**
     * Reject an asset.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.Asset
     * @param comment
     *            Reason for rejection.
     * @return The workflow Task associated with the asset rejection.
     * @throws Exception
     */
    public Task rejectPerTask(T object, String comment) throws Exception;
}
