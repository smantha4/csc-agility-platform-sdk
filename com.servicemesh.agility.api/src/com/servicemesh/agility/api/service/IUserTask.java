package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.UserTask;

public interface IUserTask
{

    public UserTask approve(UserTask task, String comment, Context imports) throws Exception;

    public UserTask reject(UserTask task, String comment, Context imports) throws Exception;

    public UserTask action(UserTask task, String action, String comment, Context imports) throws Exception;

}
