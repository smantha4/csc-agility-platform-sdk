package com.servicemesh.agility.sdk.cloud.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.servicemesh.agility.sdk.cloud.msgs.RegistrationResponse;
import com.servicemesh.agility.sdk.cloud.spi.CloudAdapter;
import com.servicemesh.core.async.AsyncService;
import com.servicemesh.core.async.ResponseHandler;
import com.servicemesh.core.messaging.Request;

public class AsyncTracker implements ServiceTrackerCustomizer
{

    private CloudAdapter _adapter;
    private BundleContext _context;
    private AsyncService _cloudService;
    private String _version;

    public AsyncTracker(CloudAdapter adapter, BundleContext context, String version)
    {
        _adapter = adapter;
        _context = context;
        _version = version;
    }

    public AsyncService getCloudService()
    {
        return _cloudService;
    }

    @Override
    public Object addingService(ServiceReference ref)
    {
        AsyncService service = (AsyncService) _context.getService(ref);
        Object cloudType = ref.getProperty("cloudType");
        Object version = ref.getProperty("version");
        if (cloudType != null && cloudType.equals("sdk") && version != null && version.toString().equals(_version))
        {
            _cloudService = service;
            _cloudService.sendRequest(_adapter.getRegistrationRequest(), new ResponseHandler<RegistrationResponse>() {

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
