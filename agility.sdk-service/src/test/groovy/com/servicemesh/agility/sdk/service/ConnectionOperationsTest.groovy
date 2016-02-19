
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.agility.sdk.service.spi.IConnection;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.Promise;


class ConnectionOperationsTest extends spock.lang.Specification
{
  def "dispatch IConnection.preCreate request"()
  {
    ConnectionPreCreateRequest request = new ConnectionPreCreateRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IConnection ops = Mock(IConnection);
    MockAdapter adapter  = [ getConnectionOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preCreate(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IConnection.postCreate request"()
  {
    ConnectionPostCreateRequest request = new ConnectionPostCreateRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IConnection ops = Mock(IConnection);
    MockAdapter adapter  = [ getConnectionOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postCreate(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IConnection.preUpdate request"()
  {
    ConnectionPreUpdateRequest request = new ConnectionPreUpdateRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IConnection ops = Mock(IConnection);
    MockAdapter adapter  = [ getConnectionOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preUpdate(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IConnection.postUpdate request"()
  {
    ConnectionPostUpdateRequest request = new ConnectionPostUpdateRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IConnection ops = Mock(IConnection);
    MockAdapter adapter  = [ getConnectionOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postUpdate(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IConnection.preDelete request"()
  {
    ConnectionPreDeleteRequest request = new ConnectionPreDeleteRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IConnection ops = Mock(IConnection);
    MockAdapter adapter  = [ getConnectionOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preDelete(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IConnection.postDelete request"()
  {
    ConnectionPostDeleteRequest request = new ConnectionPostDeleteRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IConnection ops = Mock(IConnection);
    MockAdapter adapter  = [ getConnectionOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postDelete(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }
}
