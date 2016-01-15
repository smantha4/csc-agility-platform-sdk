package com.servicemesh.agility.distributed.node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.servicemesh.agility.distributed.sync.DistributedConfig;
import com.servicemesh.agility.distributed.sync.UIDGenerator;

public class DistributedNode
{

    final public static String ZKPATH = "/agility/node";

    private static String _nodeID = UIDGenerator.generateUID();
    private static String _leaderID;

    public static String getID()
    {
        return _nodeID;
    }

    public static String getLeaderID()
    {
        return _leaderID;
    }

    public static void setLeaderID(String leaderID)
    {
        _leaderID = leaderID;
    }

    public static boolean isLeader()
    {
        return (_leaderID != null && _nodeID.equals(_leaderID));
    }

    public static Set<String> getInstances() throws Exception
    {
        List<String> paths = DistributedConfig.getChildren(ZKPATH);
        Set<String> nodes = new HashSet<String>();
        for (String path : paths)
        {
            nodes.add(getNodeID(path));
        }
        return nodes;
    }

    public static String getNodeID(String path)
    {
        int index = path.lastIndexOf("/");
        String nodeID = path.substring(index + 1);
        index = nodeID.lastIndexOf("-");
        return nodeID.substring(0, index);
    }

}
