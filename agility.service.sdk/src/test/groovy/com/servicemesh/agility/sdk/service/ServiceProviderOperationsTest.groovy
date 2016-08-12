
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.agility.sdk.service.spi.IServiceProvider;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderSyncRequest;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.Promise;


class ServiceProviderOperationsTest extends spock.lang.Specification
{
  def "dispatch IServiceProvider.preCreate request"()
  {
    ServiceProviderPreCreateRequest request = new ServiceProviderPreCreateRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceProvider ops = Mock(IServiceProvider);
    MockAdapter adapter  = [ getServiceProviderOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preCreate(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceProvider.postCreate request"()
  {
    ServiceProviderPostCreateRequest request = new ServiceProviderPostCreateRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceProvider ops = Mock(IServiceProvider);
    MockAdapter adapter  = [ getServiceProviderOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postCreate(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceProvider.preUpdate request"()
  {
    ServiceProviderPreUpdateRequest request = new ServiceProviderPreUpdateRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceProvider ops = Mock(IServiceProvider);
    MockAdapter adapter  = [ getServiceProviderOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preUpdate(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceProvider.postUpdate request"()
  {
    ServiceProviderPostUpdateRequest request = new ServiceProviderPostUpdateRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceProvider ops = Mock(IServiceProvider);
    MockAdapter adapter  = [ getServiceProviderOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postUpdate(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceProvider.preDelete request"()
  {
    ServiceProviderPreDeleteRequest request = new ServiceProviderPreDeleteRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceProvider ops = Mock(IServiceProvider);
    MockAdapter adapter  = [ getServiceProviderOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preDelete(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceProvider.postDelete request"()
  {
    ServiceProviderPostDeleteRequest request = new ServiceProviderPostDeleteRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceProvider ops = Mock(IServiceProvider);
    MockAdapter adapter  = [ getServiceProviderOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postDelete(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceProvider.sync request"()
  {
    ServiceProviderSyncRequest request = new ServiceProviderSyncRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceProvider ops = Mock(IServiceProvider);
    MockAdapter adapter  = [ getServiceProviderOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.sync(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

}
