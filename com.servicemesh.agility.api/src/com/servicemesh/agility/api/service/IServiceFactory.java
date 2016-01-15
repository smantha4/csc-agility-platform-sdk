package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Container;
import com.servicemesh.agility.api.Link;

public interface IServiceFactory
{
    public <T> IService<T> get(String className) throws Exception;

    public <T> IService<T> get(Class<T> tClass) throws Exception;

    public <T> Class<T> getJaxbClass(String assetTypeName) throws Exception;

    public <T> Class<T> getModelClass(String apiClassName) throws Exception;

    public <T> T resolve(Link link) throws Exception;

    public <T> List<T> resolve(String className, List<Link> links) throws Exception;

    public <T> List<T> resolve(Class<T> tClass, List<Link> links) throws Exception;

    public <T> List<T> lookup(Class<T> tClass, Container root, String[] path) throws Exception;

    public <T> void refresh(Class<T> tClass, int id) throws Exception;
}
