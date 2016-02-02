package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.PublishRequest;
import com.servicemesh.agility.api.SolutionDeployment;

public interface SolutionDeploymentOperations
{

    public void getDeployApprovers(SolutionDeployment solutionDeploymentItem, int locationId, PublishRequest request,
            StringBuilder users, StringBuilder groups) throws Exception;

    public void setSubmitPending(SolutionDeployment solutionDeployment) throws Exception;

    public void deployApproved(SolutionDeployment solutionDeployment, Integer approverId, String comment) throws Exception;

    public void deployRejected(SolutionDeployment solutionDeployment, String comment) throws Exception;

    public void deploy(SolutionDeployment solutionDeployment, Integer approverId, String comment) throws Exception;
}
