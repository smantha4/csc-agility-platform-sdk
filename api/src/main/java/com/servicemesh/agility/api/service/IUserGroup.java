package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.User;
import com.servicemesh.agility.api.UserGroup;

public interface IUserGroup
{

    public UserGroup addUser(UserGroup group, User user) throws Exception;

    public UserGroup deleteUser(UserGroup group, User user) throws Exception;

}
