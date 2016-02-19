package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Container;
import com.servicemesh.agility.api.PublishRequest;
import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.VersionedItem;

/**
 * Exposes operations to manage versioned items.
 */
public interface IVersion<T extends VersionedItem> extends IWorkflow<T>
{

    /**
     * Check out an asset into the specified container.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @param container
     *            An instance or derived class of com.servicemesh.agility.api.Container
     * @return The instance of VersionedItem after check out.
     * @throws Exception
     */
    public T checkOut(T object, Container container) throws Exception;

    /**
     * Clone an asset into the specified container. The new asset will have no slot.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @param container
     *            An instance or derived class of com.servicemesh.agility.api.Container
     * @return The instance of VersionedItem after check out.
     * @throws Exception
     */
    public T clone(T object, Container container) throws Exception;

    /**
     * Check in an asset into the specified container.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @param container
     *            An instance or derived class of com.servicemesh.agility.api.Container
     * @return The instance of VersionedItem after check in.
     * @throws Exception
     */
    public T checkIn(T object, Container container, String comment) throws Exception;

    /**
     * Check in an asset into the specified container.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @param container
     *            An instance or derived class of com.servicemesh.agility.api.Container
     * @param request
     *            Additional publish parameters including comment and sub asset versioning control
     * @return The instance of VersionedItem after check in.
     * @throws Exception
     */
    public T checkIn(T object, Container container, PublishRequest request) throws Exception;

    /**
     * Returns set of users defined as approvers for a given asset.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @param container
     *            An instance or derived class of com.servicemesh.agility.api.Container
     * @throws Exception
     */
    public void getApprovers(T object, Container container, PublishRequest request, StringBuilder users, StringBuilder groups)
            throws Exception;

    /**
     * A pending checkin was approved. Implements callback action.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @param container
     *            An instance or derived class of com.servicemesh.agility.api.Container
     * @return The instance of VersionedItem after check in.
     * @throws Exception
     */
    public T approved(T object, Container container, String comment) throws Exception;

    /**
     * A pending checkin was approved. Implements callback action.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @param container
     *            An instance or derived class of com.servicemesh.agility.api.Container
     * @param request
     *            The PublishRequest for this approval
     * @return The instance of VersionedItem after check in.
     * @throws Exception
     */
    public T approved(T object, Container container, PublishRequest request) throws Exception;

    /**
     * A pending checkin was rejected. Implements callback action.
     * 
     * @param object
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @param request
     *            The PublishRequest for this rejection
     * @return The instance of VersionedItem after check in.
     * @throws Exception
     */
    public T rejected(T object, PublishRequest request) throws Exception;

    /**
     * List all specified versions associated with a versioned item.
     * 
     * @param versions
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @return A list of all matching versions.
     * @throws Exception
     */
    public List<T> list(String versions) throws Exception;

    /**
     * List all specified versions associated with a versioned item.
     * 
     * @param context
     *            contains the user specified search parameters
     * @param versions
     *            An instance of a derived class of com.servicemesh.agility.api.VersionedItem
     * @return A list of all matching versions.
     * @throws Exception
     */
    public List<T> list(Context context, String versions) throws Exception;

    /**
     * Get all versions of an asset by slot
     * 
     * @param slotId
     *            asset slot id
     * @param includeHead
     *            if true include head asset
     * @return A list of all versions
     * @throws Exception
     */
    public List<T> getAllVersions(int slotId, boolean includeHead) throws Exception;

    public Task deleteSlot(int slotId) throws Exception;

    /**
     * Make versioned asset as the head asset.
     * 
     * @param id
     *            represents id of versioned asset to mark as head.
     * @param context
     *            contains the user specified search parameters.
     * @return The updated asset
     * @throws Exception
     */
    public T setHead(int id, Context context) throws Exception;

}
