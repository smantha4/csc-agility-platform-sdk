package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.Volume;

public interface IVolume
{

    public Task createVolume(Instance instance, Volume volume) throws Exception;

    /**
     * Extend the identified logical volume attached to the specified instance by one disk.
     * 
     * @param instance
     *            The instance to which the logical volume is attached.
     * @param volumeId
     *            The ID of the volume to extend.
     * @return The scheduled task which will perform the steps necessary to extend the volume.
     * @throws Exception
     */
    public Task extendVolume(Instance instance, int volumeId) throws Exception;

    /**
     * <pre>
     * return a list of task :
     * with one task for each volume of the instance
     * - the task name           is the volume name
     * - the task status         is "success" or "failure"
     *                           success means the volume can     be extended
     *                           success means the volume can NOT be extended
     * - the task error message  is filled in case status "failure"
     * - the task complete       is always true
     *
     * &#64;param instance The instance to which the logical volume is attached.
     * &#64;return Tasklist with informations about permission to extend each volume
     * (one task per volume).
     * </pre>
     **/
    public List<Task> testExtendVolume(Instance instance) throws Exception;

    /**
     * <pre>
     * returns a task :
     * for the specified volume of the instance
     * - the task status         is "success" or "failure"
     *                           success means the volume can     be extended
     *                           success means the volume can NOT be extended
     * - the task error message  is filled in case status "failure"
     * - the task complete       is always true
     *
     * &#64;param instance The instance to which the logical volume is attached.
     * &#64;param volumeStorageId the ID of the VolumeStorage
     * &#64;return Task with informations about permission to extend the volume
     * </pre>
     **/
    public Task testExtendVolume(Instance instance, int volumeStorageId) throws Exception;
}
