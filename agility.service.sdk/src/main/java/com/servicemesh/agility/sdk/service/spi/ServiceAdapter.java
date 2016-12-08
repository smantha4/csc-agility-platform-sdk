package com.servicemesh.agility.sdk.service.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.servicemesh.agility.api.ApiRequest;
import com.servicemesh.agility.api.ApiResponse;
import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Cloud;
import com.servicemesh.agility.api.CreateRequest;
import com.servicemesh.agility.api.Credential;
import com.servicemesh.agility.api.DeleteRequest;
import com.servicemesh.agility.api.GetRequest;
import com.servicemesh.agility.api.ListRequest;
import com.servicemesh.agility.api.Property;
import com.servicemesh.agility.api.ProxyUsage;
import com.servicemesh.agility.api.ServiceProviderType;
import com.servicemesh.agility.api.UpdateRequest;
import com.servicemesh.agility.sdk.service.impl.AsyncTracker;
import com.servicemesh.agility.sdk.service.msgs.AvailableAddressesRequest;
import com.servicemesh.agility.sdk.service.msgs.AvailableAddressesResponse;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ConnectionPreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostBootRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostStartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostStopRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreBootRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreStartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreStopRequest;
import com.servicemesh.agility.sdk.service.msgs.InstanceResponse;
import com.servicemesh.agility.sdk.service.msgs.MethodRequest;
import com.servicemesh.agility.sdk.service.msgs.MethodResponse;
import com.servicemesh.agility.sdk.service.msgs.MethodVariable;
import com.servicemesh.agility.sdk.service.msgs.PostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.PostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.PostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.PreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.PreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.PreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.PropertyTypeValueRequest;
import com.servicemesh.agility.sdk.service.msgs.PropertyTypeValueResponse;
import com.servicemesh.agility.sdk.service.msgs.RegistrationRequest;
import com.servicemesh.agility.sdk.service.msgs.RegistrationResponse;
import com.servicemesh.agility.sdk.service.msgs.ReleaseAddressRequest;
import com.servicemesh.agility.sdk.service.msgs.ReleaseAddressResponse;
import com.servicemesh.agility.sdk.service.msgs.ReserveAddressRequest;
import com.servicemesh.agility.sdk.service.msgs.ReserveAddressResponse;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePostStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstancePreStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceInstanceValidateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPingRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPostUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreCreateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreDeleteRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPreUpdateRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderStartRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderStopRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderSyncRequest;
import com.servicemesh.agility.sdk.service.msgs.UnreserveAddressRequest;
import com.servicemesh.agility.sdk.service.msgs.UnreserveAddressResponse;
import com.servicemesh.agility.sdk.service.operations.AssetOperations;
import com.servicemesh.core.async.AsyncService;
import com.servicemesh.core.async.Callback;
import com.servicemesh.core.async.Function;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.async.RequestHandler;
import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;
import com.servicemesh.core.messaging.Status;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.io.proxy.Proxy;
import com.servicemesh.io.proxy.ProxyType;

/**
 * The base class for a service adapter
 */
public abstract class ServiceAdapter implements BundleActivator
{

    private static final Logger logger = Logger.getLogger(ServiceAdapter.class);

    protected AsyncService _service;
    protected ServiceTracker _asyncTracker;
    protected BundleContext _context;
    private Map<String, IValueProvider> _providers = new HashMap<String, IValueProvider>();
    private Map<String, IMethod> _methods = new HashMap<String, IMethod>();
    private ServiceRegistry _registry;

    public ServiceAdapter(Reactor reactor)
    {
        try
        {
            _service = new AsyncService(reactor);
            register();
        }
        catch (Throwable t)
        {
            logger.error(t);
        }
    }

    public ServiceAdapter(AsyncService service)
    {
        _service = service;
        register();
    }

    /**
     * Gets the service provider type(s) supported by this adapter. Used to register the osgi service.
     * 
     * @return The service provider type(s) for OSGi registration.
     */
    public abstract List<ServiceProviderType> getServiceProviderTypes();

    /**
     * Required meta-data to describe capabilities of the adapter/service provider. The returned RegistrationRequest is sent to
     * the agility platform and the onRegistration method called with the response.
     * 
     * @return The completed registration request.
     */
    public abstract RegistrationRequest getRegistrationRequest();

