package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.LaunchItemDeployment;
import com.servicemesh.agility.api.Task;

public interface ILaunchItemDeployment extends IWorkflow<LaunchItemDeployment>
{

    /**
     * Deploys launch item
     * 
     * @param deployment
     * @param start
     *            optionally starts the deployment.
     * @return returns task which can be used query for deployed launch item in Task.result field.
     * @throws Exception
     */
    @Deprecated
    public Task deploy(LaunchItemDeployment deployment, boolean start) throws Exception;

    public LaunchItemDeployment deployment(LaunchItemDeployment item, boolean start) throws Exception;

}
