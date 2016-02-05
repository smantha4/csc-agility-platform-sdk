package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.EULA;

public interface IEULA
{
    EULA get() throws Exception;

    EULA accept(EULA asset, Asset parent, Context context) throws Exception;
}
