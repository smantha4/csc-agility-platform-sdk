package com.servicemesh.agility.api.service;

import java.util.List;
import java.util.Set;

import com.servicemesh.agility.api.InputVariable;
import com.servicemesh.agility.api.Package;

public interface IPackageScriptVariables
{

    public void getPackageScriptVariables(Package pkg, Set<String> visited, List<InputVariable> variables,
            boolean excludePackageScripts) throws Exception;

}
