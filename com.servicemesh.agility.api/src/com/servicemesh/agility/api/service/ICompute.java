package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Task;

public interface ICompute<T>
{
    public boolean preProvision(Task task, T asset) throws Exception;

    public void postProvisionBefore(Task task, T asset) throws Exception;

    public void postProvisionAfter(Task task, T asset) throws Exception;

    public boolean preStart(Task task, T asset) throws Exception;

    public void postStartBefore(Task task, T asset) throws Exception;

    public void postStartAfter(Task task, T asset) throws Exception;

    public boolean preStop(Task task, T asset) throws Exception;

    public void postStop(Task task, T asset) throws Exception;

    public boolean preRelease(Task task, T asset) throws Exception;

    public void postRelease(Task task, T asset) throws Exception;

    public void postStartup(Task task, T asset) throws Exception;

    public boolean preRestart(Task task, T asset) throws Exception;

    public void postRestart(Task task, T asset) throws Exception;
}
