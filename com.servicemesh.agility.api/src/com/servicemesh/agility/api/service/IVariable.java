package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.InputVariableList;
import com.servicemesh.agility.api.InputVariableRequest;
import com.servicemesh.agility.api.Variable;

public interface IVariable
{

    public InputVariableList getInputVariables(InputVariableRequest inputVariableRequest) throws Exception;

    public InputVariableList UpdateEvent(InputVariableRequest inputVariableRequest) throws Exception;

    public Variable convertAssetProperty(com.servicemesh.agility.api.AssetProperty property) throws Exception;
}
