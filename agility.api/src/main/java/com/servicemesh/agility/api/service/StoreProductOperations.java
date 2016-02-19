package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Item;
import com.servicemesh.agility.api.LaunchItem;
import com.servicemesh.agility.api.LaunchItemDeployment;
import com.servicemesh.agility.api.PublishRequest;

public interface StoreProductOperations
{
    public void setOrderLocked(LaunchItem launchItem) throws Exception;

    public void getOrderApprovers(Item launchItem, int locationId, PublishRequest request, StringBuilder users,
            StringBuilder groups) throws Exception;

    public void getDeployApprovers(Item launchItem, int locationId, PublishRequest request, StringBuilder users,
            StringBuilder groups) throws Exception;

    public void orderApproved(LaunchItem launchItem, Integer approverId, String comment) throws Exception;

    public void orderRejected(LaunchItem launchItem, String comment) throws Exception;

    public void setDeployLocked(LaunchItemDeployment launchItemDeployment) throws Exception;

    public void deployApproved(LaunchItemDeployment launchItemDeployment, Integer approverId, String comment) throws Exception;

    public void deployRejected(LaunchItemDeployment launchItemDeployment, String comment) throws Exception;
}