    /**
     * Provides any information requested by the adapter during the registration request including the persistent identifier for
     * the service provider.
     * 
     * @param response
     */
    public abstract void onRegistration(RegistrationResponse response);

    /**
     * @return An optional interface that implements hooks in instance lifecycle for workloads dependent on the service.
     */
    public IInstanceLifecycle getInstanceOperations()
    {
        return null;
    }

    /**
     * @return An optional interface that implements hooks in service instance lifecycle for service instances dependent on the
     *         service.
     */
    public IServiceInstanceLifecycle getServiceInstanceLifecycleOperations()
    {
        return null;
    }

    /**
     * @return A required interface used to manage the service provider.
     */
    public abstract IServiceProvider getServiceProviderOperations();

    /**
     * @return A required interface to manage the lifecycle of bindings (service instances) to the service provider.
     */
    public abstract IServiceInstance getServiceInstanceOperations();

    /**
     * @return An optional interface to manage the lifecycle of connections to the service provider.
     */
    public IConnection getConnectionOperations()
    {
        return null;
    }

    /**
     * @return An optional interface to manage addresses
     */
    public IAddressManagement getAddressManagementOperations()
    {
        return null;
    }

    /**
     * @return An optional interface to receive lifecycle (CRUD) notifications for specific asset types. An adapter will send a
     *         list of asset types it's interested in receiving CRUD event messages for when it builds it's initial registration
     *         message.
     */
    public IAssetLifecycle getAssetNotificationOperations()
    {
        return new AssetOperations();
    }

    public static List<Proxy> getProxyConfig(ServiceProviderRequest request)
    {
        //  Get the MANAGER -> CLOUD proxies, if any:
        return getProxyConfig(request, ProxyUsage.PROXY_MANAGER_CLOUD);
    }

    public static List<Proxy> getProxyConfig(ServiceProviderRequest request, ProxyUsage usage)
    {

        //  Get the proxies specified by the usage type requested
        List<Proxy> proxies = new ArrayList<Proxy>();

        if (request.getClouds() == null || request.getClouds().isEmpty())
        {
            logger.warn("Clouds associated with the request are empty. Returning empty list of proxies");
            return proxies;
        }
        // Get the cloud from the  request
        Cloud cloud = request.getClouds().get(0);

        Proxy last = null;
        for (com.servicemesh.agility.api.Proxy proxy : cloud.getProxies())
        {
            if (proxy.getProxyUsage().equals(usage) || proxy.getProxyUsage().equals(ProxyUsage.PROXY_ALL))
            {
                String admin = null;
                String passwd = null;
                String hostname = proxy.getHostname();
                int port = proxy.getPort();
                ProxyType proxyType = convertProxyType(proxy);
                Credential proxyCreds = proxy.getCredentials();

                switch (proxy.getAuthType())
                {
                    case AUTH_NONE:
                        break;
                    case AUTH_CONFIGURED:
                        admin = proxyCreds.getPublicKey();
                        passwd = proxyCreds.getPrivateKey();
                        break;
                    case AUTH_SESSION:
                        break;
                    default:
                        throw new RuntimeException("Unsupported authentication type: " + proxy.getAuthType());
                }

                Proxy current = new Proxy(hostname, port, proxyType, null, admin, passwd);
                if (last != null)
                {
                    last.setTargetHost(current);
                }
                proxies.add(current);
                last = current;
            }
        }
        return proxies;
    }

    private static ProxyType convertProxyType(com.servicemesh.agility.api.Proxy proxy)
    {
        ProxyType proxyType;

        switch (proxy.getProxyType())
        {
            case PROXY_SOCKS_5:
                proxyType = ProxyType.SOCKS5_PROXY;
                break;
            case PROXY_HTTP:
                proxyType = ProxyType.HTTP_PROXY;
                break;
            case PROXY_HTTPS:
                proxyType = ProxyType.HTTPS_PROXY;
                break;
            default:
                throw new RuntimeException("Unsupported proxy type: " + proxy.getProxyType());
        }

        return proxyType;
    }

    /**
     * Returns the SDK's ServiceRegistry object for this bundle
     */
    public ServiceRegistry getServiceRegistry()
    {
        return _registry;
    }

    /*
     * Returns a promise to create an asset
     *
     * @param asset The asset to be created
     * @param parent The parent of the asset to be created
     * @return The created asset when the promise is successfully completed.
     */
    public Promise<Asset> createAsset(Asset asset, Asset parent)
    {
        CreateRequest request = new CreateRequest();
        request.setAsset(asset);
        request.setParent(parent);
        return performAssetRequest(request);
    }

