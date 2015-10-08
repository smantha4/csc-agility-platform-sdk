
import org.apache.zookeeper.CreateMode;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import com.servicemesh.agility.distributed.impl.DistributedNodeWatcher;
import com.servicemesh.agility.distributed.node.IDistributedListener;
import com.servicemesh.agility.distributed.node.DistributedNode;
import com.servicemesh.agility.distributed.sync.DistributedConfig;
import com.servicemesh.agility.distributed.sync.ZNodeName;
import com.servicemesh.agility.distributed.sync.UIDGenerator;

class NodeWatcherSpec extends spock.lang.Specification
{
  def "Validate listener callouts"()
  {
    IDistributedListener listener = Mock(IDistributedListener);

    BundleContext context = Mock(BundleContext);
    ServiceReference[] srefs = new ServiceReference[1];
    ServiceReference sref = Mock(ServiceReference);
    srefs[0] = sref;
    ServiceTracker tracker = Mock(ServiceTracker);

    String nodeID = DistributedNode.getID();
    String newNodeID = UIDGenerator.generateUID();
    DistributedNodeWatcher watcher = new DistributedNodeWatcher(context,tracker);
    Object barrier = new Object();
    
    tracker.getServiceReferences() >> srefs;
    context.getService(_) >> listener;

    // initial node add - should be elected as leader
    when:
      DistributedConfig.create(DistributedNode.ZKPATH, CreateMode.PERSISTENT);
      DistributedConfig.getChildren(DistributedNode.ZKPATH, new DistributedNodeWatcher(context,tracker));
      DistributedConfig.create(DistributedNode.ZKPATH + "/"+nodeID+"-", CreateMode.EPHEMERAL_SEQUENTIAL);
      synchronized(barrier) { barrier.wait(1000); } // wait for zookeeper callback

    then:
      1 * listener.nodesChanged(nodeID,_) >> { synchronized(barrier) { barrier.notifyAll(); } }
      DistributedNode.isLeader() == true;

    // new node added - should not change current leader 
    when:
      DistributedConfig.create(DistributedNode.ZKPATH + "/"+newNodeID+"-", CreateMode.EPHEMERAL_SEQUENTIAL);
      synchronized(barrier) { barrier.wait(1000); } // wait for zookeeper callback

    then:
      DistributedNode.isLeader() == true;
      0 * listener.nodesChanged(_,_);

    // original node dies - new node should be elected
    when:
      List<String> children = DistributedConfig.getChildren(DistributedNode.ZKPATH, null);
      SortedSet<ZNodeName> names = new TreeSet<ZNodeName>();
      for(String child : children)
          names.add(new ZNodeName(child));
      DistributedConfig.delete(DistributedNode.ZKPATH+"/"+names.first().getName());
      synchronized(barrier) { barrier.wait(1000); } // wait for zookeeper callback

    then:
      1 * listener.nodesChanged(newNodeID,_) >> { synchronized(barrier) { barrier.notifyAll(); } }
      DistributedNode.isLeader() == false;
  }
}

