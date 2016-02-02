package com.servicemesh.agility.api.service;

import java.io.InputStream;

import com.servicemesh.agility.api.Attachment;
import com.servicemesh.agility.api.Script;

public interface IAttachment
{

    public void push(Script script, Attachment attachment, InputStream is);
}
