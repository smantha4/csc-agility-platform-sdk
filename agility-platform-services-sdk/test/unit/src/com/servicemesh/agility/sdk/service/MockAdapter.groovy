import com.servicemesh.agility.sdk.service.spi.*;
import com.servicemesh.agility.sdk.service.msgs.*;
import com.servicemesh.core.async.AsyncService;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.async.RequestHandler;
import com.servicemesh.core.async.ResponseHandler;
import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;



abstract class MockAdapter extends ServiceAdapter {

   public MockAdapter() 
   {
      super(new AsyncService(TimerReactor.getTimerReactor("Mock")));
   }

   public void sendRequest(Request request, ResponseHandler<Response> handler)
   {
      _service.sendRequest(request,handler);
   }

   public Promise<Response> promise(Request request)
   {
      return _service.promise(request);
   }

   public String getCloudType() { return "mock"; }
   public String getAdapterName() { return "mock"; }
   public String getAdapterVersion() { return "1.0"; }

}
