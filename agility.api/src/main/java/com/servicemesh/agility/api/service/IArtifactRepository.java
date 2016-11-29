package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Artifact;
import com.servicemesh.agility.api.ServiceProvider;

// Exposes operations for the Artifactory Adapter
public interface IArtifactRepository
{

    /**
     * Return the list of all artifacts for this repository
     *
     * @param String
     * @throws Exception
     */
    public List<Artifact> getAllArtifacts(String repoPath) throws Exception;

    /**
     * Return the list of all service providers of for an artifact
     *
     * @throws Exception
     */
    public List<ServiceProvider> getArtifactServiceProviders() throws Exception;

    /**
     * Publish artifact meta data to agility
     *
     * @param String
     * @throws Exception
     */
    public void publishArtifactMetadata(Artifact artifact) throws Exception;

    /**
     * Return the artifact
     *
     * @param String
     * @throws Exception
     */
    public byte[] downloadArtifacts(String repoPath) throws Exception;

}