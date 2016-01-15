package com.servicemesh.agility.distributed.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.servicemesh.agility.distributed.node.DistributedNode;
import com.servicemesh.agility.distributed.node.IDistributedListener;
import com.servicemesh.agility.distributed.sync.DistributedConfig;
import com.servicemesh.agility.distributed.sync.ZNodeName;

public class DistributedNodeWatcher implements Watcher
{

    private final static Logger logger = Logger.getLogger(DistributedNodeWatcher.class);
    private BundleContext _context;
    private ServiceTracker _listeners;

    public DistributedNodeWatcher(BundleContext context, ServiceTracker listeners)
    {
        _context = context;
        _listeners = listeners;
    }

    @Override
    public void process(WatchedEvent event)
    {
        try
        {
            List<String> nodes = DistributedConfig.getChildren(DistributedNode.ZKPATH, this);
            SortedSet<ZNodeName> sorted = new TreeSet<ZNodeName>();
            for (String node : nodes)
            {
                sorted.add(new ZNodeName(node));
            }

            StringBuilder msg = new StringBuilder("active distributed nodes: ");
            Set<String> uuids = new HashSet<String>();
            for (ZNodeName name : sorted)
            {
                uuids.add(name.getPrefix());
                msg.append(name.getPrefix());
                msg.append(" ");
            }
            logger.debug(msg.toString());

            String newLeaderID = sorted.first().getPrefix();
            String oldLeaderID = DistributedNode.getLeaderID();
            boolean newLeader = false;

            if (oldLeaderID == null || oldLeaderID.equals(newLeaderID) == false)
            {
                logger.debug("this node elected as leader: " + newLeaderID);
                DistributedNode.setLeaderID(newLeaderID);
                newLeader = true;
            }

            ServiceReference[] services = _listeners.getServiceReferences();
            if (services != null)
            {
                boolean isSelf = newLeaderID.equals(DistributedNode.getID());
                if (newLeader)
                {
                    for (ServiceReference sref : services)
                    {
                        IDistributedListener listener = (IDistributedListener) _context.getService(sref);
                        logger.debug("distributing listener node changed\n");
                        listener.nodesChanged(newLeaderID, uuids);
                        _context.ungetService(sref);
                    }
                }
            }
        }
        catch (Throwable t)
        {
            logger.error(t);
        }
    }

}
