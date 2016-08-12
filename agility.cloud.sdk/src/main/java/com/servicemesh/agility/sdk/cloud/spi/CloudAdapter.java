package com.servicemesh.agility.sdk.cloud.spi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.servicemesh.agility.api.ApiResponse;
import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Cloud;
import com.servicemesh.agility.api.Credential;
import com.servicemesh.agility.api.GetRequest;
import com.servicemesh.agility.api.Image;
import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.Property;
import com.servicemesh.agility.api.SearchRequest;
import com.servicemesh.agility.api.UpdateRequest;
import com.servicemesh.agility.sdk.cloud.impl.AsyncTracker;
import com.servicemesh.agility.sdk.cloud.msgs.AddressRangeSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.AddressRangeSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CloudChangeRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CloudPropertyRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CloudPropertyResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CloudResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CloudSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CloudSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialSelectRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialSelectResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.CustomerGatewayDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.DHCPOptionsAssociateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.DHCPOptionsCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.DHCPOptionsDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ImageCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ImageDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ImageResponse;
import com.servicemesh.agility.sdk.cloud.msgs.ImageSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ImageSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceBootRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceCreateSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceHotswapRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceRebootRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceReleaseRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceRemoveAllSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceRemoveSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceResponse;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceRevertSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceSnapshotResponse;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceStartRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceStopRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.InstanceUpdateSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InternetGatewayDetachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.LocationSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.LocationSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.ModelSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.ModelSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.NetworkSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.NetworkSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.PostCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PostDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PostUpdateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PreCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PreDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.PreUpdateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.RegistrationRequest;
import com.servicemesh.agility.sdk.cloud.msgs.RegistrationResponse;
import com.servicemesh.agility.sdk.cloud.msgs.RepositorySyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.RepositorySyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.RunningInstancesStopRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageAttachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageCreateFromSnapshotRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageDetachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageDetachableRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageResponse;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSnapshotCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSnapshotDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSnapshotResponse;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.StorageSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.SubnetAddRequest;
import com.servicemesh.agility.sdk.cloud.msgs.SubnetChangeRequest;
import com.servicemesh.agility.sdk.cloud.msgs.SubnetDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPCCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPCDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPCResponse;
import com.servicemesh.agility.sdk.cloud.msgs.VPCSubnetsDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPCSyncRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPCSyncResponse;
import com.servicemesh.agility.sdk.cloud.msgs.VPNConnectionDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNConnectionStateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNConnectionStateResponse;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayAttachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayAttachmentsRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayAttachmentsResponse;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayConnectionCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayConnectionDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayDetachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayStateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayStateResponse;
import com.servicemesh.core.async.AsyncService;
import com.servicemesh.core.async.RequestHandler;
import com.servicemesh.core.async.ResponseHandler;
import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;
import com.servicemesh.core.messaging.Status;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerHandler;
import com.servicemesh.io.proxy.Proxy;
import com.servicemesh.io.proxy.ProxyType;

public abstract class CloudAdapter implements BundleActivator
{

    private static final Logger logger = Logger.getLogger(CloudAdapter.class);
    public static final String AGILITY_API_VERSION = "3.1";

    protected AsyncService _service;
    protected ServiceTracker _asyncTracker;
    protected ExecutorService _executor;
    protected BundleContext _context;

    public CloudAdapter(Reactor reactor)
    {
        this(reactor, null);
    }

    public CloudAdapter(Reactor reactor, ExecutorService executor)
    {
        _service = new AsyncService(reactor);
        _executor = executor;
        register();
    }

    public CloudAdapter(AsyncService service)
    {
        this(service, null);
    }

    public CloudAdapter(AsyncService service, ExecutorService executor)
    {
        _service = service;
        _executor = executor;
        register();
    }

    public abstract String getCloudType();

    public abstract String getAdapterName();

    public abstract String getAdapterVersion();

    public abstract RegistrationRequest getRegistrationRequest();

    public abstract void onRegistration(RegistrationResponse response);

    public abstract ISync<AddressRangeSyncRequest, AddressRangeSyncResponse> getAddressRangeSync();

    public abstract ISync<CloudSyncRequest, CloudSyncResponse> getCloudSync();

    public abstract ISync<CredentialSyncRequest, CredentialSyncResponse> getCredentialSync();

    public abstract ISync<LocationSyncRequest, LocationSyncResponse> getLocationSync();

    public abstract ISync<ImageSyncRequest, ImageSyncResponse> getImageSync();

    public abstract ISync<InstanceSyncRequest, InstanceSyncResponse> getInstanceSync();

    public abstract ISync<ModelSyncRequest, ModelSyncResponse> getModelSync();

    public abstract ISync<NetworkSyncRequest, NetworkSyncResponse> getNetworkSync();

    public abstract ISync<StorageSyncRequest, StorageSyncResponse> getStorageSync();

