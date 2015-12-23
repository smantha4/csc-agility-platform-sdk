package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Container;
import com.servicemesh.agility.api.DeploymentPlan;
import com.servicemesh.agility.api.Blueprint;
import com.servicemesh.agility.api.DeploymentRequest;
import com.servicemesh.agility.api.PlanEvalRequest;
import com.servicemesh.agility.api.Task;

public interface IBlueprint {

		public DeploymentPlan evaluate(Blueprint blueprint, Container container, PlanEvalRequest planRequest, Context context) throws Exception;

		public DeploymentPlan validatePlan(Blueprint blueprint, Container container, PlanEvalRequest planRequest, DeploymentPlan plan) throws Exception;

		public Blueprint validateBlueprint(Blueprint blueprint, boolean randomize, boolean throwUponFailure, Context context) throws Exception;

		public Task deploy(Blueprint blueprint, Container container, PlanEvalRequest planRequest, DeploymentRequest deployRequest, Context context) throws Exception;
}
