package com.servicemesh.agility.api.service;

import java.util.Map;

import com.servicemesh.agility.api.ConfigurationArtifact;
import com.servicemesh.agility.api.PublishRequest;

public interface ConfigurationArtifactOperations
{
    public void setSyncPending(ConfigurationArtifact configurationArtifact) throws Exception;

    public void getApprovers(ConfigurationArtifact configurationArtifact, int locationId, PublishRequest request,
            StringBuilder users, StringBuilder groups) throws Exception;

    public void approved(ConfigurationArtifact configurationArtifact, Integer publisherId, String comment, Integer scriptId,
            Integer artifactLocationId, String artifactPath, Long lastModified, Map<String, Object> artifactParams)
                    throws Exception;

    void rejected(ConfigurationArtifact configurationArtifact) throws Exception;
}
