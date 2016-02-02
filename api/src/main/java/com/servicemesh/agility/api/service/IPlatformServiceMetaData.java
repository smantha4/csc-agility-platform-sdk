package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.ArtifactType;
import com.servicemesh.agility.api.ServiceBindingType;

/**
 * Exposes additional operations to get artifact type(s) and service type(s) for a Platform Service
 */
public interface IPlatformServiceMetaData
{

    /*********************************************************************************
     * List of component types supported: war, rpm, ddl and so on...
     * 
     * @throws Exception
     */
    public List<ArtifactType> getSupportedArtifactTypes(int platformServiceId) throws Exception;

    /*********************************************************************************
     * List of service types supported: mysql, oracle, rabbitmq and so on...
     * 
     * @throws Exception
     */
    public List<ServiceBindingType> getSupportedServiceTypes(int platformServiceId) throws Exception;

}