    /*
     * Returns a promise to retrieve an existing asset
     *
     * @param assetType The full name of the asset, e.g. Container.class.getName()
     * @param id The asset's identifier
     * @return The retrieved asset when the promise is successfully completed.
     */
    public Promise<Asset> getAsset(String assetType, int id)
    {
        GetRequest get = new GetRequest();
        get.setId(id);
        get.setType(assetType);
        return performAssetRequest(get);
    }

    /*
     * Returns a promise to retrieve multiple assets
     *
     * @param assetType The full name of the asset, e.g. Container.class.getName()
     * @param params Optional - the search query parameters
     * @return The retrieved assets when the promise is successfully completed.
     */
    public Promise<List<Asset>> getAssets(String assetType, List<Property> params)
    {
        ListRequest listRequest = new ListRequest();
        listRequest.setType(assetType);
        if ((params != null) && (!params.isEmpty()))
        {
            listRequest.getParams().addAll(params);
        }
        if (_registry == null)
        {
            return Promise.pure(new Exception("ServiceRegistry not initialized"));
        }
        AsyncService apiService = _registry.lookupApiService();
        Promise<List<ApiResponse>> promise = apiService.sequence(listRequest);
        return promise.map(new Function<List<ApiResponse>, List<Asset>>() {
            @Override
            public List<Asset> invoke(List<ApiResponse> responses)
            {
                List<Asset> assets = new ArrayList<Asset>();
                for (ApiResponse response : responses)
                {
                    assets.add(response.getAsset());
                }
                return assets;
            }
        });
    }

    /*
     * Returns a promise to update an asset
     *
     * @param asset The asset to be updated
     * @param parent Optional - the parent of the asset to be updated.
     * @return The updated asset when the promise is successfully completed.
     */
    public Promise<Asset> updateAsset(Asset asset, Asset parent)
    {
        UpdateRequest request = new UpdateRequest();
        request.setAsset(asset);
        request.setParent(parent);
        return performAssetRequest(request);
    }

    /*
     * Returns a promise to delete an asset
     *
     * @param asset The asset to be deleted
     * @param parent Optional - the parent of the asset to be deleted
     * @return The deleted asset when the promise is successfully completed.
     */
    public Promise<Asset> deleteAsset(final Asset asset, Asset parent)
    {
        DeleteRequest request = new DeleteRequest();
        request.setAsset(asset);
        request.setParent(parent);

        if (_registry == null)
        {
            return Promise.pure(new Exception("ServiceRegistry not initialized"));
        }
        AsyncService apiService = _registry.lookupApiService();
        Promise<Response> promise = apiService.promise(request);
        return promise.map(new Function<Response, Asset>() {
            @Override
            public Asset invoke(Response response)
            {
                return asset;
            }
        });
    }

