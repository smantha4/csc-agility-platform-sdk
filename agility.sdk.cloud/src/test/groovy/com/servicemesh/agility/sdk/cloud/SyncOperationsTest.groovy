import com.servicemesh.agility.sdk.cloud.spi.CloudAdapter;
import com.servicemesh.agility.sdk.cloud.spi.ISync;
import com.servicemesh.agility.sdk.cloud.msgs.AddressRangeSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.AddressRangeSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CloudSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CloudSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.ImageSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ImageSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.LocationSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.LocationSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.ModelSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ModelSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.NetworkSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.NetworkSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.RegistrationRequest;
import com.servicemesh.agility.sdk.cloud.msgs.RegistrationResponse;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSyncResponse;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.QueueingHandler;


class SyncOperationsTest extends spock.lang.Specification
{
  def "dispatch ISync<AddressRangeSyncRequest,AddressRangeSyncResponse>.sync request"()
  {
    AddressRangeSyncRequest request = new AddressRangeSyncRequest();
    AddressRangeSyncResponse response = new AddressRangeSyncResponse();
    QueueingHandler<AddressRangeSyncResponse> qhandler = new QueueingHandler<AddressRangeSyncResponse>()
    ISync ops = Mock(ISync);
    MockAdapter adapter  = [ getAddressRangeSync: { -> return ops; } ] as MockAdapter;

    when:
      ops.sync(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch ISync<CloudSyncRequest,CloudSyncResponse>.sync request"()
  {
    CloudSyncRequest request = new CloudSyncRequest();
    CloudSyncResponse response = new CloudSyncResponse();
    QueueingHandler<CloudSyncResponse> qhandler = new QueueingHandler<CloudSyncResponse>()
    ISync ops = Mock(ISync);
    MockAdapter adapter  = [ getCloudSync: { -> return ops; } ] as MockAdapter;

    when:
      ops.sync(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch ISync<CredentialSyncRequest,CredentialSyncResponse>.sync request"()
  {
    CredentialSyncRequest request = new CredentialSyncRequest();
    CredentialSyncResponse response = new CredentialSyncResponse();
    QueueingHandler<CredentialSyncResponse> qhandler = new QueueingHandler<CredentialSyncResponse>()
    ISync ops = Mock(ISync);
    MockAdapter adapter  = [ getCredentialSync: { -> return ops; } ] as MockAdapter;

    when:
      ops.sync(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch ISync<ImageSyncRequest,ImageSyncResponse>.sync request"()
  {
    ImageSyncRequest request = new ImageSyncRequest();
    ImageSyncResponse response = new ImageSyncResponse();
    QueueingHandler<ImageSyncResponse> qhandler = new QueueingHandler<ImageSyncResponse>()
    ISync ops = Mock(ISync);
    MockAdapter adapter  = [ getImageSync: { -> return ops; } ] as MockAdapter;

    when:
      ops.sync(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch ISync<InstanceSyncRequest,InstanceSyncResponse>.sync request"()
  {
    InstanceSyncRequest request = new InstanceSyncRequest();
    InstanceSyncResponse response = new InstanceSyncResponse();
    QueueingHandler<InstanceSyncResponse> qhandler = new QueueingHandler<InstanceSyncResponse>()
    ISync ops = Mock(ISync);
    MockAdapter adapter  = [ getInstanceSync: { -> return ops; } ] as MockAdapter;

    when:
      ops.sync(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch ISync<LocationSyncRequest,LocationSyncResponse>.sync request"()
  {
    LocationSyncRequest request = new LocationSyncRequest();
    LocationSyncResponse response = new LocationSyncResponse();
    QueueingHandler<LocationSyncResponse> qhandler = new QueueingHandler<LocationSyncResponse>()
    ISync ops = Mock(ISync);
    MockAdapter adapter  = [ getLocationSync: { -> return ops; } ] as MockAdapter;

    when:
      ops.sync(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch ISync<ModelSyncRequest,ModelSyncResponse>.sync request"()
  {
    ModelSyncRequest request = new ModelSyncRequest();
    ModelSyncResponse response = new ModelSyncResponse();
    QueueingHandler<ModelSyncResponse> qhandler = new QueueingHandler<ModelSyncResponse>()
    ISync ops = Mock(ISync);
    MockAdapter adapter  = [ getModelSync: { -> return ops; } ] as MockAdapter;

    when:
      ops.sync(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch ISync<NetworkSyncRequest,NetworkSyncResponse>.sync request"()
  {
    NetworkSyncRequest request = new NetworkSyncRequest();
    NetworkSyncResponse response = new NetworkSyncResponse();
    QueueingHandler<NetworkSyncResponse> qhandler = new QueueingHandler<NetworkSyncResponse>()
    ISync ops = Mock(ISync);
    MockAdapter adapter  = [ getNetworkSync: { -> return ops; } ] as MockAdapter;

    when:
      ops.sync(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch ISync<StorageSyncRequest,StorageSyncResponse>.sync request"()
  {
    StorageSyncRequest request = new StorageSyncRequest();
    StorageSyncResponse response = new StorageSyncResponse();
    QueueingHandler<StorageSyncResponse> qhandler = new QueueingHandler<StorageSyncResponse>()
    ISync ops = Mock(ISync);
    MockAdapter adapter  = [ getStorageSync: { -> return ops; } ] as MockAdapter;

    when:
      ops.sync(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }
}
