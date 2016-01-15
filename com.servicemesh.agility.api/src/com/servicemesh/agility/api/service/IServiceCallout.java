package com.servicemesh.agility.api.service;

public interface IServiceCallout<T>
{

    /**
     * Calls the pre delete aspects
     *
     * @param asset
     *            Asset for which the aspects will execute
     * @throws Exception
     */
    public void preDelete(T asset) throws Exception;

    /**
     * Calls the post delete aspects
     * 
     * @param asset
     *            Asset for which the aspects will execute
     * @throws Exception
     */
    public void postDelete(T asset) throws Exception;

    /**
     * Calls the post soft delete aspects
     * 
     * @param asset
     *            Asset for which the aspects will execute
     * @throws Exception
     */
    public void postSoftDelete(T asset) throws Exception;

}
