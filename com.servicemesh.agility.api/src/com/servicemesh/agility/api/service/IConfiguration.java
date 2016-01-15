package com.servicemesh.agility.api.service;

public interface IConfiguration<T, V>
{
    // base operations supported by all assets
    T updateConfiguration(T asset, V configuration) throws Exception;
}
