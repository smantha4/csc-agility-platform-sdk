package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Solution;
import com.servicemesh.agility.api.SolutionDeployment;

public interface ISolution
{

    /**
     * Save any and fixed artifact order for a solution
     * 
     * @param item
     * @return Solution
     * @throws Exception
     */
    public Solution saveArtifactOrder(Solution item) throws Exception;

    public SolutionDeployment createDeploymentLatest(SolutionDeployment asset, Solution parent, Context context) throws Exception;
}
