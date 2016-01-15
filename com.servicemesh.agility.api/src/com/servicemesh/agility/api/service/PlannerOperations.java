package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Item;
import com.servicemesh.agility.api.PlannerApplication;
import com.servicemesh.agility.api.PublishRequest;

public interface PlannerOperations
{
    public void technicalReview(PlannerApplication plannerApplication) throws Exception;

    public void getApprovers(Item item, int locationId, PublishRequest request, StringBuilder users, StringBuilder groups)
            throws Exception;

    public void reject(PlannerApplication plannerApplication, Integer publisherId) throws Exception;

    public void approve(PlannerApplication plannerApplication, Integer publisherId) throws Exception;
}
