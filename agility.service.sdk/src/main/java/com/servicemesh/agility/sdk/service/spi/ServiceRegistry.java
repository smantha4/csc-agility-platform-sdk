package com.servicemesh.agility.sdk.service.spi;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.servicemesh.agility.api.ServiceProvider;
import com.servicemesh.core.async.AsyncService;

public class ServiceRegistry
{
    private static final Logger _logger = Logger.getLogger(ServiceRegistry.class);

    private class ApiServiceHolder implements ServiceListener
    {
        private AsyncService _apiService = null;
        private final Object _lock = new Object();

        private ApiServiceHolder()
        {
        }

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void serviceChanged(ServiceEvent ev)
        {
            ServiceReference sr = ev.getServiceReference();

            switch (ev.getType())
            {
                case ServiceEvent.REGISTERED:
                    setApiService((AsyncService) _context.getService(sr));
                    _logger.debug("Registered ApiService");
                    break;

                case ServiceEvent.UNREGISTERING:
                    setApiService(null);
                    _logger.debug("Unregistered ApiService");
                    break;
            }
        }

        private void setApiService(AsyncService svc)
        {
            synchronized (_lock)
            {
                _apiService = svc;
                if (svc != null)
                {
                    _lock.notifyAll();
                }
            }
        }

        private AsyncService getApiService()
        {
            synchronized (_lock)
            {
                return _apiService;
            }
        }

        private AsyncService getApiServiceBlocking()
        {
            synchronized (_lock)
            {
                while (_apiService == null)
                {
                    try
                    {
                        _logger.debug("Waiting for ApiService");
                        _lock.wait();
                    }
                    catch (InterruptedException ex)
                    {
                    }
                }
                return _apiService;
            }
        }
    }

    private class ApiServiceRegistrar
    {
        private boolean _registered = false;
        private final Object _lock = new Object();

        private ApiServiceRegistrar()
        {
        }

        private boolean register(ServiceListener listener)
        {
            synchronized (_lock)
            {
                if (!_registered)
                {
                    StringBuilder filter = new StringBuilder();
                    filter.append("(&(serviceType=api)(version=");
                    filter.append(Version.API_VERSION);
                    filter.append("))");

                    try
                    {
                        // Start listener and check if api service is already
                        // available
                        _context.addServiceListener(listener, filter.toString());

                        @SuppressWarnings("rawtypes")
                        ServiceReference[] refs = _context.getServiceReferences(AsyncService.class.getName(), filter.toString());
                        if ((refs != null) && (refs.length > 0))
                        {
                            listener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, refs[0]));
                        }
                        _registered = true;
                    }
                    catch (InvalidSyntaxException ex)
                    {
                        _logger.error("Failed to register listener: " + ex);
                    }
                }
                return _registered;
            }
        }
    }

    private BundleContext _context = null;
    private ApiServiceHolder _apiServiceHolder;
    private ApiServiceRegistrar _apiServiceRegistrar;

    public ServiceRegistry(BundleContext context)
    {
        _context = context;
        // Not registering API service listener by default - it's only
        // needed if lookupApiService() is invoked
        _apiServiceHolder = new ApiServiceHolder();
        _apiServiceRegistrar = new ApiServiceRegistrar();
    }

    /**
     * Returns the AsyncService that can process com.servicemesh.agility.api Requests like CreateRequest and GetRequest. Blocks
     * until the service is available.
     */
    public AsyncService lookupApiService()
    {
        AsyncService apiService = _apiServiceHolder.getApiService();
        if (apiService == null)
        {
            if (_apiServiceRegistrar.register(_apiServiceHolder))
            {
                apiService = _apiServiceHolder.getApiServiceBlocking();
            }
        }
        return apiService;
    }

    /**
     * Returns the AsyncService for the service provider
     * 
     * @param provider
     *            A service provider
     */
    public AsyncService lookupServiceByProvider(ServiceProvider provider)
    {
        if (provider.getType() != null)
        {
            return lookupServiceByProviderType(provider.getType().getName());
        }
        return null;
    }

    /**
     * Returns the AsyncService for a service provider type
     * 
     * @param name
     *            The name attribute of a service provider type
     */
    public AsyncService lookupServiceByProviderType(String name)
    {
        StringBuilder filter = new StringBuilder();
        filter.append("(&(serviceType=service)(version=");
        filter.append(Version.SDK_VERSION);
        filter.append(")(serviceProviderType=");
        filter.append(name);
        filter.append("))");

        ServiceReference<?>[] references = null;
        try
        {
            references = _context.getServiceReferences(AsyncService.class.getName(), filter.toString());
        }
        catch (InvalidSyntaxException ex)
        {
        }

        return (references != null && references.length > 0) ? (AsyncService) _context.getService(references[0]) : null;
    }
}
