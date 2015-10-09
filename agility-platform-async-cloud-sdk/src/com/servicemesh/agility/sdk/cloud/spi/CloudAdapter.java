package com.servicemesh.agility.sdk.cloud.spi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
import com.servicemesh.agility.sdk.cloud.spi.ICancellable;
import com.servicemesh.agility.sdk.cloud.impl.AsyncTracker;
import com.servicemesh.agility.sdk.cloud.msgs.*;
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


public abstract class CloudAdapter implements BundleActivator {

	private static final Logger logger = Logger.getLogger(CloudAdapter.class);
	public static final String AGILITY_API_VERSION = "3.1";
	
    protected AsyncService _service;
	protected ServiceTracker _asyncTracker;
    protected BundleContext _context;
	
	public CloudAdapter(Reactor reactor) {
		_service = new AsyncService(reactor);
		register();
	}

	public CloudAdapter(AsyncService service) {
		_service = service;
		register();
	}
	
	public abstract String getCloudType();
	public abstract String getAdapterName();
	public abstract String getAdapterVersion();
	
	public abstract RegistrationRequest getRegistrationRequest();
	public abstract void onRegistration(RegistrationResponse response);
	
	public abstract ISync<AddressRangeSyncRequest,AddressRangeSyncResponse> getAddressRangeSync();
	public abstract ISync<CloudSyncRequest,CloudSyncResponse> getCloudSync();
	public abstract ISync<CredentialSyncRequest,CredentialSyncResponse> getCredentialSync();
	public abstract ISync<LocationSyncRequest,LocationSyncResponse> getLocationSync();
	public abstract ISync<ImageSyncRequest,ImageSyncResponse> getImageSync();
	public abstract ISync<InstanceSyncRequest,InstanceSyncResponse> getInstanceSync();
	public abstract ISync<ModelSyncRequest,ModelSyncResponse> getModelSync();
	public abstract ISync<NetworkSyncRequest,NetworkSyncResponse> getNetworkSync();
	public abstract ISync<StorageSyncRequest,StorageSyncResponse> getStorageSync();
	
	/* This method needs to be overridden by the EC2 adapter to process VPC sync requests */
	public ISync<VPCSyncRequest,VPCSyncResponse> getVPCSync() {
		return new VPCSyncHandler();  //  Provide a default handler that just completes this phase of sync.
	}
	
	/* This method needs to be overridden by any adapter that wants to support Repository sync */
	public ISync<RepositorySyncRequest,RepositorySyncResponse> getRepositorySync() {
		return new RepositorySyncHandler();  //  Provide a default handler that just completes this phase of sync.
	}
	
	public abstract IInstance getInstanceOperations();
	public abstract IStorage getStorageOperations();
	public abstract ICredential getCredentialOperations();
	public abstract IImage getImageOperations();
	
	/*
	 * These operations are optional for a cloud adapter.  If not implemented, 
	 * a "not supported" error is returned.
	 */
	
	public IStorageSnapshot getSnapshotOperations() { return null; }
	public ISnapshot getInstanceSnapshotOperations() { return null;	}
	public IHotSwap getHotSwapOperations() { return null; }
	public ICloudProperties getCloudPropertyOperations() { return null; }
	public IVPC getVPCOperations() { return null; }
	public IAddress getAddressOperations() { return null; }
	public ICredentialSelector getCredentialSelectorOperations() { return null; }
	public ICloudChanged getCloudChangedOperations() { return null; }
	
