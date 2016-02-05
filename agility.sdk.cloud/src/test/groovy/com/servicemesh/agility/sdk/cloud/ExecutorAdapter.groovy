import com.servicemesh.agility.sdk.cloud.spi.*;
import com.servicemesh.agility.sdk.cloud.msgs.*;
import com.servicemesh.core.async.AsyncService;
import com.servicemesh.core.async.RequestHandler;
import com.servicemesh.core.async.ResponseHandler;
import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerReactor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


abstract class ExecutorAdapter extends CloudAdapter {

   public ExecutorAdapter() 
   {
      super(new AsyncService(TimerReactor.getTimerReactor("Mock")), Executors.newCachedThreadPool(new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "mock");
		}
       }));
   }

   public void sendRequest(Request request, ResponseHandler<Response> handler)
   {
      _service.sendRequest(request,handler);
   }

   public String getCloudType() { return "mock"; }
   public String getAdapterName() { return "mock"; }
   public String getAdapterVersion() { return "1.0"; }

}
