/**
 * COPYRIGHT (C) 2008-2012 SERVICEMESH, INC.  ALL RIGHTS RESERVED.  CONFIDENTIAL AND PROPRIETARY.
 *
 * ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.     ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY SERVICEMESH IS STRICTLY PROHIBITED.
 *
 */

package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.HotswapList;
import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.Link;
import com.servicemesh.agility.api.Package;
import com.servicemesh.agility.api.Policy;
import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.Template;

/**
 * Exposes additional operations to manage virtual machine templates.
 */
public interface ITemplate
{

    /**
     * Create instance objects based on template definitions without starting them.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @throws Exception
     */
    public List<Instance> createInstances(Template template) throws Exception;

    /**
     * Creates a new package and adds it to the template definition
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @return Completion status
     * @throws Exception
     */
    public boolean addPackage(Template template, Package pkg) throws Exception;

    /**
     * Removes a package from the template definition
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @return Completion status
     * @throws Exception
     */
    public Task removePackage(Template template, Package pkg) throws Exception;

    /**
     * Adds a package to the template definition.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @return Completion status
     * @throws Exception
     */
    public boolean attachPackage(Template template, Package pkg) throws Exception;

    /**
     * Adds a ConfigurationResource reference to the template definition.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @param cr
     *            A reference to the ConfigurationResource
     * @return Completion status
     * @throws Exception
     */
    public boolean addConfigurationResource(Template template, Link cr) throws Exception;

    /**
     * Removes a ConfigurationResource reference from the template definition
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @param crId
     *            ConfigurationResource identifier
     * @return Completion status
     * @throws Exception
     */
    public Task removeConfigurationResource(Template template, Integer crId) throws Exception;

    /**
     * Returns any scale-up policy associated with the template.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @return An instance of com.servicemesh.agility.api.Policy
     * @throws Exception
     */
    public Policy getScaleUpPolicy(Template template) throws Exception;

    /**
     * Define the scale-up policy associated with the template.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @param policy
     *            The policy definition as an instance of com.servicemesh.agility.api.Policy
     * @return The policy definition after application to the template.
     * @throws Exception
     */
    public Policy setScaleUpPolicy(Template template, Policy policy) throws Exception;

    /**
     * Deletes any scale-up policy defined for the template.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @throws Exception
     */
    public void deleteScaleUpPolicy(Template template) throws Exception;

    /**
     * Returns any scale-down policy associated with the template.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @return An instance of com.servicemesh.agility.api.Policy
     * @throws Exception
     */
    public Policy getScaleDownPolicy(Template template) throws Exception;

    /**
     * Define the scale-down policy associated with the template.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @param policy
     *            The policy definition as an instance of com.servicemesh.agility.api.Policy
     * @return The policy definition after application to the template.
     * @throws Exception
     */
    public Policy setScaleDownPolicy(Template template, Policy policy) throws Exception;

    /**
     * Deletes any scale-down policy defined for the template.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @throws Exception
     */
    public void deleteScaleDownPolicy(Template template) throws Exception;

    /**
     * If any instances associated with the template are in a starting state this call does not return until they have obtained a
     * network address and should be reachable on the network.
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @throws Exception
     */
    public List<Link> waitForBoot(Template template) throws Exception;

    /**
     * This method is functionally the same as the waitForBoot(Template) method, but takes a second parameter specifying the
     * maximum time that one will wait for the method to return
     * 
     * @param template
     *            An instance of com.servicemesh.agility.api.Template
     * @param timeout
     *            The max time to wait (in seconds) for instances to come up
     * @throws Exception
     */
    public List<Link> waitForBoot(Template template, Integer timeout) throws Exception;

    /**
     * Returns a list of instances that can be hotswapped and a list of instances that need to be restarted in order to have their
     * resources updated.
     * 
     * @param template
     * @throws Exception
     */
    public HotswapList hotswapInstances(Template template) throws Exception;

    /**
     * Request to delete a template.
     * 
     * @param parentTask
     *            Parent task of the delete request
     * @param template
     *            Template to be deleted
     * @param isParentContext
     *            If true, aspect context of the parent is used
     * @return The task specifying the delete request
     * @throws Exception
     */
    public Task delete(Task parentTask, Template template, boolean isParentContext) throws Exception;

    /**
     * Request to remove all associations after all instances have been released from a template
     * 
     * @param parentTask
     *            task that initialized the request
     * @param template
     *            template to perform the action against
     * @param isParentContext
     *            If true the parent context is used for aspect calls
     * @param releaseVolumes
     *            If true volumes are release as part of this action
     * @throws Exception
     */
    public void finalizeDelete(Task parentTask, Template template, boolean isParentContext, boolean releaseVolumes)
            throws Exception;
}
