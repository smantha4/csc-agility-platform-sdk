package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Task;

public interface IDBBackup
{

    /**
     * Generates an Agility Database backup on demand
     * 
     * @param username
     *            Agility user executing the database backup
     * @return Task object representing the database backup action
     * @throws Exception
     */
    public Task createDBBackup(String username) throws Exception;

}