    /* This method needs to be overridden by the EC2 adapter to process VPC sync requests */
    public ISync<VPCSyncRequest, VPCSyncResponse> getVPCSync()
    {
        return new VPCSyncHandler(); //  Provide a default handler that just completes this phase of sync.
    }

    /* This method needs to be overridden by any adapter that wants to support Repository sync */
    public ISync<RepositorySyncRequest, RepositorySyncResponse> getRepositorySync()
    {
        return new RepositorySyncHandler(); //  Provide a default handler that just completes this phase of sync.
    }

    public abstract IInstance getInstanceOperations();

    public abstract IStorage getStorageOperations();

    public abstract ICredential getCredentialOperations();

    public abstract IImage getImageOperations();

    /*
     * These operations are optional for a cloud adapter.  If not implemented, 
     * a "not supported" error is returned.
     */

    public IStorageSnapshot getSnapshotOperations()
    {
        return null;
    }

    public ISnapshot getInstanceSnapshotOperations()
    {
        return null;
    }

    public IHotSwap getHotSwapOperations()
    {
        return null;
    }

    public ICloudProperties getCloudPropertyOperations()
    {
        return null;
    }

    public IVPC getVPCOperations()
    {
        return null;
    }

    public ICredentialSelector getCredentialSelectorOperations()
    {
        return null;
    }

    public ICloudChanged getCloudChangedOperations()
    {
        return null;
    }

    /*
     * These methods handle the asset CRUD notifications.  A cloud adapter can override this
     * method to return a class that implements IAssetNotification and processes the messages.
     * A default implementation is provided below.
     * 
     * A cloud adapter will send a list of asset types it's interested in receiving CRUD event
     * messages for when the adapter sends it's initial registration message.
     *
     */
    public IAssetNotification getAssetNotificationOperations()
    {
        return new IAssetNotification() {

            @Override
            public ICancellable preCreate(PreCreateRequest request, ResponseHandler<CloudResponse> handler)
            {
                CloudResponse response = new CloudResponse();
                response.setStatus(Status.COMPLETE);
                handler.onResponse(response);
                return null;
            }

            @Override
            public ICancellable postCreate(PostCreateRequest request, ResponseHandler<CloudResponse> handler)
            {
                CloudResponse response = new CloudResponse();
                response.setStatus(Status.COMPLETE);
                handler.onResponse(response);
                return null;
            }

            @Override
            public ICancellable preUpdate(PreUpdateRequest request, ResponseHandler<CloudResponse> handler)
            {
                CloudResponse response = new CloudResponse();
                response.setStatus(Status.COMPLETE);
                handler.onResponse(response);
                return null;
            }

            @Override
            public ICancellable postUpdate(PostUpdateRequest request, ResponseHandler<CloudResponse> handler)
            {
                CloudResponse response = new CloudResponse();
                response.setStatus(Status.COMPLETE);
                handler.onResponse(response);
                return null;
            }

            @Override
            public ICancellable preDelete(PreDeleteRequest request, ResponseHandler<CloudResponse> handler)
            {
                CloudResponse response = new CloudResponse();
                response.setStatus(Status.COMPLETE);
                handler.onResponse(response);
                return null;
            }

            @Override
            public ICancellable postDelete(PostDeleteRequest request, ResponseHandler<CloudResponse> handler)
            {
                CloudResponse response = new CloudResponse();
                response.setStatus(Status.COMPLETE);
                handler.onResponse(response);
                return null;
            }
        };
    }

