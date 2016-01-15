package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.AccessRight;
import com.servicemesh.agility.api.AccessRightSet;
import com.servicemesh.agility.api.Container;
import com.servicemesh.agility.api.ContainerRights;
import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.User;
import com.servicemesh.agility.api.UserGroup;

/**
 * Exposes operations on an container.
 */
public interface IContainer
{

    public Container addSecurity(Container container, ContainerRights security) throws Exception;

    public Task deleteSecurity(Container container, int security_id) throws Exception;

    public Container addSecurityUser(Container container, int security_id, User user) throws Exception;

    public Task deleteSecurityUser(Container container, int security_id, int user_id) throws Exception;

    public Container addSecurityUserGroup(Container container, int security_id, UserGroup usergroup) throws Exception;

    public Task deleteSecurityUserGroup(Container container, int security_id, int usergroup_id) throws Exception;

    public Container addSecurityRights(Container container, int security_id, AccessRightSet rights) throws Exception;

    public Task deleteSecurityRights(Container container, int security_id, int rights_id) throws Exception;

    public Container addSecurityAccessRight(Container container, int security_id, int rights_id, AccessRight accessright)
            throws Exception;

    public Task deleteSecurityAccessRight(Container container, int security_id, int rights_id, int accessright_id)
            throws Exception;

}
