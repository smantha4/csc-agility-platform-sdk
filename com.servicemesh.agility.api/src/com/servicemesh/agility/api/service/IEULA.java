package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Assetlist;
import com.servicemesh.agility.api.Envelope;
import com.servicemesh.agility.api.Link;
import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.EULA;

public interface IEULA
{
	EULA get() throws Exception;
	EULA accept(EULA asset, Asset parent, Context context) throws Exception;
}