    public static Proxy getAsyncProxies(Cloud cloud)
    {
        Proxy firstProxy = null;

        List<com.servicemesh.agility.api.Proxy> possible_proxies = cloud.getProxies();
        List<com.servicemesh.agility.api.Proxy> proxies = new ArrayList<com.servicemesh.agility.api.Proxy>();

        //  Get the MANAGER -> CLOUD proxies, if any:
        for (com.servicemesh.agility.api.Proxy proxy : possible_proxies)
        {
            switch (proxy.getProxyUsage())
            {
                case PROXY_ALL:
                case PROXY_MANAGER_CLOUD:
                    proxies.add(proxy);
                    break;
                default:
                    break;
            }
        }

        if (proxies.size() > 0)
        {
            Proxy lastProxy = null;
            for (com.servicemesh.agility.api.Proxy proxy : proxies)
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
                        //  Not supported for cloud
                        break;
                    default:
                        throw new RuntimeException("Unsupported authentication type: " + proxy.getAuthType());
                }

                Proxy nextProxy = new Proxy(hostname, port, proxyType, null, admin, passwd);

                if (firstProxy == null)
                {
                    firstProxy = nextProxy;
                }

                if (lastProxy != null)
                {
                    lastProxy.setTargetHost(nextProxy);
                }

                lastProxy = nextProxy;
            }
        }

        return firstProxy;
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

    public AsyncService getAgilityApiService()
    {
        if (_context != null)
        {
            StringBuilder filter = new StringBuilder();
            filter.append("(&(serviceType=api)(version=");
            filter.append(AGILITY_API_VERSION);
            filter.append("))");
            ServiceReference<?>[] references = null;
            try
            {
                references = _context.getServiceReferences(AsyncService.class.getName(), filter.toString());
            }
            catch (InvalidSyntaxException ex)
            {
            }
            if (references != null && references.length > 0)
            {
                return (AsyncService) _context.getService(references[0]);
            }
        }
        return null;
    }

    /*
     * Don't know if this is currently used but it's a good example of using the Async API service - in
     * this case - the Search functionality.
     */
    public void getInstance(String user, final int cloudId, final String instanceId, final Callback<Instance> callback)
    {
        AsyncService apiService = getAgilityApiService();
        Property filter = new Property();
        filter.setName("qterm.field.instanceId");
        filter.setValue(instanceId);
        SearchRequest search = new SearchRequest();
        search.setUser(user);
        search.setType(Instance.class.getName());
        search.getParams().add(filter);
        apiService.sendRequest(search, new ResponseHandler<ApiResponse>() {

            @Override
            public boolean onResponse(ApiResponse response)
            {
                if (response.getStatus() == Status.FAILURE)
                {
                    callback.onError(new Exception(response.getMessage()));
                    return false;
                }

                Asset asset = response.getAsset();
                if (asset instanceof Instance)
                {
                    Instance instance = (Instance) asset;
                    if (instance.getInstanceId().equals(instanceId) && instance.getCloud().getId() == cloudId)
                    {
                        callback.onResponse(instance);
                        return false;
                    }
                }

                if (response.getStatus() == Status.COMPLETE)
                {
                    callback.onResponse(null);
                    return false;
                }
                return true;
            }

            @Override
            public void onError(Request request, Throwable t)
            {
                callback.onError(t);
            }
        });
    }

    /*
     * This method should get any arbitrary asset.  The type should be something like:
     * 
     *   Container.class.getName()
     */
    public void getAsset(String assetType, int id, final Callback<Asset> callback)
    {
        AsyncService apiService = getAgilityApiService();
        GetRequest get = new GetRequest();
        get.setId(id);
        get.setType(assetType);

        apiService.sendRequest(get, new ResponseHandler<ApiResponse>() {

            @Override
            public boolean onResponse(ApiResponse response)
            {
                if (response.getStatus() == Status.FAILURE)
                {
                    callback.onError(new Exception(response.getMessage()));
                    return false;
                }

                Asset asset = response.getAsset();
                callback.onResponse(asset);
                return false;
            }

            @Override
            public void onError(Request request, Throwable t)
            {
                callback.onError(t);
            }
        });
    }

    /*
     * This is currently being used by the async vCD adapter to update the hot swap
     * flags on an image.  In order to override the existing flags, the boolean:
     * "flagsSynced" in Image must be set to false - this allows the update method in
     * ImageImpl to re-write the hot swap flags to the desired settings.
     */
    public void updateImage(Image image, Asset parent, final Callback<Image> callback)
    {
        AsyncService apiService = getAgilityApiService();
        UpdateRequest update = new UpdateRequest();
        update.setAsset(image);
        update.setParent(parent); //  Will be container for cloud

        apiService.sendRequest(update, new ResponseHandler<ApiResponse>() {

            @Override
            public boolean onResponse(ApiResponse response)
            {
                if (response.getStatus() == Status.FAILURE)
                {
                    callback.onError(new Exception(response.getMessage()));
                    return false;
                }

                Asset asset = response.getAsset();
                if (asset instanceof Image)
                {
                    Image newImage = (Image) asset;
                    callback.onResponse(newImage);
                    return false;
                }

                if (response.getStatus() == Status.COMPLETE)
                {
                    callback.onResponse(null);
                    return false;
                }
                return true;
            }

            @Override
            public void onError(Request request, Throwable t)
            {
                callback.onError(t);
            }
        });
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        try
        {
            _context = context;

            // register the service
            Hashtable<String, Object> metadata = new Hashtable<String, Object>();
            metadata.put("serviceType", "cloud");
            metadata.put("cloudType", getCloudType());
            metadata.put("version", AGILITY_API_VERSION);
            context.registerService(AsyncService.class.getName(), _service, metadata);

            // register a service tracker to wait for sdk service
            _asyncTracker = new ServiceTracker(context, AsyncService.class.getName(),
                    new AsyncTracker(this, context, AGILITY_API_VERSION));
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

    public ICancellable timerCreateRel(long interval, TimerHandler handler)
    {
        return new TimerCancel(_service.getReactor().timerCreateRel(interval, handler));
    }

    public interface Dispatch<REQ extends Request, RSP extends Response>
    {
        public ICancellable execute(REQ req, ResponseHandler<RSP> resp);
    };

    private <REQ extends Request, RSP extends Response> void register(Class<REQ> reqClass, final Dispatch<REQ, RSP> handler)
    {
        if (_executor != null)
        {
            _service.registerRequest(reqClass, new RequestHandler<REQ>() {
                ICancellable _cancellable;

                @Override
                public void onRequest(final REQ request)
                {
                    _executor.submit(new Runnable() {
                        @Override
                        public void run()
                        {

                            _cancellable = handler.execute(request, new ResponseHandler<RSP>() {

                                @Override
                                public boolean onResponse(RSP response)
                                {
                                    _service.sendResponse(request, response);
                                    return false;
                                }

                                @Override
                                public void onError(Request request, Throwable t)
                                {
                                    _service.onError(request, t);
                                }

                            });
                        }
                    });
                }

                @Override
                public void onCancel(long reqId)
                {
                    if (_cancellable != null)
                    {
                        _cancellable.cancel();
                    }
                }
            });
        }
        else
        {
            _service.registerRequest(reqClass, new RequestHandler<REQ>() {
                ICancellable _cancellable;

                @Override
                public void onRequest(final REQ request)
                {
                    _cancellable = handler.execute(request, new ResponseHandler<RSP>() {

                        @Override
                        public boolean onResponse(RSP response)
                        {
                            _service.sendResponse(request, response);
                            return false;
                        }

                        @Override
                        public void onError(Request request, Throwable t)
                        {
                            _service.onError(request, t);
                        }

                    });
                }

                @Override
                public void onCancel(long reqId)
                {
                    if (_cancellable != null)
                    {
                        _cancellable.cancel();
                    }
                }
            });
        }
    }

    private void register()
    {
        //
        // Credential Operations
        //

        register(CredentialCreateRequest.class, new Dispatch<CredentialCreateRequest, CredentialResponse>() {
            @Override
            public ICancellable execute(CredentialCreateRequest request, ResponseHandler<CredentialResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    ICredential iops = getCredentialOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.create(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(CredentialDeleteRequest.class, new Dispatch<CredentialDeleteRequest, CredentialResponse>() {
            @Override
            public ICancellable execute(CredentialDeleteRequest request, ResponseHandler<CredentialResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    ICredential iops = getCredentialOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.delete(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        // Instance Operations
        //

        register(InstanceBootRequest.class, new Dispatch<InstanceBootRequest, InstanceResponse>() {
            @Override
            public ICancellable execute(InstanceBootRequest request, ResponseHandler<InstanceResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IInstance iops = getInstanceOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.boot(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InstanceCreateRequest.class, new Dispatch<InstanceCreateRequest, InstanceResponse>() {
            @Override
            public ICancellable execute(InstanceCreateRequest request, ResponseHandler<InstanceResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IInstance iops = getInstanceOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.create(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InstanceStartRequest.class, new Dispatch<InstanceStartRequest, InstanceResponse>() {
            @Override
            public ICancellable execute(InstanceStartRequest request, ResponseHandler<InstanceResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IInstance iops = getInstanceOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.start(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InstanceStopRequest.class, new Dispatch<InstanceStopRequest, InstanceResponse>() {
            @Override
            public ICancellable execute(InstanceStopRequest request, ResponseHandler<InstanceResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IInstance iops = getInstanceOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.stop(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InstanceRebootRequest.class, new Dispatch<InstanceRebootRequest, InstanceResponse>() {
            @Override
            public ICancellable execute(InstanceRebootRequest request, ResponseHandler<InstanceResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IInstance iops = getInstanceOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.reboot(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InstanceReleaseRequest.class, new Dispatch<InstanceReleaseRequest, InstanceResponse>() {
            @Override
            public ICancellable execute(InstanceReleaseRequest request, ResponseHandler<InstanceResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IInstance iops = getInstanceOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.release(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        // Image Operations
        //

        register(ImageCreateRequest.class, new Dispatch<ImageCreateRequest, ImageResponse>() {
            @Override
            public ICancellable execute(ImageCreateRequest request, ResponseHandler<ImageResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IImage iops = getImageOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.create(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(ImageDeleteRequest.class, new Dispatch<ImageDeleteRequest, ImageResponse>() {
            @Override
            public ICancellable execute(ImageDeleteRequest request, ResponseHandler<ImageResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IImage iops = getImageOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.delete(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        // Storage Operations
        //

        register(StorageCreateRequest.class, new Dispatch<StorageCreateRequest, StorageResponse>() {
            @Override
            public ICancellable execute(StorageCreateRequest request, ResponseHandler<StorageResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IStorage iops = getStorageOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.create(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(StorageAttachRequest.class, new Dispatch<StorageAttachRequest, StorageResponse>() {
            @Override
            public ICancellable execute(StorageAttachRequest request, ResponseHandler<StorageResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IStorage iops = getStorageOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.attach(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(StorageDeleteRequest.class, new Dispatch<StorageDeleteRequest, StorageResponse>() {
            @Override
            public ICancellable execute(StorageDeleteRequest request, ResponseHandler<StorageResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IStorage iops = getStorageOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.delete(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(StorageDetachRequest.class, new Dispatch<StorageDetachRequest, StorageResponse>() {
            @Override
            public ICancellable execute(StorageDetachRequest request, ResponseHandler<StorageResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IStorage iops = getStorageOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.detach(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(StorageSnapshotCreateRequest.class, new Dispatch<StorageSnapshotCreateRequest, StorageSnapshotResponse>() {
            @Override
            public ICancellable execute(StorageSnapshotCreateRequest request, ResponseHandler<StorageSnapshotResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IStorageSnapshot iops = getSnapshotOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.create(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(StorageCreateFromSnapshotRequest.class, new Dispatch<StorageCreateFromSnapshotRequest, StorageResponse>() {
            @Override
            public ICancellable execute(StorageCreateFromSnapshotRequest request, ResponseHandler<StorageResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IStorageSnapshot iops = getSnapshotOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.createFromSnapshot(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(StorageSnapshotDeleteRequest.class, new Dispatch<StorageSnapshotDeleteRequest, StorageSnapshotResponse>() {
            @Override
            public ICancellable execute(StorageSnapshotDeleteRequest request, ResponseHandler<StorageSnapshotResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IStorageSnapshot iops = getSnapshotOperations();
                    Thread.currentThread().setContextClassLoader(iops.getClass().getClassLoader());
                    return iops.delete(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        // Sync
        //

        register(AddressRangeSyncRequest.class, new Dispatch<AddressRangeSyncRequest, AddressRangeSyncResponse>() {
            @Override
            public ICancellable execute(AddressRangeSyncRequest request, ResponseHandler<AddressRangeSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<AddressRangeSyncRequest, AddressRangeSyncResponse> isync = getAddressRangeSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(CloudSyncRequest.class, new Dispatch<CloudSyncRequest, CloudSyncResponse>() {
            @Override
            public ICancellable execute(CloudSyncRequest request, ResponseHandler<CloudSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<CloudSyncRequest, CloudSyncResponse> isync = getCloudSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(CredentialSyncRequest.class, new Dispatch<CredentialSyncRequest, CredentialSyncResponse>() {
            @Override
            public ICancellable execute(CredentialSyncRequest request, ResponseHandler<CredentialSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<CredentialSyncRequest, CredentialSyncResponse> isync = getCredentialSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(LocationSyncRequest.class, new Dispatch<LocationSyncRequest, LocationSyncResponse>() {
            @Override
            public ICancellable execute(LocationSyncRequest request, ResponseHandler<LocationSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<LocationSyncRequest, LocationSyncResponse> isync = getLocationSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(ImageSyncRequest.class, new Dispatch<ImageSyncRequest, ImageSyncResponse>() {
            @Override
            public ICancellable execute(ImageSyncRequest request, ResponseHandler<ImageSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<ImageSyncRequest, ImageSyncResponse> isync = getImageSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InstanceSyncRequest.class, new Dispatch<InstanceSyncRequest, InstanceSyncResponse>() {
            @Override
            public ICancellable execute(InstanceSyncRequest request, ResponseHandler<InstanceSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<InstanceSyncRequest, InstanceSyncResponse> isync = getInstanceSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(ModelSyncRequest.class, new Dispatch<ModelSyncRequest, ModelSyncResponse>() {
            @Override
            public ICancellable execute(ModelSyncRequest request, ResponseHandler<ModelSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<ModelSyncRequest, ModelSyncResponse> isync = getModelSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(NetworkSyncRequest.class, new Dispatch<NetworkSyncRequest, NetworkSyncResponse>() {
            @Override
            public ICancellable execute(NetworkSyncRequest request, ResponseHandler<NetworkSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<NetworkSyncRequest, NetworkSyncResponse> isync = getNetworkSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(StorageSyncRequest.class, new Dispatch<StorageSyncRequest, StorageSyncResponse>() {
            @Override
            public ICancellable execute(StorageSyncRequest request, ResponseHandler<StorageSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<StorageSyncRequest, StorageSyncResponse> isync = getStorageSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPCSyncRequest.class, new Dispatch<VPCSyncRequest, VPCSyncResponse>() {
            @Override
            public ICancellable execute(VPCSyncRequest request, ResponseHandler<VPCSyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<VPCSyncRequest, VPCSyncResponse> isync = getVPCSync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(RepositorySyncRequest.class, new Dispatch<RepositorySyncRequest, RepositorySyncResponse>() {
            @Override
            public ICancellable execute(RepositorySyncRequest request, ResponseHandler<RepositorySyncResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ISync<RepositorySyncRequest, RepositorySyncResponse> isync = getRepositorySync();
                try
                {
                    Thread.currentThread().setContextClassLoader(isync.getClass().getClassLoader());
                    return isync.sync(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        // Asset Notification operations
        //
        register(PreCreateRequest.class, new Dispatch<PreCreateRequest, CloudResponse>() {
            @Override
            public ICancellable execute(PreCreateRequest request, ResponseHandler<CloudResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                IAssetNotification notification = getAssetNotificationOperations();
                try
                {
                    Thread.currentThread().setContextClassLoader(notification.getClass().getClassLoader());
                    return notification.preCreate(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(PostCreateRequest.class, new Dispatch<PostCreateRequest, CloudResponse>() {
            @Override
            public ICancellable execute(PostCreateRequest request, ResponseHandler<CloudResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                IAssetNotification notification = getAssetNotificationOperations();
                try
                {
                    Thread.currentThread().setContextClassLoader(notification.getClass().getClassLoader());
                    return notification.postCreate(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(PreUpdateRequest.class, new Dispatch<PreUpdateRequest, CloudResponse>() {
            @Override
            public ICancellable execute(PreUpdateRequest request, ResponseHandler<CloudResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                IAssetNotification notification = getAssetNotificationOperations();
                try
                {
                    Thread.currentThread().setContextClassLoader(notification.getClass().getClassLoader());
                    return notification.preUpdate(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(PostUpdateRequest.class, new Dispatch<PostUpdateRequest, CloudResponse>() {
            @Override
            public ICancellable execute(PostUpdateRequest request, ResponseHandler<CloudResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                IAssetNotification notification = getAssetNotificationOperations();
                try
                {
                    Thread.currentThread().setContextClassLoader(notification.getClass().getClassLoader());
                    return notification.postUpdate(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(PreDeleteRequest.class, new Dispatch<PreDeleteRequest, CloudResponse>() {
            @Override
            public ICancellable execute(PreDeleteRequest request, ResponseHandler<CloudResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                IAssetNotification notification = getAssetNotificationOperations();
                try
                {
                    Thread.currentThread().setContextClassLoader(notification.getClass().getClassLoader());
                    return notification.preDelete(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(PostDeleteRequest.class, new Dispatch<PostDeleteRequest, CloudResponse>() {
            @Override
            public ICancellable execute(PostDeleteRequest request, ResponseHandler<CloudResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                IAssetNotification notification = getAssetNotificationOperations();
                try
                {
                    Thread.currentThread().setContextClassLoader(notification.getClass().getClassLoader());
                    return notification.postDelete(request, handler);
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        //  Instance snapshot operations:
        //
        register(InstanceCreateSnapshotRequest.class, new Dispatch<InstanceCreateSnapshotRequest, InstanceSnapshotResponse>() {
            @Override
            public ICancellable execute(InstanceCreateSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    ISnapshot operations = getInstanceSnapshotOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.createSnapshot(request, handler);
                    }
                    else
                    {
                        InstanceSnapshotResponse response = new InstanceSnapshotResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Instance snapshot create operation not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InstanceUpdateSnapshotRequest.class, new Dispatch<InstanceUpdateSnapshotRequest, InstanceSnapshotResponse>() {
            @Override
            public ICancellable execute(InstanceUpdateSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    ISnapshot operations = getInstanceSnapshotOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.updateSnapshot(request, handler);
                    }
                    else
                    {
                        InstanceSnapshotResponse response = new InstanceSnapshotResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Instance snapshot update operation not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InstanceRemoveSnapshotRequest.class, new Dispatch<InstanceRemoveSnapshotRequest, InstanceSnapshotResponse>() {
            @Override
            public ICancellable execute(InstanceRemoveSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    ISnapshot operations = getInstanceSnapshotOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.removeSnapshot(request, handler);
                    }
                    else
                    {
                        InstanceSnapshotResponse response = new InstanceSnapshotResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Instance snapshot remove operation not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InstanceRemoveAllSnapshotRequest.class,
                new Dispatch<InstanceRemoveAllSnapshotRequest, InstanceSnapshotResponse>() {
                    @Override
                    public ICancellable execute(InstanceRemoveAllSnapshotRequest request,
                            ResponseHandler<InstanceSnapshotResponse> handler)
                    {
                        ClassLoader cl = Thread.currentThread().getContextClassLoader();
                        try
                        {
                            ISnapshot operations = getInstanceSnapshotOperations();
                            if (operations != null)
                            {
                                Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                                return operations.removeAllSnapshots(request, handler);
                            }
                            else
                            {
                                InstanceSnapshotResponse response = new InstanceSnapshotResponse();
                                response.setStatus(Status.FAILURE);
                                response.setMessage("Instance snapshot remove all operation not supported");
                                handler.onResponse(response);
                                return null;
                            }
                        }
                        finally
                        {
                            Thread.currentThread().setContextClassLoader(cl);
                        }
                    }
                });

        register(InstanceRevertSnapshotRequest.class, new Dispatch<InstanceRevertSnapshotRequest, InstanceSnapshotResponse>() {
            @Override
            public ICancellable execute(InstanceRevertSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    ISnapshot operations = getInstanceSnapshotOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.revertSnapshot(request, handler);
                    }
                    else
                    {
                        InstanceSnapshotResponse response = new InstanceSnapshotResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Instance snapshot revert operation not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        //  Hotswap operations:
        //
        register(InstanceHotswapRequest.class, new Dispatch<InstanceHotswapRequest, InstanceResponse>() {
            @Override
            public ICancellable execute(InstanceHotswapRequest request, ResponseHandler<InstanceResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IHotSwap operations = getHotSwapOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.reconfigure(request, handler);
                    }
                    else
                    {
                        InstanceResponse response = new InstanceResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Instance hotswap operation not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        //  Cloud Properites:
        //
        register(CloudPropertyRequest.class, new Dispatch<CloudPropertyRequest, CloudPropertyResponse>() {
            @Override
            public ICancellable execute(CloudPropertyRequest request, ResponseHandler<CloudPropertyResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    ICloudProperties operations = getCloudPropertyOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.getCloudProperty(request, handler);
                    }
                    else
                    {
                        CloudPropertyResponse response = new CloudPropertyResponse();
                        response.setStatus(Status.COMPLETE);
                        response.setProperty(null); //  Denotes that cloud adapter doesn't support this function
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(StorageDetachableRequest.class, new Dispatch<StorageDetachableRequest, StorageResponse>() {
            @Override
            public ICancellable execute(StorageDetachableRequest request, ResponseHandler<StorageResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                ICloudProperties operations = getCloudPropertyOperations();
                if (operations != null)
                {
                    Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                    return operations.isDetachable(request, handler);
                }
                else
                {
                    StorageResponse response = new StorageResponse();
                    response.setStatus(Status.COMPLETE);
                    response.setDetachable(true); //  Default for most adapters
                    handler.onResponse(response);
                    return null;
                }
            }
        });

        //
        // ICredentialInfoSelector
        //
        register(CredentialSelectRequest.class, new Dispatch<CredentialSelectRequest, CredentialSelectResponse>() {
            @Override
            public ICancellable execute(CredentialSelectRequest request, ResponseHandler<CredentialSelectResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    ICredentialSelector operations = getCredentialSelectorOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.select(request, handler);
                    }
                    else
                    {
                        CredentialSelectResponse response = new CredentialSelectResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("Credential selector operation not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        //  IVPC
        //
        register(VPCCreateRequest.class, new Dispatch<VPCCreateRequest, VPCResponse>() {
            @Override
            public ICancellable execute(VPCCreateRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.create(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPCDeleteRequest.class, new Dispatch<VPCDeleteRequest, VPCResponse>() {
            @Override
            public ICancellable execute(VPCDeleteRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.delete(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(SubnetAddRequest.class, new Dispatch<SubnetAddRequest, VPCResponse>() {
            @Override
            public ICancellable execute(SubnetAddRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.subnetAdd(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(SubnetChangeRequest.class, new Dispatch<SubnetChangeRequest, VPCResponse>() {
            @Override
            public ICancellable execute(SubnetChangeRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.subnetChange(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(SubnetDeleteRequest.class, new Dispatch<SubnetDeleteRequest, VPCResponse>() {
            @Override
            public ICancellable execute(SubnetDeleteRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.subnetDelete(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPCSubnetsDeleteRequest.class, new Dispatch<VPCSubnetsDeleteRequest, VPCResponse>() {
            @Override
            public ICancellable execute(VPCSubnetsDeleteRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpcSubnetsDelete(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(DHCPOptionsCreateRequest.class, new Dispatch<DHCPOptionsCreateRequest, VPCResponse>() {
            @Override
            public ICancellable execute(DHCPOptionsCreateRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.dhcpOptionsCreate(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(DHCPOptionsDeleteRequest.class, new Dispatch<DHCPOptionsDeleteRequest, VPCResponse>() {
            @Override
            public ICancellable execute(DHCPOptionsDeleteRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.dhcpOptionsDelete(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(DHCPOptionsAssociateRequest.class, new Dispatch<DHCPOptionsAssociateRequest, VPCResponse>() {
            @Override
            public ICancellable execute(DHCPOptionsAssociateRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.dhcpOptionsAssociate(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPNGatewayConnectionCreateRequest.class, new Dispatch<VPNGatewayConnectionCreateRequest, VPCResponse>() {
            @Override
            public ICancellable execute(VPNGatewayConnectionCreateRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpnGatewayConnectionCreate(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPNGatewayConnectionDeleteRequest.class, new Dispatch<VPNGatewayConnectionDeleteRequest, VPCResponse>() {
            @Override
            public ICancellable execute(VPNGatewayConnectionDeleteRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpnGatewayConnectionDelete(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPNGatewayAttachRequest.class, new Dispatch<VPNGatewayAttachRequest, VPCResponse>() {
            @Override
            public ICancellable execute(VPNGatewayAttachRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpnGatewayAttach(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPNGatewayDetachRequest.class, new Dispatch<VPNGatewayDetachRequest, VPCResponse>() {
            @Override
            public ICancellable execute(VPNGatewayDetachRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpnGatewayDetach(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPNConnectionStateRequest.class, new Dispatch<VPNConnectionStateRequest, VPNConnectionStateResponse>() {
            @Override
            public ICancellable execute(VPNConnectionStateRequest request, ResponseHandler<VPNConnectionStateResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpnConnectionState(request, handler);
                    }
                    else
                    {
                        VPNConnectionStateResponse response = new VPNConnectionStateResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPNGatewayStateRequest.class, new Dispatch<VPNGatewayStateRequest, VPNGatewayStateResponse>() {
            @Override
            public ICancellable execute(VPNGatewayStateRequest request, ResponseHandler<VPNGatewayStateResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpnGatewayState(request, handler);
                    }
                    else
                    {
                        VPNGatewayStateResponse response = new VPNGatewayStateResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPNConnectionDeleteRequest.class, new Dispatch<VPNConnectionDeleteRequest, VPCResponse>() {
            @Override
            public ICancellable execute(VPNConnectionDeleteRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpnConnectionDelete(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPNGatewayDeleteRequest.class, new Dispatch<VPNGatewayDeleteRequest, VPCResponse>() {
            @Override
            public ICancellable execute(VPNGatewayDeleteRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpnGatewayDelete(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(CustomerGatewayDeleteRequest.class, new Dispatch<CustomerGatewayDeleteRequest, VPCResponse>() {
            @Override
            public ICancellable execute(CustomerGatewayDeleteRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.customerGatewayDelete(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(InternetGatewayDetachRequest.class, new Dispatch<InternetGatewayDetachRequest, VPCResponse>() {
            @Override
            public ICancellable execute(InternetGatewayDetachRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.internetGatewayDetach(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(VPNGatewayAttachmentsRequest.class, new Dispatch<VPNGatewayAttachmentsRequest, VPNGatewayAttachmentsResponse>() {
            @Override
            public ICancellable execute(VPNGatewayAttachmentsRequest request,
                    ResponseHandler<VPNGatewayAttachmentsResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.vpnGatewayAttachments(request, handler);
                    }
                    else
                    {
                        VPNGatewayAttachmentsResponse response = new VPNGatewayAttachmentsResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        register(RunningInstancesStopRequest.class, new Dispatch<RunningInstancesStopRequest, VPCResponse>() {
            @Override
            public ICancellable execute(RunningInstancesStopRequest request, ResponseHandler<VPCResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    IVPC operations = getVPCOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.runningInstancesStop(request, handler);
                    }
                    else
                    {
                        VPCResponse response = new VPCResponse();
                        response.setStatus(Status.FAILURE);
                        response.setMessage("VPC operations not supported");
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

        //
        //  Cloud Changed:
        //
        register(CloudChangeRequest.class, new Dispatch<CloudChangeRequest, CloudResponse>() {
            @Override
            public ICancellable execute(CloudChangeRequest request, ResponseHandler<CloudResponse> handler)
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                try
                {
                    ICloudChanged operations = getCloudChangedOperations();
                    if (operations != null)
                    {
                        Thread.currentThread().setContextClassLoader(operations.getClass().getClassLoader());
                        return operations.cloudChanged(request, handler);
                    }
                    else
                    {
                        CloudResponse response = new CloudResponse();
                        response.setStatus(Status.COMPLETE);
                        handler.onResponse(response);
                        return null;
                    }
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
        });

    }

    protected AsyncService getService()
    {
        return _service;
    }

    /*
     * This is the default handler for syncing VPCs.  Since only the EC2 adapter needs this, a default
     * implementation is provided here so that all the other adapters don't have to handle this message.
     * 
     * The EC2 adapter will override the getVPCSync() method above and provide it's own implementation
     * of the ISync<VPCSyncRequest,VPCSyncResponse> interface.
     */
    public class VPCSyncHandler implements ISync<VPCSyncRequest, VPCSyncResponse>
    {
        public VPCSyncHandler()
        {
        }

        @Override
        public ICancellable sync(VPCSyncRequest request, ResponseHandler<VPCSyncResponse> handler)
        {
            VPCSyncResponse response = new VPCSyncResponse();
            response.setStatus(Status.COMPLETE);
            handler.onResponse(response);
            return null;
        }
    }

    public class RepositorySyncHandler implements ISync<RepositorySyncRequest, RepositorySyncResponse>
    {
        public RepositorySyncHandler()
        {
        }

        @Override
        public ICancellable sync(RepositorySyncRequest request, ResponseHandler<RepositorySyncResponse> handler)
        {
            RepositorySyncResponse response = new RepositorySyncResponse();
            response.setStatus(Status.COMPLETE);
            handler.onResponse(response);
            return null;
        }
    }

}
