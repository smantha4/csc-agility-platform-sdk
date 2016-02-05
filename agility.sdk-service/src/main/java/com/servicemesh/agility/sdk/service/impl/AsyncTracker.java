package com.servicemesh.agility.sdk.service.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.servicemesh.agility.sdk.service.msgs.RegistrationRequest;
import com.servicemesh.agility.sdk.service.msgs.RegistrationResponse;
import com.servicemesh.agility.sdk.service.spi.IAddressManagement;
import com.servicemesh.agility.sdk.service.spi.IInstanceLifecycle;
import com.servicemesh.agility.sdk.service.spi.IServiceInstance;
import com.servicemesh.agility.sdk.service.spi.IServiceProvider;
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.core.async.AsyncService;
import com.servicemesh.core.async.ResponseHandler;
import com.servicemesh.core.messaging.Request;

public class AsyncTracker implements ServiceTrackerCustomizer
{

    private ServiceAdapter _adapter;
    private BundleContext _context;
    private AsyncService _serviceProvider;
    private String _version;

    public AsyncTracker(ServiceAdapter adapter, BundleContext context, String version)
    {
        _adapter = adapter;
        _context = context;
        _version = version;
    }

    public AsyncService getCloudService()
    {
        return _serviceProvider;
    }

    @Override
    public Object addingService(ServiceReference ref)
    {
        AsyncService service = (AsyncService) _context.getService(ref);
        Object serviceType = ref.getProperty("serviceType");
        Object serviceProviderType = ref.getProperty("serviceProviderType");
        Object version = ref.getProperty("version");
        if (serviceType != null && serviceType.equals("service") && serviceProviderType != null
                && serviceProviderType.equals("framework") && version != null && version.toString().equals(_version))
        {
            _serviceProvider = service;
            RegistrationRequest request = _adapter.getRegistrationRequest();
            if (_adapter.getInstanceOperations() != null)
            {
                request.getSupportedInterfaces().add(IInstanceLifecycle.class.getName());
            }
            if (_adapter.getServiceProviderOperations() != null)
            {
                request.getSupportedInterfaces().add(IServiceProvider.class.getName());
            }
            if (_adapter.getServiceInstanceOperations() != null)
            {
                request.getSupportedInterfaces().add(IServiceInstance.class.getName());
            }
            if (_adapter.getAddressManagementOperations() != null)
            {
                request.getSupportedInterfaces().add(IAddressManagement.class.getName());
            }
            _serviceProvider.sendRequest(request, new ResponseHandler<RegistrationResponse>() {

                @Override
                public boolean onResponse(RegistrationResponse response)
                {
                    _adapter.onRegistration(response);
                    return false;
                }

                @Override
                public void onError(Request request, Throwable t)
                {

                }
            });
        }
        return service;
    }

    @Override
    public void modifiedService(ServiceReference arg0, Object arg1)
    {
    }

    @Override
    public void removedService(ServiceReference arg0, Object arg1)
    {
    }

}
