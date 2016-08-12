import com.servicemesh.agility.sdk.cloud.spi.CloudAdapter;
import com.servicemesh.agility.sdk.cloud.spi.IStorage;
import com.servicemesh.agility.sdk.cloud.msgs.StorageCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageAttachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageDetachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageResponse;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.QueueingHandler;


class StorageOperationsTest extends spock.lang.Specification
{
  def "dispatch IStorage.create request"()
  {
    StorageCreateRequest request = new StorageCreateRequest();
    StorageResponse response = new StorageResponse();
    QueueingHandler<StorageResponse> qhandler = new QueueingHandler<StorageResponse>()
    IStorage ops = Mock(IStorage);
    MockAdapter adapter  = [ getStorageOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.create(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IStorage.delete request"()
  {
    StorageDeleteRequest request = new StorageDeleteRequest();
    StorageResponse response = new StorageResponse();
    QueueingHandler<StorageResponse> qhandler = new QueueingHandler<StorageResponse>()
    IStorage ops = Mock(IStorage);
    MockAdapter adapter  = [ getStorageOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.delete(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IStorage.attach request"()
  {
    StorageAttachRequest request = new StorageAttachRequest();
    StorageResponse response = new StorageResponse();
    QueueingHandler<StorageResponse> qhandler = new QueueingHandler<StorageResponse>()
    IStorage ops = Mock(IStorage);
    MockAdapter adapter  = [ getStorageOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.attach(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IStorage.detach request"()
  {
    StorageDetachRequest request = new StorageDetachRequest();
    StorageResponse response = new StorageResponse();
    QueueingHandler<StorageResponse> qhandler = new QueueingHandler<StorageResponse>()
    IStorage ops = Mock(IStorage);
    MockAdapter adapter  = [ getStorageOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.detach(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

}
