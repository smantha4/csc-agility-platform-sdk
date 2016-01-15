package com.servicemesh.agility.distributed.node;

import java.util.Set;

public interface IDistributedListener
{
    public void nodesChanged(String leader, Set<String> activeNodes);
}
