package com.servicemesh.agility.sdk.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.powermock.reflect.Whitebox;

import com.servicemesh.agility.sdk.service.spi.ServiceRegistry;
import com.servicemesh.core.async.AsyncService;

public class ServiceRegistryTest
{
    class ApiClient implements Runnable
    {
        private ServiceRegistry _registry;
        private AsyncService _apiService = null;

        public ApiClient(ServiceRegistry registry)
        {
            _registry = registry;
        }

        public ServiceRegistry getServiceRegistry()
        {
            return _registry;
        }

        public AsyncService getApiService()
        {
            return _apiService;
        }

        @Override
        public void run()
        {
            _apiService = _registry.lookupApiService();
        }
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testLookupApi() throws Exception
    {
        AsyncService mockAsync = mock(AsyncService.class);
        ServiceReference mockReference = mock(ServiceReference.class);
        BundleContext mockContext = mock(BundleContext.class);
        when(mockContext.getService(eq(mockReference))).thenReturn(mockAsync);

        final List<ServiceReference> references = new ArrayList<ServiceReference>();

        when(mockContext.getServiceReferences(anyString(), anyString())).thenAnswer(new Answer<ServiceReference[]>() {
            @Override
            public ServiceReference[] answer(InvocationOnMock invocation)
            {
                return references.toArray(new ServiceReference[0]);
            }
        });

        ServiceRegistry registry = new ServiceRegistry(mockContext);

        Object apiSvcHldrObj = Whitebox.getInternalState(registry, "_apiServiceHolder");
        Object apiSvcRgstrObj = Whitebox.getInternalState(registry, "_apiServiceRegistrar");
        Assert.assertNotNull(apiSvcHldrObj);
        Assert.assertNotNull(apiSvcRgstrObj);

        // Scenario 1: API service is immediately found
        references.add(mockReference);
        ApiClient apiClient = new ApiClient(registry);
        Thread t = new Thread(apiClient);
        t.start();
        verifyApiService(apiClient, t);

        // Scenario 2: Client blocks and API service registers later
        references.clear();
        Whitebox.setInternalState(apiSvcRgstrObj, "_registered", Boolean.FALSE);
        AsyncService asyncService = null;
        Whitebox.invokeMethod(apiSvcHldrObj, "setApiService", asyncService);

        apiClient = new ApiClient(registry);
        t = new Thread(apiClient);
        t.start();
        Thread.sleep(500);
        Assert.assertNull(apiClient.getApiService());

        ServiceEvent regEvent = new ServiceEvent(ServiceEvent.REGISTERED, mockReference);
        Assert.assertTrue((Boolean) Whitebox.getInternalState(apiSvcRgstrObj, "_registered"));

        Whitebox.invokeMethod(apiSvcHldrObj, "serviceChanged", regEvent);
        verifyApiService(apiClient, t);

        // Scenario 3 - API service unregisters and re-registers
        ServiceEvent unregEvent = new ServiceEvent(ServiceEvent.UNREGISTERING, mockReference);
        Whitebox.invokeMethod(apiSvcHldrObj, "serviceChanged", unregEvent);
        asyncService = (AsyncService) Whitebox.invokeMethod(apiSvcHldrObj, "getApiService");
        Assert.assertNull(asyncService);

        apiClient = new ApiClient(registry);
        t = new Thread(apiClient);
        t.start();
        Thread.sleep(500);
        Assert.assertNull(apiClient.getApiService());

        Whitebox.invokeMethod(apiSvcHldrObj, "serviceChanged", regEvent);
        verifyApiService(apiClient, t);
    }

    private void verifyApiService(ApiClient apiClient, Thread t) throws Exception
    {
        try
        {
            t.join(5000);
        }
        catch (Exception e)
        {
            Assert.fail("Exception: " + e);
        }
        Assert.assertFalse(t.isAlive());
        Assert.assertNotNull(apiClient.getApiService());
        Assert.assertNotNull(apiClient.getServiceRegistry().lookupApiService());
    }
}