	/*
	 * These methods handle the asset CRUD notifications.  A cloud adapter can override this
	 * method to return a class that implements IAssetNotification and processes the messages.
	 * A default implementation is provided below.
	 * 
	 * A cloud adapter will send a list of asset types it's interested in receiving CRUD event
	 * messages for when the adapter sends it's initial registration message.
	 *
	 */
	public IAssetNotification getAssetNotificationOperations() { return new IAssetNotification() {

			@Override
			public ICancellable preCreate(PreCreateRequest request, ResponseHandler<CloudResponse> handler) {
				CloudResponse response = new CloudResponse();
			    response.setStatus(Status.COMPLETE);
			    handler.onResponse(response);
			    return null;
			}
	
			@Override
			public ICancellable postCreate(PostCreateRequest request, ResponseHandler<CloudResponse> handler) {
				CloudResponse response = new CloudResponse();
			    response.setStatus(Status.COMPLETE);
			    handler.onResponse(response);
			    return null;
			}
	
			@Override
			public ICancellable preUpdate(PreUpdateRequest request, ResponseHandler<CloudResponse> handler) {
				CloudResponse response = new CloudResponse();
			    response.setStatus(Status.COMPLETE);
			    handler.onResponse(response);
			    return null;
			}
	
			@Override
			public ICancellable postUpdate(PostUpdateRequest request, ResponseHandler<CloudResponse> handler) {
				CloudResponse response = new CloudResponse();
			    response.setStatus(Status.COMPLETE);
			    handler.onResponse(response);
			    return null;
			}
	
			@Override
			public ICancellable preDelete(PreDeleteRequest request, ResponseHandler<CloudResponse> handler) {
				CloudResponse response = new CloudResponse();
			    response.setStatus(Status.COMPLETE);
			    handler.onResponse(response);
			    return null;
			}
	
			@Override
			public ICancellable postDelete(PostDeleteRequest request, ResponseHandler<CloudResponse> handler) {
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
		    switch (proxy.getProxyUsage()) {
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

                switch (proxy.getAuthType()) {
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

                if (firstProxy == null) {
                    firstProxy = nextProxy;
                }

                if (lastProxy != null) {
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

        switch (proxy.getProxyType()) {
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
		if(_context != null)
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
			catch (InvalidSyntaxException ex) {}
			if(references != null && references.length > 0)
				return (AsyncService)_context.getService(references[0]);
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
				if(response.getStatus() == Status.FAILURE)
				{
					callback.onError(new Exception(response.getMessage()));
					return false;
				}
				
				Asset asset = response.getAsset();
				if(asset instanceof Instance)
				{
					Instance instance = (Instance)asset;
					if(instance.getInstanceId().equals(instanceId) && instance.getCloud().getId() == cloudId)
					{
						callback.onResponse(instance);
						return false;
					}
				}
				
				if(response.getStatus() == Status.COMPLETE)
				{
					callback.onResponse(null);
					return false;
				}
				return true;
			}
	
			@Override
			public void onError(Request request, Throwable t) {
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
				if(response.getStatus() == Status.FAILURE)
				{
					callback.onError(new Exception(response.getMessage()));
					return false;
				}
				
				Asset asset = response.getAsset();
				callback.onResponse(asset);
				return false;
			}
	
			@Override
			public void onError(Request request, Throwable t) {
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
		update.setParent(parent);  //  Will be container for cloud
		
		apiService.sendRequest(update, new ResponseHandler<ApiResponse>() {
	
			@Override
			public boolean onResponse(ApiResponse response) 
			{
				if(response.getStatus() == Status.FAILURE)
				{
					callback.onError(new Exception(response.getMessage()));
					return false;
				}
				
				Asset asset = response.getAsset();
				if(asset instanceof Image)
				{
					Image newImage = (Image)asset;
					callback.onResponse(newImage);
					return false;
				}
				
				if(response.getStatus() == Status.COMPLETE)
				{
					callback.onResponse(null);
					return false;
				}
				return true;
			}
	
			@Override
			public void onError(Request request, Throwable t) {
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
			Hashtable<String,Object> metadata = new Hashtable<String,Object>();
			metadata.put("serviceType", "cloud");
			metadata.put("cloudType", getCloudType());
			metadata.put("version", AGILITY_API_VERSION);
			context.registerService(AsyncService.class.getName(),_service,metadata);
	
			// register a service tracker to wait for sdk service
			_asyncTracker = new ServiceTracker(context, AsyncService.class.getName(), new AsyncTracker(this,context, AGILITY_API_VERSION));
			_asyncTracker.open();
			
		}
		catch(Throwable t)
		{
			logger.error(t.getMessage(),t);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		
		if (_asyncTracker != null)
			_asyncTracker.close();
	}
	
	public ICancellable timerCreateRel(long interval, TimerHandler handler)
	{
		return new TimerCancel(_service.getReactor().timerCreateRel(interval,handler));
	}

	public interface Dispatch<REQ extends Request, RSP extends Response> {
		public ICancellable execute(REQ req, ResponseHandler<RSP> resp);
	};
	
	private <REQ extends Request, RSP extends Response> void register(Class<REQ> reqClass, final Dispatch<REQ,RSP> handler)
	{
		_service.registerRequest(reqClass, new RequestHandler<REQ>() {
			ICancellable _cancellable;
	
			@Override
			public void onRequest(final REQ request) {
					_cancellable = handler.execute(request, new ResponseHandler<RSP>() {
			
					@Override
					public boolean onResponse(RSP response) {
						_service.sendResponse(request,response);
						return false;
					}
	
					@Override
					public void onError(Request request, Throwable t) {
						_service.onError(request,t);
					}
	            	
	            });
			}
	
			@Override
			public void onCancel(long reqId) {
				if(_cancellable != null)
					_cancellable.cancel();
			}
		});		
	}
	
	private void register()
	{
		//
		// Credential Operations
		//
		
		register(CredentialCreateRequest.class,new Dispatch<CredentialCreateRequest,CredentialResponse>()
		{
				@Override
				public ICancellable execute(CredentialCreateRequest request, ResponseHandler<CredentialResponse> handler) {
			        return getCredentialOperations().create(request, handler);
				}
		});

		register(CredentialDeleteRequest.class,new Dispatch<CredentialDeleteRequest,CredentialResponse>()
		{
				@Override
				public ICancellable execute(CredentialDeleteRequest request, ResponseHandler<CredentialResponse> handler) {
			        return getCredentialOperations().delete(request, handler);
				}
		});

		//
		// Instance Operations
		//
		
		register(InstanceBootRequest.class,new Dispatch<InstanceBootRequest,InstanceResponse>()
		{
				@Override
				public ICancellable execute(InstanceBootRequest request, ResponseHandler<InstanceResponse> handler) {
					return getInstanceOperations().boot(request,  handler);
				}
		});

		register(InstanceCreateRequest.class,new Dispatch<InstanceCreateRequest,InstanceResponse>()
		{
				@Override
				public ICancellable execute(InstanceCreateRequest request, ResponseHandler<InstanceResponse> handler) {
					return getInstanceOperations().create(request,  handler);
				}
		});

		register(InstanceStartRequest.class,new Dispatch<InstanceStartRequest,InstanceResponse>()
		{
				@Override
				public ICancellable execute(InstanceStartRequest request, ResponseHandler<InstanceResponse> handler) {
					return getInstanceOperations().start(request,  handler);
				}
		});

		register(InstanceStopRequest.class,new Dispatch<InstanceStopRequest,InstanceResponse>()
		{
				@Override
				public ICancellable execute(InstanceStopRequest request, ResponseHandler<InstanceResponse> handler) {
					return getInstanceOperations().stop(request,  handler);
				}
		});

		register(InstanceRebootRequest.class,new Dispatch<InstanceRebootRequest,InstanceResponse>()
		{
				@Override
				public ICancellable execute(InstanceRebootRequest request, ResponseHandler<InstanceResponse> handler) {
					return getInstanceOperations().reboot(request,  handler);
				}
		});

		register(InstanceReleaseRequest.class,new Dispatch<InstanceReleaseRequest,InstanceResponse>()
		{
				@Override
				public ICancellable execute(InstanceReleaseRequest request, ResponseHandler<InstanceResponse> handler) {
					return getInstanceOperations().release(request,  handler);
				}
		});

		//
		// Image Operations
		//
		
		register(ImageCreateRequest.class,new Dispatch<ImageCreateRequest,ImageResponse>()
		{
				@Override
				public ICancellable execute(ImageCreateRequest request, ResponseHandler<ImageResponse> handler) {
					return getImageOperations().create(request,  handler);
				}
		});

		register(ImageDeleteRequest.class,new Dispatch<ImageDeleteRequest,ImageResponse>()
		{
				@Override
				public ICancellable execute(ImageDeleteRequest request, ResponseHandler<ImageResponse> handler) {
					return getImageOperations().delete(request,  handler);
				}
		});
		
		//
		// Storage Operations
		//
		
		register(StorageCreateRequest.class,new Dispatch<StorageCreateRequest,StorageResponse>()
		{
				@Override
				public ICancellable execute(StorageCreateRequest request, ResponseHandler<StorageResponse> handler) {
					return getStorageOperations().create(request,  handler);
				}
		});
		
		register(StorageAttachRequest.class,new Dispatch<StorageAttachRequest,StorageResponse>()
		{
				@Override
				public ICancellable execute(StorageAttachRequest request, ResponseHandler<StorageResponse> handler) {
					return getStorageOperations().attach(request,  handler);
				}
		});
		
		register(StorageDeleteRequest.class,new Dispatch<StorageDeleteRequest,StorageResponse>()
		{
				@Override
				public ICancellable execute(StorageDeleteRequest request, ResponseHandler<StorageResponse> handler) {
					return getStorageOperations().delete(request,  handler);
				}
		});
		
		register(StorageDetachRequest.class,new Dispatch<StorageDetachRequest,StorageResponse>()
		{
				@Override
				public ICancellable execute(StorageDetachRequest request, ResponseHandler<StorageResponse> handler) {
					return getStorageOperations().detach(request,  handler);
				}
		});
		
		register(StorageSnapshotCreateRequest.class,new Dispatch<StorageSnapshotCreateRequest,StorageSnapshotResponse>()
		{
				@Override
				public ICancellable execute(StorageSnapshotCreateRequest request, ResponseHandler<StorageSnapshotResponse> handler) {
					return getSnapshotOperations().create(request,  handler);
				}
		});
		
		register(StorageCreateFromSnapshotRequest.class,new Dispatch<StorageCreateFromSnapshotRequest,StorageResponse>()
		{
				@Override
				public ICancellable execute(StorageCreateFromSnapshotRequest request, ResponseHandler<StorageResponse> handler) {
					return getSnapshotOperations().createFromSnapshot(request,  handler);
				}
		});

		register(StorageSnapshotDeleteRequest.class,new Dispatch<StorageSnapshotDeleteRequest,StorageSnapshotResponse>()
		{
				@Override
				public ICancellable execute(StorageSnapshotDeleteRequest request, ResponseHandler<StorageSnapshotResponse> handler) {
					return getSnapshotOperations().delete(request,  handler);
				}
		});

		//
		// Sync
		//
		
		register(AddressRangeSyncRequest.class,new Dispatch<AddressRangeSyncRequest,AddressRangeSyncResponse>()
		{
				@Override
				public ICancellable execute(AddressRangeSyncRequest request, ResponseHandler<AddressRangeSyncResponse> handler) {
					ISync<AddressRangeSyncRequest,AddressRangeSyncResponse> isync = getAddressRangeSync();
					return isync.sync(request,  handler);
				}
		});

		register(CloudSyncRequest.class,new Dispatch<CloudSyncRequest,CloudSyncResponse>()
		{
				@Override
				public ICancellable execute(CloudSyncRequest request, ResponseHandler<CloudSyncResponse> handler) {
					ISync<CloudSyncRequest,CloudSyncResponse> isync = getCloudSync();
		            return isync.sync(request, handler);
				}
		});
		
		register(CredentialSyncRequest.class,new Dispatch<CredentialSyncRequest,CredentialSyncResponse>()
		{
				@Override
				public ICancellable execute(CredentialSyncRequest request, ResponseHandler<CredentialSyncResponse> handler) {
					ISync<CredentialSyncRequest,CredentialSyncResponse> isync = getCredentialSync();
		            return isync.sync(request, handler);
				}
		});

		register(LocationSyncRequest.class,new Dispatch<LocationSyncRequest,LocationSyncResponse>()
		{
				@Override
				public ICancellable execute(LocationSyncRequest request, ResponseHandler<LocationSyncResponse> handler) {
					ISync<LocationSyncRequest,LocationSyncResponse> isync = getLocationSync();
		            return isync.sync(request, handler);
				}
		});
		
		register(ImageSyncRequest.class,new Dispatch<ImageSyncRequest,ImageSyncResponse>()
		{
				@Override
				public ICancellable execute(ImageSyncRequest request, ResponseHandler<ImageSyncResponse> handler) {
					ISync<ImageSyncRequest,ImageSyncResponse> isync = getImageSync();
		            return isync.sync(request, handler);
				}
		});
		
		register(InstanceSyncRequest.class,new Dispatch<InstanceSyncRequest,InstanceSyncResponse>()
		{
				@Override
				public ICancellable execute(InstanceSyncRequest request, ResponseHandler<InstanceSyncResponse> handler) {
					ISync<InstanceSyncRequest,InstanceSyncResponse> isync = getInstanceSync();
		            return isync.sync(request, handler);
				}
		});
		
		register(ModelSyncRequest.class,new Dispatch<ModelSyncRequest,ModelSyncResponse>()
		{
				@Override
				public ICancellable execute(ModelSyncRequest request, ResponseHandler<ModelSyncResponse> handler) {
					ISync<ModelSyncRequest,ModelSyncResponse> isync = getModelSync();
		            return isync.sync(request, handler);
				}
		});
		
		register(NetworkSyncRequest.class,new Dispatch<NetworkSyncRequest,NetworkSyncResponse>()
		{
				@Override
				public ICancellable execute(NetworkSyncRequest request, ResponseHandler<NetworkSyncResponse> handler) {
					ISync<NetworkSyncRequest,NetworkSyncResponse> isync = getNetworkSync();
		            return isync.sync(request, handler);
				}
		});
		
		register(StorageSyncRequest.class,new Dispatch<StorageSyncRequest,StorageSyncResponse>()
		{
				@Override
				public ICancellable execute(StorageSyncRequest request, ResponseHandler<StorageSyncResponse> handler) {
					ISync<StorageSyncRequest,StorageSyncResponse> isync = getStorageSync();
		            return isync.sync(request, handler);
				}
		});
		
		register(VPCSyncRequest.class,new Dispatch<VPCSyncRequest,VPCSyncResponse>()
		{
				@Override
				public ICancellable execute(VPCSyncRequest request, ResponseHandler<VPCSyncResponse> handler) {
					ISync<VPCSyncRequest,VPCSyncResponse> isync = getVPCSync();
		            return isync.sync(request, handler);
				}
		});
		
		register(RepositorySyncRequest.class,new Dispatch<RepositorySyncRequest,RepositorySyncResponse>()
		{
				@Override
				public ICancellable execute(RepositorySyncRequest request, ResponseHandler<RepositorySyncResponse> handler) {
					ISync<RepositorySyncRequest,RepositorySyncResponse> isync = getRepositorySync();
		            return isync.sync(request, handler);
				}
		});


		//
		// Asset Notification operations
		//
		register(PreCreateRequest.class, new Dispatch<PreCreateRequest, CloudResponse>() 
		{
				@Override
				public ICancellable execute(PreCreateRequest request, ResponseHandler<CloudResponse> handler) {
					IAssetNotification notification = getAssetNotificationOperations();
					return notification.preCreate(request, handler);
				}
		});

		register(PostCreateRequest.class, new Dispatch<PostCreateRequest, CloudResponse>() 
		{
				@Override
				public ICancellable execute(PostCreateRequest request, ResponseHandler<CloudResponse> handler) {
					IAssetNotification notification = getAssetNotificationOperations();
					return notification.postCreate(request, handler);
				}
		});

		register(PreUpdateRequest.class, new Dispatch<PreUpdateRequest, CloudResponse>()
		{
			@Override
			public ICancellable execute(PreUpdateRequest request, ResponseHandler<CloudResponse> handler) {
				IAssetNotification notification = getAssetNotificationOperations();
				return notification.preUpdate(request, handler);
			}
		});

		register(PostUpdateRequest.class, new Dispatch<PostUpdateRequest, CloudResponse>() 
		{
			@Override
			public ICancellable execute(PostUpdateRequest request, ResponseHandler<CloudResponse> handler) {
				IAssetNotification notification = getAssetNotificationOperations();
				return notification.postUpdate(request, handler);
			}
		});

		register(PreDeleteRequest.class, new Dispatch<PreDeleteRequest, CloudResponse>() 
		{
			@Override
			public ICancellable execute(PreDeleteRequest request, ResponseHandler<CloudResponse> handler) {
				IAssetNotification notification = getAssetNotificationOperations();
				return notification.preDelete(request, handler);
			}
		});

		register(PostDeleteRequest.class, new Dispatch<PostDeleteRequest, CloudResponse>() 
		{
			@Override
			public ICancellable execute(PostDeleteRequest request, ResponseHandler<CloudResponse> handler) {
				IAssetNotification notification = getAssetNotificationOperations();
				return notification.postDelete(request, handler);
			}
		});
		
		//
		//  Instance snapshot operations:
		//
		register(InstanceCreateSnapshotRequest.class,new Dispatch<InstanceCreateSnapshotRequest,InstanceSnapshotResponse>()
		{
			@Override
			public ICancellable execute(InstanceCreateSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler) {
				ISnapshot operations = getInstanceSnapshotOperations();
				if (operations != null)
					return operations.createSnapshot(request,  handler);
				else
				{
					InstanceSnapshotResponse response = new InstanceSnapshotResponse();
					response.setStatus(Status.FAILURE);
					response.setMessage("Instance snapshot create operation not supported");
					handler.onResponse(response);
					return null;
				}
			}
		});
		
		register(InstanceUpdateSnapshotRequest.class,new Dispatch<InstanceUpdateSnapshotRequest,InstanceSnapshotResponse>()
		{
			@Override
			public ICancellable execute(InstanceUpdateSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler) {
					ISnapshot operations = getInstanceSnapshotOperations();
					if (operations != null)
						return operations.updateSnapshot(request,  handler);
					else
					{
						InstanceSnapshotResponse response = new InstanceSnapshotResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Instance snapshot update operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(InstanceRemoveSnapshotRequest.class,new Dispatch<InstanceRemoveSnapshotRequest,InstanceSnapshotResponse>()
		{
			@Override
			public ICancellable execute(InstanceRemoveSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler) {
					ISnapshot operations = getInstanceSnapshotOperations();
					if (operations != null)
						return operations.removeSnapshot(request,  handler);
					else
					{
						InstanceSnapshotResponse response = new InstanceSnapshotResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Instance snapshot remove operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(InstanceRemoveAllSnapshotRequest.class,new Dispatch<InstanceRemoveAllSnapshotRequest,InstanceSnapshotResponse>()
		{
			@Override
			public ICancellable execute(InstanceRemoveAllSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler) {
					ISnapshot operations = getInstanceSnapshotOperations();
					if (operations != null)
						return operations.removeAllSnapshots(request,  handler);
					else
					{
						InstanceSnapshotResponse response = new InstanceSnapshotResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Instance snapshot remove all operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(InstanceRevertSnapshotRequest.class,new Dispatch<InstanceRevertSnapshotRequest,InstanceSnapshotResponse>()
		{
			@Override
			public ICancellable execute(InstanceRevertSnapshotRequest request, ResponseHandler<InstanceSnapshotResponse> handler) {
					ISnapshot operations = getInstanceSnapshotOperations();
					if (operations != null)
						return operations.revertSnapshot(request,  handler);
					else
					{
						InstanceSnapshotResponse response = new InstanceSnapshotResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Instance snapshot revert operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		//
		//  Hotswap operations:
		//
		register(InstanceHotswapRequest.class,new Dispatch<InstanceHotswapRequest,InstanceResponse>()
		{
			@Override
			public ICancellable execute(InstanceHotswapRequest request, ResponseHandler<InstanceResponse> handler) {
					IHotSwap operations = getHotSwapOperations();
					if (operations != null)
						return operations.reconfigure(request, handler);
					else
					{
						InstanceResponse response = new InstanceResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Instance hotswap operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		//
		//  Cloud Properites:
		//
		register(CloudPropertyRequest.class,new Dispatch<CloudPropertyRequest,CloudPropertyResponse>()
		{
			@Override
			public ICancellable execute(CloudPropertyRequest request, ResponseHandler<CloudPropertyResponse> handler) {
					ICloudProperties operations = getCloudPropertyOperations();
					if (operations != null)
						return operations.getCloudProperty(request, handler);
					else
					{
						CloudPropertyResponse response = new CloudPropertyResponse();
						response.setStatus(Status.COMPLETE);
						response.setProperty(null);  //  Denotes that cloud adapter doesn't support this function
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(StorageDetachableRequest.class,new Dispatch<StorageDetachableRequest,StorageResponse>()
		{
			@Override
			public ICancellable execute(StorageDetachableRequest request, ResponseHandler<StorageResponse> handler) {
					ICloudProperties operations = getCloudPropertyOperations();
					if (operations != null)
						return operations.isDetachable(request, handler);
					else
					{
						StorageResponse response = new StorageResponse();
						response.setStatus(Status.COMPLETE);
						response.setDetachable(true);  //  Default for most adapters
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		//
		// Address 
		//
		register(AddressAllocateRequest.class,new Dispatch<AddressAllocateRequest,AddressResponse>()
		{
			@Override
			public ICancellable execute(AddressAllocateRequest request, ResponseHandler<AddressResponse> handler) {
					IAddress operations = getAddressOperations();
					if (operations != null)
						return operations.allocate(request, handler);
					else
					{
						AddressResponse response = new AddressResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Adapter Address operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(AddressReleaseRequest.class,new Dispatch<AddressReleaseRequest,AddressResponse>()
		{
			@Override
			public ICancellable execute(AddressReleaseRequest request, ResponseHandler<AddressResponse> handler) {
					IAddress operations = getAddressOperations();
					if (operations != null)
						return operations.release(request, handler);
					else
					{
						AddressResponse response = new AddressResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Adapter Address operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(AddressAssociateRequest.class,new Dispatch<AddressAssociateRequest,AddressResponse>()
		{
			@Override
			public ICancellable execute(AddressAssociateRequest request, ResponseHandler<AddressResponse> handler) {
					IAddress operations = getAddressOperations();
					if (operations != null)
						return operations.associate(request, handler);
					else
					{
						AddressResponse response = new AddressResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Adapter Address operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(AddressDisassociateRequest.class,new Dispatch<AddressDisassociateRequest,AddressResponse>()
		{
			@Override
			public ICancellable execute(AddressDisassociateRequest request, ResponseHandler<AddressResponse> handler) {
					IAddress operations = getAddressOperations();
					if (operations != null)
						return operations.disassociate(request, handler);
					else
					{
						AddressResponse response = new AddressResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Adapter Address operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		//
		// ICredentialInfoSelector
		//
		register(CredentialSelectRequest.class,new Dispatch<CredentialSelectRequest,CredentialSelectResponse>()
		{
			@Override
			public ICancellable execute(CredentialSelectRequest request, ResponseHandler<CredentialSelectResponse> handler) {
					ICredentialSelector operations = getCredentialSelectorOperations();
					if (operations != null)
						return operations.select(request, handler);
					else
					{
						CredentialSelectResponse response = new CredentialSelectResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("Credential selector operation not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		
		//
		//  IVPC
		//
		register(VPCCreateRequest.class,new Dispatch<VPCCreateRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(VPCCreateRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.create(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(VPCDeleteRequest.class,new Dispatch<VPCDeleteRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(VPCDeleteRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.delete(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(SubnetAddRequest.class,new Dispatch<SubnetAddRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(SubnetAddRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.subnetAdd(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(SubnetChangeRequest.class,new Dispatch<SubnetChangeRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(SubnetChangeRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.subnetChange(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(SubnetDeleteRequest.class,new Dispatch<SubnetDeleteRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(SubnetDeleteRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.subnetDelete(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});

		register(VPCSubnetsDeleteRequest.class,new Dispatch<VPCSubnetsDeleteRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(VPCSubnetsDeleteRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.vpcSubnetsDelete(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});

		register(DHCPOptionsCreateRequest.class,new Dispatch<DHCPOptionsCreateRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(DHCPOptionsCreateRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.dhcpOptionsCreate(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(DHCPOptionsDeleteRequest.class,new Dispatch<DHCPOptionsDeleteRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(DHCPOptionsDeleteRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.dhcpOptionsDelete(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(DHCPOptionsAssociateRequest.class,new Dispatch<DHCPOptionsAssociateRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(DHCPOptionsAssociateRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.dhcpOptionsAssociate(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(VPNGatewayConnectionCreateRequest.class,new Dispatch<VPNGatewayConnectionCreateRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(VPNGatewayConnectionCreateRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.vpnGatewayConnectionCreate(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(VPNGatewayConnectionDeleteRequest.class,new Dispatch<VPNGatewayConnectionDeleteRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(VPNGatewayConnectionDeleteRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.vpnGatewayConnectionDelete(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(VPNGatewayAttachRequest.class,new Dispatch<VPNGatewayAttachRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(VPNGatewayAttachRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.vpnGatewayAttach(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(VPNGatewayDetachRequest.class,new Dispatch<VPNGatewayDetachRequest,VPCResponse>()
		{
			@Override
			public ICancellable execute(VPNGatewayDetachRequest request, ResponseHandler<VPCResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.vpnGatewayDetach(request, handler);
					else
					{
						VPCResponse response = new VPCResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(VPNConnectionStateRequest.class,new Dispatch<VPNConnectionStateRequest,VPNConnectionStateResponse>()
		{
			@Override
			public ICancellable execute(VPNConnectionStateRequest request, ResponseHandler<VPNConnectionStateResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.vpnConnectionState(request, handler);
					else
					{
						VPNConnectionStateResponse response = new VPNConnectionStateResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});
		
		register(VPNGatewayStateRequest.class,new Dispatch<VPNGatewayStateRequest,VPNGatewayStateResponse>()
		{
			@Override
			public ICancellable execute(VPNGatewayStateRequest request, ResponseHandler<VPNGatewayStateResponse> handler) {
					IVPC operations = getVPCOperations();
					if (operations != null)
						return operations.vpnGatewayState(request, handler);
					else
					{
						VPNGatewayStateResponse response = new VPNGatewayStateResponse();
						response.setStatus(Status.FAILURE);
						response.setMessage("VPC operations not supported");
						handler.onResponse(response);
						return null;
					}
			}
		});

		register(VPNConnectionDeleteRequest.class,new Dispatch<VPNConnectionDeleteRequest,VPCResponse>() 
		{
			@Override
			public ICancellable execute(VPNConnectionDeleteRequest request, ResponseHandler<VPCResponse> handler) {
				IVPC operations = getVPCOperations();
				if (operations != null)
					return operations.vpnConnectionDelete(request, handler);
				else
				{
					VPCResponse response = new VPCResponse();
					response.setStatus(Status.FAILURE);
					response.setMessage("VPC operations not supported");
					handler.onResponse(response);
					return null;
				}
			}
		});
		
		register(VPNGatewayDeleteRequest.class,new Dispatch<VPNGatewayDeleteRequest,VPCResponse>() 
		{
			@Override
			public ICancellable execute(VPNGatewayDeleteRequest request, ResponseHandler<VPCResponse> handler) {
				IVPC operations = getVPCOperations();
				if (operations != null)
					return operations.vpnGatewayDelete(request, handler);
				else
				{
					VPCResponse response = new VPCResponse();
					response.setStatus(Status.FAILURE);
					response.setMessage("VPC operations not supported");
					handler.onResponse(response);
					return null;
				}
			}
		});

		register(CustomerGatewayDeleteRequest.class,new Dispatch<CustomerGatewayDeleteRequest,VPCResponse>() 
		{
			@Override
			public ICancellable execute(CustomerGatewayDeleteRequest request, ResponseHandler<VPCResponse> handler) {
				IVPC operations = getVPCOperations();
				if (operations != null)
					return operations.customerGatewayDelete(request, handler);
				else
				{
					VPCResponse response = new VPCResponse();
					response.setStatus(Status.FAILURE);
					response.setMessage("VPC operations not supported");
					handler.onResponse(response);
					return null;
				}
			}
		});

		register(InternetGatewayDetachRequest.class,new Dispatch<InternetGatewayDetachRequest,VPCResponse>() 
		{
			@Override
			public ICancellable execute(InternetGatewayDetachRequest request, ResponseHandler<VPCResponse> handler) {
				IVPC operations = getVPCOperations();
				if (operations != null)
					return operations.internetGatewayDetach(request, handler);
				else
				{
					VPCResponse response = new VPCResponse();
					response.setStatus(Status.FAILURE);
					response.setMessage("VPC operations not supported");
					handler.onResponse(response);
					return null;
				}
			}
		});
		
		register(VPNGatewayAttachmentsRequest.class,new Dispatch<VPNGatewayAttachmentsRequest,VPNGatewayAttachmentsResponse>() 
		{
			@Override
			public ICancellable execute(VPNGatewayAttachmentsRequest request, ResponseHandler<VPNGatewayAttachmentsResponse> handler) {
				IVPC operations = getVPCOperations();
				if (operations != null)
					return operations.vpnGatewayAttachments(request, handler);
				else
				{
					VPNGatewayAttachmentsResponse response = new VPNGatewayAttachmentsResponse();
					response.setStatus(Status.FAILURE);
					response.setMessage("VPC operations not supported");
					handler.onResponse(response);
					return null;
				}
			}
		});
		
		

		register(RunningInstancesStopRequest.class,new Dispatch<RunningInstancesStopRequest,VPCResponse>() 
		{
			@Override
			public ICancellable execute(RunningInstancesStopRequest request, ResponseHandler<VPCResponse> handler) {
				IVPC operations = getVPCOperations();
				if (operations != null)
					return operations.runningInstancesStop(request, handler);
				else
				{
					VPCResponse response = new VPCResponse();
					response.setStatus(Status.FAILURE);
					response.setMessage("VPC operations not supported");
					handler.onResponse(response);
					return null;
				}
			}
		});

		//
		//  Cloud Changed:
		//
		register(CloudChangeRequest.class,new Dispatch<CloudChangeRequest,CloudResponse>()
		{
			@Override
			public ICancellable execute(CloudChangeRequest request, ResponseHandler<CloudResponse> handler) {
					ICloudChanged operations = getCloudChangedOperations();
					if (operations != null)
						return operations.cloudChanged(request, handler);
					else
					{
						CloudResponse response = new CloudResponse();
						response.setStatus(Status.COMPLETE);
						handler.onResponse(response);
						return null;
					}
			}
		});

	}


	protected AsyncService getService() {
		return _service;
	}
	
	/*
	 * This is the default handler for syncing VPCs.  Since only the EC2 adapter needs this, a default
	 * implementation is provided here so that all the other adapters don't have to handle this message.
	 * 
	 * The EC2 adapter will override the getVPCSync() method above and provide it's own implementation
	 * of the ISync<VPCSyncRequest,VPCSyncResponse> interface.
	 */
	public class VPCSyncHandler implements ISync<VPCSyncRequest,VPCSyncResponse> {
	    public VPCSyncHandler() {}
	    
	    @Override
	    public ICancellable sync(VPCSyncRequest request,ResponseHandler<VPCSyncResponse> handler) 
	    {
	    	VPCSyncResponse response = new VPCSyncResponse();
			response.setStatus(Status.COMPLETE);
			handler.onResponse(response);
			return null;
	    }
	}
	
	
	public class RepositorySyncHandler implements ISync<RepositorySyncRequest,RepositorySyncResponse> {
	    public RepositorySyncHandler() {}
	    
	    @Override
	    public ICancellable sync(RepositorySyncRequest request,ResponseHandler<RepositorySyncResponse> handler) 
	    {
	    	RepositorySyncResponse response = new RepositorySyncResponse();
			response.setStatus(Status.COMPLETE);
			handler.onResponse(response);
			return null;
	    }
	}
	
}
