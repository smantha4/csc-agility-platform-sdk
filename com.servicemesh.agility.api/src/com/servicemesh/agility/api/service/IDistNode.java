/**
 * COPYRIGHT (C) 2008-2012 SERVICEMESH, INC.  ALL RIGHTS RESERVED.  CONFIDENTIAL AND PROPRIETARY.
 *
 * ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.     ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY SERVICEMESH IS STRICTLY PROHIBITED.
 *
 */

package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.DistNode;
import com.servicemesh.agility.api.DistNodePackage;
import com.servicemesh.agility.api.Script;
import com.servicemesh.agility.api.Task;

public interface IDistNode
{

    public static final String START_AGILITY_SCRIPT_NAME = "Agility Service Start";
    public static final String STOP_AGILITY_SCRIPT_NAME = "Agility Service Stop";

    /**
     * Gets the distributed nodes based on the node type. If no nodes of the requested type exist an empty List is returned.
     */
    public List<DistNode> getNodesByType(String type) throws Exception;

    /**
     * Updates the nodeType of the DistNode. 'newType' must map to an existing type under the PropertyType "agilityNodeType" or an
     * Exception will be thrown. An Exception will also be thrown if the node (which maps to an instance) can't be found.
     * 
     * @return The updated DistNode
     * @throws Exception
     */
    public DistNode updateNodeType(int nodeId, String newType) throws Exception;

    /**
     * If the DistNodePackage isn't associated with the given node this method will create the package and add it to the node's
     * list of packages. If the package does exist this method will update the status and/or version depending on their values.
     * Lookup is done by package name, id is ignored if pkg.status is null it is not changed if pkg.version is null it is not
     * changed
     * 
     * @return the DistNode with 'nodeId'
     */
    public DistNode updatePackage(int nodeId, DistNodePackage pkg) throws Exception;

    /**
     * Returns the operational script named 'scriptName' associated with the instance with the given node id, or null if no script
     * found. association can be via template->packages or template->package->dependency package, Throws an exception if the
     * instance isn't found or if it's not a distributed node. throws an exception if an operational script with the given name
     * isn't found.
     */
    public Script getOperationalScriptFromDistNode(int nodeId, String scriptName) throws Exception;

    /**
     * Executes the named script against all distributed nodes. If no operational script with the given name is found on a
     * particular node an error msg is logged and processing continues.
     */
    public Task executeScriptOnAllNodes(String scriptName, String msg) throws Exception;

    /**
     * Gets the distributed node based on the node's public ip address.
     */
    public DistNode getByAddress(String addr) throws Exception;

    /**
     * Gets the distributed node based on the node's uuid.
     */
    public DistNode getByUuid(String uuid) throws Exception;

    /**
     * Gets all Distributed Sync Config Parameters.
     */
    public String getDistSyncParams() throws Exception;

}
