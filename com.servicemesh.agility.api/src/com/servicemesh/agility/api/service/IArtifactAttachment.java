package com.servicemesh.agility.api.service;

import java.io.InputStream;

import com.servicemesh.agility.api.Artifact;
import com.servicemesh.agility.api.ArtifactAttachment;
import com.servicemesh.agility.api.Task;

public interface IArtifactAttachment
{

    public Task push(Artifact artifact, ArtifactAttachment attachment, InputStream is) throws Exception;
}
