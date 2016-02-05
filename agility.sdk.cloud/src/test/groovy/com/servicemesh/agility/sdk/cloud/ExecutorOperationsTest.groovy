
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


class ExecutorOperationsTest extends spock.lang.Specification
{
  /*
  def "executor dispatch IInstance.boot request"()
  {
    InstanceBootRequest request = new InstanceBootRequest();
    InstanceResponse response = new InstanceResponse();
    QueueingHandler<InstanceResponse> qhandler = new QueueingHandler<InstanceResponse>()
    IInstance ops = Mock(IInstance);
    CloudAdapter adapter  = [ getInstanceOperations: { -> return ops; } ] as ExecutorAdapter;

    when:
      ops.boot(request,_) >> { args -> response.setMessage(Thread.currentThread().getName()); args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
      assert(response.getMessage().equals("mock"));
  }
  */
}
