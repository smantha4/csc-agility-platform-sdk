/**
 * COPYRIGHT (C) 2013 SERVICEMESH, INC.  ALL RIGHTS RESERVED.  CONFIDENTIAL AND PROPRIETARY.
 *
 * ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.     ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY SERVICEMESH IS STRICTLY PROHIBITED.
 *
 */
package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Task;

public interface IReconfigure<T>
{

    /**
     * Request to start the specified asset.
     * 
     * @param asset
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @param reconfigure
     *            If true, instance specification (e.g. memory and cpu) will get reconfigured
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task start(T asset, boolean reconfigure) throws Exception;

    /**
     * Request to start the specified asset.
     * 
     * @param parent
     *            A parent task instance.
     * @param asset
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @param reconfigure
     *            If true, instance specification (e.g. memory and cpu) will get reconfigured
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task start(Task parent, T asset, boolean reconfigure) throws Exception;

    /**
     * Request to restart the specified asset.
     * 
     * @param asset
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @param reconfigure
     *            If true, instance specification (e.g. memory and cpu) will get reconfigured
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task restart(T asset, boolean reconfigure) throws Exception;

    /**
     * Request to restart the specified asset.
     * 
     * @param parent
     *            A parent task instance.
     * @param asset
     *            An instance of [Project|Environment|Topology|Template|Instance]
     * @param reconfigure
     *            If true, instance specification (e.g. memory and cpu) will get reconfigured
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task restart(Task parent, T asset, boolean reconfigure) throws Exception;

}
