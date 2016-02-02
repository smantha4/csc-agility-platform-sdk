package com.servicemesh.agility.distributed.impl;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.servicemesh.agility.distributed.node.DistributedNode;
import com.servicemesh.agility.distributed.node.IDistributedListener;
import com.servicemesh.agility.distributed.sync.DistributedConfig;

public class Activator implements BundleActivator
{

    private final static Logger logger = Logger.getLogger(Activator.class);
    private ServiceTracker _listenerTracker;

    public Activator()
    {

    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        try
        {
            _listenerTracker =
                    new ServiceTracker(context, IDistributedListener.class.getName(), new DistributedListenerTracker(context));
            _listenerTracker.open();

            String nodeID = DistributedNode.getID();
            DistributedConfig.create(DistributedNode.ZKPATH, CreateMode.PERSISTENT);
            DistributedConfig.getChildren(DistributedNode.ZKPATH, new DistributedNodeWatcher(context, _listenerTracker));
            DistributedConfig.create(DistributedNode.ZKPATH + "/" + nodeID + "-", CreateMode.EPHEMERAL_SEQUENTIAL);
        }
        catch (Throwable t)
        {
            logger.error(t);
        }
    }

    @Override
    public void stop(BundleContext arg0) throws Exception
    {

    }

}
