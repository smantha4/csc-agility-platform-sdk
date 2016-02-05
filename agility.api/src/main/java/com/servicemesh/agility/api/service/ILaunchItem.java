/**
 *              COPYRIGHT (C) 2008-2012 SERVICEMESH, INC.
 *                        ALL RIGHTS RESERVED.
 *                   CONFIDENTIAL AND PROPRIETARY.
 *
 *  ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS
 *  (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT
 *  ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.
 *  ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE
 *  AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED
 *  IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY
 *  OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY
 *  SERVICEMESH IS STRICTLY PROHIBITED.
 */

package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.LaunchItem;
import com.servicemesh.agility.api.ServiceInstance;
import com.servicemesh.agility.api.Task;

public interface ILaunchItem extends IWorkflow<LaunchItem>
{
    /**
     * Order product edition specifying location.
     * 
     * @param item
     *            The launch item with relevant fields populated. A Topology product requires edition and parent. A Project
     *            product requires edition, parent, name and description (optional if not set). A Blueprint product requires
     *            edition and parent.
     * @return LaunchItem which represents ordered product. If workflow applies, then returned launch item can be used to complete
     *         order once the order has been approved.
     * @throws Exception
     */
    public LaunchItem order(LaunchItem item) throws Exception;

    /**
     * Request to start the specified launch item.
     * 
     * @param object
     *            An instance of [LaunchItem]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task start(LaunchItem object) throws Exception;

    /**
     * Request to stop the specified launch item.
     * 
     * @param object
     *            An instance of [LaunchItem]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task stop(LaunchItem object) throws Exception;

    /**
     * Request to restart the specified launch item.
     * 
     * @param object
     *            An instance of [LaunchItem]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task restart(LaunchItem object) throws Exception;

    /**
     * Request to release the specified launch item.
     * 
     * @param object
     *            An instance of [LaunchItem]
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task release(LaunchItem object) throws Exception;

    /**
     * Request to retrieve the list of instances provisioned for the LaunchItem.
     * 
     * @param launchItem
     *            An instance of [LaunchItem]
     * @return A list of the LaunchItem's instances.
     * @throws Exception
     */
    public List<Instance> getInstances(LaunchItem launchItem) throws Exception;

    /**
     * Request to retrieve the list of instances provisioned for the LaunchItem.
     * 
     * @param launchItem
     *            An instance of [LaunchItem]
     * @param context
     *            a Context optionally used to specify desired fields. A null means to return all fields.
     * @return A list of the LaunchItem's instances.
     * @throws Exception
     */
    public List<Instance> getInstances(LaunchItem launchItem, Context context) throws Exception;

    /**
     * Request to retrieve the list of service instances provisioned for the LaunchItem.
     * 
     * @param launchItem
     *            An instance of [LaunchItem]
     * @return A Linklist of Link objects referencing service instances.
     * @throws Exception
     */
    public List<ServiceInstance> getServiceInstances(LaunchItem launchItem) throws Exception;
}
