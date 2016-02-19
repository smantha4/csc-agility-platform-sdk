package com.servicemesh.agility.api.service;

import java.io.InputStream;
import java.util.List;

import com.servicemesh.agility.api.Artifact;
import com.servicemesh.agility.api.ArtifactAttachment;
import com.servicemesh.agility.api.Task;

/**
 * Exposes operations to manage versioned items.
 */
public interface IArtifact
{

    public Task publish(Artifact artifact, ArtifactAttachment attachment, InputStream input, Context context) throws Exception;

    public Task publish(Artifact artifact, List<ArtifactAttachment> attachments, List<InputStream> inputs, Context context)
            throws Exception;

}
