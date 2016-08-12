
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.agility.sdk.service.spi.IInstanceLifecycle;
import com.servicemesh.agility.sdk.service.msgs.InstancePreBootRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostBootRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreStartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostStartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreStopRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostStopRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.Promise;


class InstanceOperationsTest extends spock.lang.Specification
{
  def "dispatch IInstanceLifecycle.preProvision request"()
  {
    InstancePreProvisionRequest request = new InstancePreProvisionRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preProvision(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.postProvision request"()
  {
    InstancePostProvisionRequest request = new InstancePostProvisionRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postProvision(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.preBoot request"()
  {
    InstancePreBootRequest request = new InstancePreBootRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preBoot(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.postBoot request"()
  {
    InstancePostBootRequest request = new InstancePostBootRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postBoot(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.preStart request"()
  {
    InstancePreStartRequest request = new InstancePreStartRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preStart(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.postStart request"()
  {
    InstancePostStartRequest request = new InstancePostStartRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postStart(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.preStop request"()
  {
    InstancePreStopRequest request = new InstancePreStopRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preStop(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.preRestart request"()
  {
    InstancePreRestartRequest request = new InstancePreRestartRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preRestart(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.postRestart request"()
  {
    InstancePostRestartRequest request = new InstancePostRestartRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postRestart(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.preRelease request"()
  {
    InstancePreReleaseRequest request = new InstancePreReleaseRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.preRelease(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }

  def "dispatch IInstanceLifecycle.postRelease request"()
  {
    InstancePostReleaseRequest request = new InstancePostReleaseRequest();
    ServiceProviderResponse response = new ServiceProviderResponse();
    IInstanceLifecycle ops = Mock(IInstanceLifecycle);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;
    Promise<ServiceProviderResponse> promise;

    when:
      ops.postRelease(request) >> { return Promise.pure(response); }
      promise = adapter.promise(request);

    then:
      response == promise.get()
  }
}
