package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Router;
import com.servicemesh.agility.api.RouterInterface;
import com.servicemesh.agility.api.Task;

public interface IRouter
{

    public RouterInterface addRouterInterface(RouterInterface rinterface, Router router) throws Exception;

    public Task removeRouterInterface(RouterInterface rinterface, Router router) throws Exception;

    /**
     * Delete Router with option to release its resources
     * 
     * @param asset
     *            The router to be deleted
     * @param parent
     *            The containing asset
     * @param release
     *            if true, release router resources
     * @return A task handle that can be polled for task completion.
     * @throws Exception
     */
    public Task delete(Router asset, Asset parent, boolean release) throws Exception;
}
