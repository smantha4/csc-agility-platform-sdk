package com.servicemesh.agility.distributed.impl;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.servicemesh.agility.distributed.node.DistributedNode;
import com.servicemesh.agility.distributed.node.IDistributedListener;

public class DistributedListenerTracker implements ServiceTrackerCustomizer
{

    private static final Logger logger = Logger.getLogger(DistributedListenerTracker.class);
    private BundleContext _context;

    public DistributedListenerTracker(BundleContext context)
    {
        _context = context;
    }

    @Override
    public Object addingService(ServiceReference ref)
    {
        IDistributedListener service = (IDistributedListener) _context.getService(ref);
        try
        {
            if (DistributedNode.getLeaderID() != null)
            {
                service.nodesChanged(DistributedNode.getLeaderID(), DistributedNode.getInstances());
            }
        }
        catch (Throwable t)
        {
            logger.error(t);
        }
        return service;
    }

    @Override
    public void modifiedService(ServiceReference arg0, Object arg1)
    {
    }

    @Override
    public void removedService(ServiceReference arg0, Object arg1)
    {
    }

}
