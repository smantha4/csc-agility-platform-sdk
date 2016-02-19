import com.servicemesh.agility.sdk.cloud.spi.CloudAdapter;
import com.servicemesh.agility.sdk.cloud.spi.IStorage;
import com.servicemesh.agility.sdk.cloud.spi.IStorageSnapshot;
import com.servicemesh.agility.sdk.cloud.msgs.StorageCreateFromSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageResponse;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSnapshotCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSnapshotDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSnapshotResponse;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.QueueingHandler;


class StorageSnapshotOperationsTest extends spock.lang.Specification
{
  def "dispatch IStorageSnapshot.create request"()
  {
    StorageSnapshotCreateRequest request = new StorageSnapshotCreateRequest();
    StorageSnapshotResponse response = new StorageSnapshotResponse();
    QueueingHandler<StorageSnapshotResponse> qhandler = new QueueingHandler<StorageSnapshotResponse>()
    IStorageSnapshot ops = Mock(IStorageSnapshot);
    MockAdapter adapter  = [ getSnapshotOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.create(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IStorageSnapshot.delete request"()
  {
    StorageSnapshotDeleteRequest request = new StorageSnapshotDeleteRequest();
    StorageSnapshotResponse response = new StorageSnapshotResponse();
    QueueingHandler<StorageSnapshotResponse> qhandler = new QueueingHandler<StorageSnapshotResponse>()
    IStorageSnapshot ops = Mock(IStorageSnapshot);
    MockAdapter adapter  = [ getSnapshotOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.delete(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IStorageSnapshot.createFromSnapshot request"()
  {
    StorageCreateFromSnapshotRequest request = new StorageCreateFromSnapshotRequest();
    StorageResponse response = new StorageResponse();
    QueueingHandler<StorageResponse> qhandler = new QueueingHandler<StorageResponse>()
    IStorageSnapshot ops = Mock(IStorageSnapshot);
    MockAdapter adapter  = [ getSnapshotOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.createFromSnapshot(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }
}
