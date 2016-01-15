package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Authentication;

public interface IAuthentication
{

    public List<Authentication> saveAuthenticationOrder(List<Authentication> auths) throws Exception;

}
