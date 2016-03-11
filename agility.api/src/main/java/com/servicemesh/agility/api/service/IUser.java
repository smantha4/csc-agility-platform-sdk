/*
 * Copyright (C) 2016 Computer Science Corporation
 * All rights reserved.
 *
 */
package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.User;

/**
 * @author akofman
 */
public interface IUser
{
    /**
     * Returns true if specified user is an admin user. Lookup user first by id, then by name.
     *
     * @param user
     * @return
     * @throws Exception
     *             thrown if not allowed to view the specified user or user doesn't exist
     */
    public boolean isAdmin(User user) throws Exception;
}
