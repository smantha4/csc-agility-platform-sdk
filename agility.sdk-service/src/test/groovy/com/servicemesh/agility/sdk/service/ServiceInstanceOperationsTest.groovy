
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.agility.sdk.service.spi.IServiceInstance;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceValidateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.Promise;


class ServiceInstanceOperationsTest extends spock.lang.Specification
{
  def "dispatch IServiceInstance.validate request"()
  {
    ServiceInstanceValidateRequest request = new ServiceInstanceValidateRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceInstance ops = Mock(IServiceInstance);
    MockAdapter adapter  = [ getServiceInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.validate(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceInstance.provision request"()
  {
    ServiceInstanceProvisionRequest request = new ServiceInstanceProvisionRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceInstance ops = Mock(IServiceInstance);
    MockAdapter adapter  = [ getServiceInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.provision(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceInstance.start request"()
  {
    ServiceInstanceStartRequest request = new ServiceInstanceStartRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceInstance ops = Mock(IServiceInstance);
    MockAdapter adapter  = [ getServiceInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.start(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceInstance.stop request"()
  {
    ServiceInstanceStopRequest request = new ServiceInstanceStopRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceInstance ops = Mock(IServiceInstance);
    MockAdapter adapter  = [ getServiceInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.stop(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IServiceInstance.release request"()
  {
    ServiceInstanceReleaseRequest request = new ServiceInstanceReleaseRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IServiceInstance ops = Mock(IServiceInstance);
    MockAdapter adapter  = [ getServiceInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.release(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }
}
