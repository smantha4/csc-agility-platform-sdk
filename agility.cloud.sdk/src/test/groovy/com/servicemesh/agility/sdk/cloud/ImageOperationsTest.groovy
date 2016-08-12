import com.servicemesh.agility.sdk.cloud.spi.CloudAdapter;
import com.servicemesh.agility.sdk.cloud.spi.IImage;
import com.servicemesh.agility.sdk.cloud.msgs.ImageCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ImageDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CloudResponse;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.async.QueueingHandler;


class ImageOperationsTest extends spock.lang.Specification
{
  def "dispatch IImages.create request"()
  {
    ImageCreateRequest request = new ImageCreateRequest();
    CloudResponse response = new CloudResponse();
    QueueingHandler<CloudResponse> qhandler = new QueueingHandler<CloudResponse>()
    IImage ops = Mock(IImage);
    MockAdapter adapter  = [ getImageOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.create(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

  def "dispatch IImages.delete request"()
  {
    ImageDeleteRequest request = new ImageDeleteRequest();
    CloudResponse response = new CloudResponse();
    QueueingHandler<CloudResponse> qhandler = new QueueingHandler<CloudResponse>()
    IImage ops = Mock(IImage);
    MockAdapter adapter  = [ getImageOperations: { -> return ops; } ] as MockAdapter;

    when:
      ops.delete(request,_) >> { args -> args[1].onResponse(response); }
      adapter.sendRequest(request,qhandler);

    then:
      response == qhandler.get()
  }

}
