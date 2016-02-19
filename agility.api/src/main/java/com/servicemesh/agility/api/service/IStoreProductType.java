package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.StoreProductAdapterItemList;

public interface IStoreProductType
{
    /**
     * Get the list of items for the product type.
     * 
     * @param productType
     *            Name of the product type.
     * @return List of StoreProductAdapterItems for the product type.
     * @throws Exception
     */
    public StoreProductAdapterItemList getItemsByType(String productType) throws Exception;
}
