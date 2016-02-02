package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.InputVariable;

public interface IVariableValues<T>
{

    public void getVariableValues(T asset, List<InputVariable> variables) throws Exception;
}
