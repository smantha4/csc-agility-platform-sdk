package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.ConfigurationRepository;
import com.servicemesh.agility.api.Task;

public interface IConfigurationRepository
{

    public Task sync(ConfigurationRepository repo) throws Exception;

}
