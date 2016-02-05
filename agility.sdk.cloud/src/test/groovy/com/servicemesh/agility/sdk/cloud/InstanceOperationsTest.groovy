
import com.servicemesh.agility.sdk.cloud.spi.CloudAdapter;
import com.servicemesh.agility.sdk.cloud.spi.IInstance;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceBootRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceStartRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceStopRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceRebootRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceReleaseRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceResponse;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.QueueingHandler;


class InstanceOperationsTest extends spock.lang.Specification
{
  def "dispatch IInstance.boot request"()
  {
    InstanceBootRequest request = new InstanceBootRequest();
    InstanceResponse response = new InstanceResponse();
    QueueingHandler<InstanceResponse> qhandler = new QueueingHandler<InstanceResponse>()
    IInstance ops = Mock(IInstance);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.boot(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IInstance.create request"()
  {
    InstanceCreateRequest request = new InstanceCreateRequest();
    InstanceResponse response = new InstanceResponse();
    QueueingHandler<InstanceResponse> qhandler = new QueueingHandler<InstanceResponse>()
    IInstance ops = Mock(IInstance);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.create(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IInstance.start request"()
  {
    InstanceStartRequest request = new InstanceStartRequest();
    InstanceResponse response = new InstanceResponse();
    QueueingHandler<InstanceResponse> qhandler = new QueueingHandler<InstanceResponse>()
    IInstance ops = Mock(IInstance);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.start(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IInstance.stop request"()
  {
    InstanceStopRequest request = new InstanceStopRequest();
    InstanceResponse response = new InstanceResponse();
    QueueingHandler<InstanceResponse> qhandler = new QueueingHandler<InstanceResponse>()
    IInstance ops = Mock(IInstance);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.stop(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IInstance.reboot request"()
  {
    InstanceRebootRequest request = new InstanceRebootRequest();
    InstanceResponse response = new InstanceResponse();
    QueueingHandler<InstanceResponse> qhandler = new QueueingHandler<InstanceResponse>()
    IInstance ops = Mock(IInstance);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.reboot(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IInstance.release request"()
  {
    InstanceReleaseRequest request = new InstanceReleaseRequest();
    InstanceResponse response = new InstanceResponse();
    QueueingHandler<InstanceResponse> qhandler = new QueueingHandler<InstanceResponse>()
    IInstance ops = Mock(IInstance);
    MockAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.release(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }
}
