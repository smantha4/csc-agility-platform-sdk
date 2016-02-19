package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Assetlist;

public interface IAssetTypeSearch
{
    public Assetlist getSubset(int startTypeId, boolean leavesOnly, Context context) throws Exception;
}
