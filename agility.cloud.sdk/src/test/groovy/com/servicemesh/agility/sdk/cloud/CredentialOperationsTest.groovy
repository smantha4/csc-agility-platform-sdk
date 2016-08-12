import com.servicemesh.agility.sdk.cloud.spi.CloudAdapter;
import com.servicemesh.agility.sdk.cloud.spi.ICredential;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CloudResponse;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.QueueingHandler;


class CredentialOperationsTest extends spock.lang.Specification
{
  def "dispatch ICredentials.create request"()
  {
    CredentialCreateRequest request = new CredentialCreateRequest();
    CredentialResponse response = new CredentialResponse();
    QueueingHandler<CredentialResponse> qhandler = new QueueingHandler<CredentialResponse>()
    ICredential ops = Mock(ICredential);
    MockAdapter adapter  = [ getCredentialOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.create(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch ICredentials.delete request"()
  {
    CredentialDeleteRequest request = new CredentialDeleteRequest();
    CloudResponse response = new CloudResponse();
    QueueingHandler<CloudResponse> qhandler = new QueueingHandler<CloudResponse>()
    ICredential ops = Mock(ICredential);
    MockAdapter adapter  = [ getCredentialOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.delete(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

}