    private Promise<Asset> performAssetRequest(ApiRequest request)
    {
        if (_registry == null)
        {
            return Promise.pure(new Exception("ServiceRegistry not initialized"));
        }
        AsyncService apiService = _registry.lookupApiService();
        Promise<ApiResponse> promise = apiService.promise(request);
        return promise.map(new Function<ApiResponse, Asset>() {
            @Override
            public Asset invoke(ApiResponse response)
            {
                return response.getAsset();
            }
        });
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        try
        {
            _context = context;
            _registry = new ServiceRegistry(_context);

            for (ServiceProviderType type : getServiceProviderTypes())
            {
                // register the service
                Hashtable<String, Object> metadata = new Hashtable<String, Object>();
                metadata.put("serviceType", "service");
                metadata.put("serviceProviderType", type.getName());
                metadata.put("version", Version.SDK_VERSION);
                context.registerService(AsyncService.class.getName(), _service, metadata);
            }

            // register a service tracker to wait for sdk service
            _asyncTracker = new ServiceTracker(context, AsyncService.class.getName(),
                    new AsyncTracker(this, context, Version.SDK_VERSION));
            _asyncTracker.open();
        }
        catch (Throwable t)
        {
            logger.error(t.getMessage(), t);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {

        if (_asyncTracker != null)
        {
            _asyncTracker.close();
        }
    }

    private <REQ extends Request, RSP extends Response> void register(Class<REQ> reqClass,
            final Function<REQ, Promise<RSP>> handler)
    {
        _service.registerRequest(reqClass, new RequestHandler<REQ>() {
            Promise<RSP> _promise;

            @Override
            public void onRequest(final REQ request)
            {
                try
                {
                    _promise = handler.invoke(request);
                    _promise.onComplete(new Callback<RSP>() {
                        @Override
                        public void invoke(RSP response)
                        {
                            _service.sendResponse(request, response);
                        }
                    });
                    _promise.onFailure(new Callback<Throwable>() {
                        @Override
                        public void invoke(Throwable t)
                        {
                            _service.onError(request, t);
                        }
                    });
                }
                catch (Throwable t)
                {
                    _service.onError(request, t);
                }
            }

            @Override
            public void onCancel(long reqId)
            {
                if (_promise != null)
                {
                    _promise.cancel();
                }
            }
        });
    }

    private void register()
    {
        // 
        // Service provider operations
        //

        register(ServiceProviderPreCreateRequest.class,
                new Function<ServiceProviderPreCreateRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceProviderPreCreateRequest request)
                    {
                        try
                        {
                            IServiceProvider operations = getServiceProviderOperations();
                            if (operations != null)
                            {
                                return operations.preCreate(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceProviderPostCreateRequest.class,
                new Function<ServiceProviderPostCreateRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceProviderPostCreateRequest request)
                    {
                        try
                        {
                            IServiceProvider operations = getServiceProviderOperations();
                            if (operations != null)
                            {
                                return operations.postCreate(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceProviderPreUpdateRequest.class,
                new Function<ServiceProviderPreUpdateRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceProviderPreUpdateRequest request)
                    {
                        try
                        {
                            IServiceProvider operations = getServiceProviderOperations();
                            if (operations != null)
                            {
                                return operations.preUpdate(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceProviderPostUpdateRequest.class,
                new Function<ServiceProviderPostUpdateRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceProviderPostUpdateRequest request)
                    {
                        try
                        {
                            IServiceProvider operations = getServiceProviderOperations();
                            if (operations != null)
                            {
                                return operations.postUpdate(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceProviderPreDeleteRequest.class,
                new Function<ServiceProviderPreDeleteRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceProviderPreDeleteRequest request)
                    {
                        try
                        {
                            IServiceProvider operations = getServiceProviderOperations();
                            if (operations != null)
                            {
                                return operations.preDelete(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceProviderPostDeleteRequest.class,
                new Function<ServiceProviderPostDeleteRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceProviderPostDeleteRequest request)
                    {
                        try
                        {
                            IServiceProvider operations = getServiceProviderOperations();
                            if (operations != null)
                            {
                                return operations.postDelete(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceProviderSyncRequest.class, new Function<ServiceProviderSyncRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(ServiceProviderSyncRequest request)
            {
                try
                {
                    IServiceProvider operations = getServiceProviderOperations();
                    if (operations != null)
                    {
                        return operations.sync(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Service provider operations not supported");
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(ServiceProviderPingRequest.class, new Function<ServiceProviderPingRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(ServiceProviderPingRequest request)
            {
                try
                {
                    IServiceProvider operations = getServiceProviderOperations();
                    if (operations != null)
                    {
                        return operations.ping(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Service provider operations not supported");
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(ServiceProviderStartRequest.class,
                new Function<ServiceProviderStartRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceProviderStartRequest request)
                    {
                        try
                        {
                            IServiceProvider operations = getServiceProviderOperations();
                            if (operations != null)
                            {
                                return operations.start(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceProviderStopRequest.class, new Function<ServiceProviderStopRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(ServiceProviderStopRequest request)
            {
                try
                {
                    IServiceProvider operations = getServiceProviderOperations();
                    if (operations != null)
                    {
                        return operations.stop(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Service provider operations not supported");
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        //
        // Service Provider Binding operations
        //

        register(ServiceInstanceValidateRequest.class,
                new Function<ServiceInstanceValidateRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstanceValidateRequest request)
                    {
                        try
                        {
                            IServiceInstance operations = getServiceInstanceOperations();
                            if (operations != null)
                            {
                                return operations.validate(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider binding operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstanceProvisionRequest.class,
                new Function<ServiceInstanceProvisionRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstanceProvisionRequest request)
                    {
                        try
                        {
                            IServiceInstance operations = getServiceInstanceOperations();
                            if (operations != null)
                            {
                                return operations.provision(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider binding operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstanceReconfigureRequest.class,
                new Function<ServiceInstanceReconfigureRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstanceReconfigureRequest request)
                    {
                        try
                        {
                            IServiceInstance operations = getServiceInstanceOperations();
                            if (operations != null)
                            {
                                return operations.reconfigure(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider binding operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstanceStartRequest.class,
                new Function<ServiceInstanceStartRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstanceStartRequest request)
                    {
                        try
                        {
                            IServiceInstance operations = getServiceInstanceOperations();
                            if (operations != null)
                            {
                                return operations.start(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider binding operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstanceStopRequest.class, new Function<ServiceInstanceStopRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(ServiceInstanceStopRequest request)
            {
                try
                {
                    IServiceInstance operations = getServiceInstanceOperations();
                    if (operations != null)
                    {
                        return operations.stop(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Service provider binding operations not supported");
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(ServiceInstanceReleaseRequest.class,
                new Function<ServiceInstanceReleaseRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstanceReleaseRequest request)
                    {
                        try
                        {
                            IServiceInstance operations = getServiceInstanceOperations();
                            if (operations != null)
                            {
                                return operations.release(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider binding operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstanceReleaseRequest.class,
                new Function<ServiceInstanceReleaseRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstanceReleaseRequest request)
                    {
                        try
                        {
                            IServiceInstance operations = getServiceInstanceOperations();
                            if (operations != null)
                            {
                                return operations.release(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Service provider binding operations not supported");
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        //
        // IAssetNotification operations
        //

        register(PostCreateRequest.class, new Function<PostCreateRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(PostCreateRequest request)
            {
                try
                {
                    IAssetLifecycle operations = getAssetNotificationOperations();
                    if (operations != null)
                    {
                        return operations.postCreate(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(PostDeleteRequest.class, new Function<PostDeleteRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(PostDeleteRequest request)
            {
                try
                {
                    IAssetLifecycle operations = getAssetNotificationOperations();
                    if (operations != null)
                    {
                        return operations.postDelete(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(PostUpdateRequest.class, new Function<PostUpdateRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(PostUpdateRequest request)
            {
                try
                {
                    IAssetLifecycle operations = getAssetNotificationOperations();
                    if (operations != null)
                    {
                        return operations.postUpdate(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(PreCreateRequest.class, new Function<PreCreateRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(PreCreateRequest request)
            {
                try
                {
                    IAssetLifecycle operations = getAssetNotificationOperations();
                    if (operations != null)
                    {
                        return operations.preCreate(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(PreUpdateRequest.class, new Function<PreUpdateRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(PreUpdateRequest request)
            {
                try
                {
                    IAssetLifecycle operations = getAssetNotificationOperations();
                    if (operations != null)
                    {
                        return operations.preUpdate(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(PreDeleteRequest.class, new Function<PreDeleteRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(PreDeleteRequest request)
            {
                try
                {
                    IAssetLifecycle operations = getAssetNotificationOperations();
                    if (operations != null)
                    {
                        return operations.preDelete(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        //
        // IInstance operations
        //

        register(InstancePreProvisionRequest.class, new Function<InstancePreProvisionRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePreProvisionRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.preProvision(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePostProvisionRequest.class, new Function<InstancePostProvisionRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePostProvisionRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.postProvision(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePreBootRequest.class, new Function<InstancePreBootRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePreBootRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.preBoot(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePostBootRequest.class, new Function<InstancePostBootRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePostBootRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.postBoot(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePreStartRequest.class, new Function<InstancePreStartRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePreStartRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.preStart(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePostStartRequest.class, new Function<InstancePostStartRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePostStartRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.postStart(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePreStopRequest.class, new Function<InstancePreStopRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePreStopRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.preStop(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePostStopRequest.class, new Function<InstancePostStopRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePostStopRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.postStop(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePreRestartRequest.class, new Function<InstancePreRestartRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePreRestartRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.preRestart(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePostRestartRequest.class, new Function<InstancePostRestartRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePostRestartRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.postRestart(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePreReleaseRequest.class, new Function<InstancePreReleaseRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePreReleaseRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.preRelease(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePostReleaseRequest.class, new Function<InstancePostReleaseRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePostReleaseRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.postRelease(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePreReconfigureRequest.class, new Function<InstancePreReconfigureRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePreReconfigureRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.preReconfigure(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(InstancePostReconfigureRequest.class, new Function<InstancePostReconfigureRequest, Promise<InstanceResponse>>() {
            @Override
            public Promise<InstanceResponse> invoke(InstancePostReconfigureRequest request)
            {
                try
                {
                    IInstanceLifecycle operations = getInstanceOperations();
                    if (operations != null)
                    {
                        return operations.postReconfigure(request);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        //
        // IServiceInstanceLifycle operations
        //

        register(ServiceInstancePreProvisionRequest.class,
                new Function<ServiceInstancePreProvisionRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePreProvisionRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.preProvision(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstancePostProvisionRequest.class,
                new Function<ServiceInstancePostProvisionRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePostProvisionRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.postProvision(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstancePreStartRequest.class,
                new Function<ServiceInstancePreStartRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePreStartRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.preStart(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstancePostStartRequest.class,
                new Function<ServiceInstancePostStartRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePostStartRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.postStart(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstancePreStopRequest.class,
                new Function<ServiceInstancePreStopRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePreStopRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.preStop(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstancePostStopRequest.class,
                new Function<ServiceInstancePostStopRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePostStopRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.postStop(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstancePreRestartRequest.class,
                new Function<ServiceInstancePreRestartRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePreRestartRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.preRestart(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstancePostRestartRequest.class,
                new Function<ServiceInstancePostRestartRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePostRestartRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.postRestart(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstancePreReleaseRequest.class,
                new Function<ServiceInstancePreReleaseRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePreReleaseRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.preRelease(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ServiceInstancePostReleaseRequest.class,
                new Function<ServiceInstancePostReleaseRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ServiceInstancePostReleaseRequest request)
                    {
                        try
                        {
                            IServiceInstanceLifecycle operations = getServiceInstanceLifecycleOperations();
                            if (operations != null)
                            {
                                return operations.postRelease(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        //
        // IAddressManagement operations
        //

        register(AvailableAddressesRequest.class, new Function<AvailableAddressesRequest, Promise<AvailableAddressesResponse>>() {
            @Override
            public Promise<AvailableAddressesResponse> invoke(AvailableAddressesRequest request)
            {
                try
                {
                    IAddressManagement operations = getAddressManagementOperations();
                    if (operations != null)
                    {
                        return operations.getAvailableAddresses(request);
                    }
                    else
                    {
                        AvailableAddressesResponse response = new AvailableAddressesResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }

        });

        register(ReserveAddressRequest.class, new Function<ReserveAddressRequest, Promise<ReserveAddressResponse>>() {
            @Override
            public Promise<ReserveAddressResponse> invoke(ReserveAddressRequest request)
            {
                try
                {
                    IAddressManagement operations = getAddressManagementOperations();
                    if (operations != null)
                    {
                        return operations.reserveAddress(request);
                    }
                    else
                    {
                        ReserveAddressResponse response = new ReserveAddressResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }

        });

        register(UnreserveAddressRequest.class, new Function<UnreserveAddressRequest, Promise<UnreserveAddressResponse>>() {
            @Override
            public Promise<UnreserveAddressResponse> invoke(UnreserveAddressRequest request)
            {
                try
                {
                    IAddressManagement operations = getAddressManagementOperations();
                    if (operations != null)
                    {
                        return operations.unreserveAddress(request);
                    }
                    else
                    {
                        UnreserveAddressResponse response = new UnreserveAddressResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }

        });

        register(ReleaseAddressRequest.class, new Function<ReleaseAddressRequest, Promise<ReleaseAddressResponse>>() {
            @Override
            public Promise<ReleaseAddressResponse> invoke(ReleaseAddressRequest request)
            {
                try
                {
                    IAddressManagement operations = getAddressManagementOperations();
                    if (operations != null)
                    {
                        return operations.releaseAddress(request);
                    }
                    else
                    {
                        ReleaseAddressResponse response = new ReleaseAddressResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }

        });

        //
        // IConnection operations
        //

        register(ConnectionPreCreateRequest.class, new Function<ConnectionPreCreateRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(ConnectionPreCreateRequest request)
            {
                try
                {
                    IConnection operations = getConnectionOperations();
                    if (operations != null)
                    {
                        return operations.preCreate(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(ConnectionPostCreateRequest.class,
                new Function<ConnectionPostCreateRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ConnectionPostCreateRequest request)
                    {
                        try
                        {
                            IConnection operations = getConnectionOperations();
                            if (operations != null)
                            {
                                return operations.postCreate(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ConnectionPreUpdateRequest.class, new Function<ConnectionPreUpdateRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(ConnectionPreUpdateRequest request)
            {
                try
                {
                    IConnection operations = getConnectionOperations();
                    if (operations != null)
                    {
                        return operations.preUpdate(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(ConnectionPostUpdateRequest.class,
                new Function<ConnectionPostUpdateRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ConnectionPostUpdateRequest request)
                    {
                        try
                        {
                            IConnection operations = getConnectionOperations();
                            if (operations != null)
                            {
                                return operations.postUpdate(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        register(ConnectionPreDeleteRequest.class, new Function<ConnectionPreDeleteRequest, Promise<ServiceProviderResponse>>() {
            @Override
            public Promise<ServiceProviderResponse> invoke(ConnectionPreDeleteRequest request)
            {
                try
                {
                    IConnection operations = getConnectionOperations();
                    if (operations != null)
                    {
                        return operations.preDelete(request);
                    }
                    else
                    {
                        ServiceProviderResponse response = new ServiceProviderResponse();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        register(ConnectionPostDeleteRequest.class,
                new Function<ConnectionPostDeleteRequest, Promise<ServiceProviderResponse>>() {
                    @Override
                    public Promise<ServiceProviderResponse> invoke(ConnectionPostDeleteRequest request)
                    {
                        try
                        {
                            IConnection operations = getConnectionOperations();
                            if (operations != null)
                            {
                                return operations.postDelete(request);
                            }
                            else
                            {
                                ServiceProviderResponse response = new ServiceProviderResponse();
                                response.setStatus(Status.COMPLETE);
                                return Promise.pure(response);
                            }
                        }
                        catch (Throwable t)
                        {
                            return Promise.pure(t);
                        }
                    }
                });

        //
        // IValueProvider
        //

        register(PropertyTypeValueRequest.class, new Function<PropertyTypeValueRequest, Promise<PropertyTypeValueResponse>>() {
            @Override
            public Promise<PropertyTypeValueResponse> invoke(PropertyTypeValueRequest request)
            {
                try
                {
                    IValueProvider provider = getValueProvider(request.getValueProvider());
                    if (provider != null)
                    {
                        return provider.getRootValues(request);
                    }
                    else
                    {
                        PropertyTypeValueResponse response = new PropertyTypeValueResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Property type value provider not supported");
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });

        //
        // IMethodCall
        //

        register(MethodRequest.class, new Function<MethodRequest, Promise<MethodResponse>>() {
            @Override
            public Promise<MethodResponse> invoke(MethodRequest request)
            {
                try
                {
                    IMethod imethod = getMethod(request.getName());
                    if (imethod != null)
                    {
                        Map<String, MethodVariable> arguments = new HashMap<String, MethodVariable>();
                        for (MethodVariable arg : request.getArguments())
                        {
                            arguments.put(arg.getName(), arg);
                        }
                        return imethod.execute(request, arguments);
                    }
                    else
                    {
                        MethodResponse response = new MethodResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Method not supported: " + request.getName());
                        return Promise.pure(response);
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            }
        });
    }

    protected IValueProvider getValueProvider(String className)
    {
        try
        {
            IValueProvider provider = _providers.get(className);
            if (provider == null)
            {
                Class<?> pClass = Class.forName(className, true, this.getClass().getClassLoader());
                if (pClass == null)
                {
                    return null;
                }
                provider = (IValueProvider) pClass.newInstance();
                _providers.put(className, provider);
            }
            return provider;
        }
        catch (Exception ex)
        {
            logger.error(ex);
            return null;
        }
    }

    protected IMethod getMethod(String name)
    {
        return _methods.get(name);
    }

    public void registerValueProvider(String className, IValueProvider provider)
    {
        _providers.put(className, provider);
    }

    public void deregisterValueProvider(String className)
    {
        _providers.remove(className);
    }

    public void registerMethod(String name, IMethod imethod)
    {
        _methods.put(name, imethod);
    }

    public void deregisterMethod(String name)
    {
        _methods.remove(name);
    }

    protected AsyncService getService()
    {
        return _service;
    }

    public Reactor getReactor()
    {
        return _service.getReactor();
    }

}
