package com.servicemesh.agility.api.service;

import java.util.List;
import java.util.Set;

import com.servicemesh.agility.api.InputVariable;

public interface IInputVariables<T>
{

    public void getVariables(T asset, Set<String> visited, List<InputVariable> variables) throws Exception;
}
