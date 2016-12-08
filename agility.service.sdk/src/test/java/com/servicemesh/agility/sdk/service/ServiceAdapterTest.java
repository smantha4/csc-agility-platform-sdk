package com.servicemesh.agility.sdk.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.servicemesh.agility.api.ApiRequest;
import com.servicemesh.agility.api.ApiResponse;
import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.AuthType;
import com.servicemesh.agility.api.Cloud;
import com.servicemesh.agility.api.Credential;
import com.servicemesh.agility.api.DeleteRequest;
import com.servicemesh.agility.api.Property;
import com.servicemesh.agility.api.Proxy;
import com.servicemesh.agility.api.ProxyType;
import com.servicemesh.agility.api.ProxyUsage;
import com.servicemesh.agility.api.ServiceProvider;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderRequest;
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.agility.sdk.service.spi.ServiceRegistry;
import com.servicemesh.core.async.AsyncService;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.Response;
import com.servicemesh.core.messaging.Status;

public class ServiceAdapterTest
{
    @Test
    public void testUnAuthGetProxy() throws Exception
    {
        ServiceProviderRequest mockSPR = mock(ServiceProviderRequest.class);
        List<Proxy> listOfProxies = new ArrayList<Proxy>();
        Proxy mockProxy = mock(Proxy.class);
        Proxy mockProxy2 = mock(Proxy.class);
        listOfProxies.add(mockProxy);
        listOfProxies.add(mockProxy2);
        List<com.servicemesh.io.proxy.Proxy> returnProxyList = new ArrayList<com.servicemesh.io.proxy.Proxy>();
        Cloud mockCloud = mock(Cloud.class);
        List<Cloud> mockCloudList = new ArrayList<Cloud>();
        mockCloudList.add(mockCloud);
        when(mockSPR.getClouds()).thenReturn(mockCloudList);
        when(mockCloud.getProxies()).thenReturn(listOfProxies);
        when(mockProxy.getProxyUsage()).thenReturn(ProxyUsage.PROXY_ALL);
        when(mockProxy.getProxyType()).thenReturn(ProxyType.PROXY_HTTP);
        when(mockProxy.getAuthType()).thenReturn(AuthType.AUTH_NONE);
        when(mockProxy.getHostname()).thenReturn("dummyHost");
        when(mockProxy2.getProxyUsage()).thenReturn(ProxyUsage.PROXY_MANAGER_CLOUD);
        when(mockProxy2.getProxyType()).thenReturn(ProxyType.PROXY_HTTP);
        when(mockProxy2.getAuthType()).thenReturn(AuthType.AUTH_NONE);
        when(mockProxy2.getHostname()).thenReturn("dummyHost2");

        returnProxyList = ServiceAdapter.getProxyConfig(mockSPR);
        Assert.assertTrue("Two Proxies should've been created", returnProxyList.size() == 2);
        Assert.assertTrue("The first Proxy was not given hostname correctly",
                returnProxyList.get(0).toString().equals("http//dummyHost->http//dummyHost2"));
        Assert.assertTrue("The second Proxy was not given hostname correctly",
                returnProxyList.get(1).toString().equals("http//dummyHost2"));
        Assert.assertTrue("The test Proxy was not given Proxy Type correctly",
                returnProxyList.get(0).getType().equals(com.servicemesh.io.proxy.ProxyType.HTTP_PROXY));
    }

    @Test
    public void testAuthGetProxy() throws Exception
    {
        ServiceProviderRequest mockSPR = mock(ServiceProviderRequest.class);
        List<Proxy> listOfProxies = new ArrayList<Proxy>();
        Proxy mockProxy = mock(Proxy.class);
        listOfProxies.add(mockProxy);
        List<com.servicemesh.io.proxy.Proxy> returnProxyList = new ArrayList<com.servicemesh.io.proxy.Proxy>();
        Credential mockProxyCred = mock(Credential.class);

        Cloud mockCloud = mock(Cloud.class);
        List<Cloud> mockCloudList = new ArrayList<Cloud>();
        mockCloudList.add(mockCloud);
        when(mockSPR.getClouds()).thenReturn(mockCloudList);
        when(mockCloud.getProxies()).thenReturn(listOfProxies);
        when(mockProxy.getProxyUsage()).thenReturn(ProxyUsage.PROXY_MANAGER_CLOUD);
        when(mockProxy.getProxyType()).thenReturn(ProxyType.PROXY_HTTPS);
        when(mockProxy.getAuthType()).thenReturn(AuthType.AUTH_CONFIGURED);
        when(mockProxy.getHostname()).thenReturn("dummyHost");
        when(mockProxy.getCredentials()).thenReturn(mockProxyCred);
        when(mockProxyCred.getPrivateKey()).thenReturn("dummyUser");
        when(mockProxyCred.getPublicKey()).thenReturn("dummyPass");

        returnProxyList = ServiceAdapter.getProxyConfig(mockSPR);
        Assert.assertTrue("One Proxy should've been created", returnProxyList.size() == 1);
        Assert.assertTrue("The test Proxy was not given hostname correctly",
                returnProxyList.get(0).getHostname().equals("dummyHost"));
        Assert.assertTrue("The test Proxy was not given Proxy Type correctly",
                returnProxyList.get(0).getType().equals(com.servicemesh.io.proxy.ProxyType.HTTPS_PROXY));
    }

    @Test
    public void testSessionGetProxy() throws Exception
    {
        ServiceProviderRequest mockSPR = mock(ServiceProviderRequest.class);
        List<Proxy> listOfProxies = new ArrayList<Proxy>();
        Proxy mockProxy = mock(Proxy.class);
        listOfProxies.add(mockProxy);
        List<com.servicemesh.io.proxy.Proxy> returnProxyList = new ArrayList<com.servicemesh.io.proxy.Proxy>();

        Cloud mockCloud = mock(Cloud.class);
        List<Cloud> mockCloudList = new ArrayList<Cloud>();
        mockCloudList.add(mockCloud);
        when(mockSPR.getClouds()).thenReturn(mockCloudList);
        when(mockCloud.getProxies()).thenReturn(listOfProxies);
        when(mockProxy.getProxyUsage()).thenReturn(ProxyUsage.PROXY_MANAGER_CLOUD);
        when(mockProxy.getProxyType()).thenReturn(ProxyType.PROXY_HTTP);
        when(mockProxy.getAuthType()).thenReturn(AuthType.AUTH_SESSION);
        when(mockProxy.getHostname()).thenReturn("dummyHost");

        returnProxyList = ServiceAdapter.getProxyConfig(mockSPR);
        Assert.assertTrue("One Proxy should've been created", returnProxyList.size() == 1);
        Assert.assertTrue("The test Proxy was not given hostname correctly",
                returnProxyList.get(0).getHostname().equals("dummyHost"));
        Assert.assertTrue("The test Proxy was not given Proxy Type correctly",
                returnProxyList.get(0).getType().equals(com.servicemesh.io.proxy.ProxyType.HTTP_PROXY));
    }

    @Test
    public void testSessionGetProxyWithNoCloudsPassedIn() throws Exception
    {
        ServiceProviderRequest mockSPR = mock(ServiceProviderRequest.class);
        List<Proxy> listOfProxies = new ArrayList<Proxy>();
        Proxy mockProxy = mock(Proxy.class);
        listOfProxies.add(mockProxy);
        List<com.servicemesh.io.proxy.Proxy> returnProxyList = new ArrayList<com.servicemesh.io.proxy.Proxy>();

        List<Cloud> mockCloudList = new ArrayList<Cloud>();
        when(mockSPR.getClouds()).thenReturn(mockCloudList);

        returnProxyList = ServiceAdapter.getProxyConfig(mockSPR);
        Assert.assertTrue("One Proxy should've been created", returnProxyList.size() == 0);

    }

    @Test
    public void testAssetCRUD() throws Exception
    {
        AsyncService mockAsync = mock(AsyncService.class);
        ServiceRegistry mockRegistry = mock(ServiceRegistry.class);
        when(mockRegistry.lookupApiService()).thenReturn(mockAsync);

        final List<ServiceProvider> providers = new ArrayList<ServiceProvider>();

        when(mockAsync.promise(any(Request.class))).thenAnswer(new Answer<Promise<Response>>() {
            @Override
            public Promise<Response> answer(InvocationOnMock invocation)
            {
                Object[] args = invocation.getArguments();
                Request request = (Request) args[0];
                if (request instanceof DeleteRequest)
                {
                    if (!providers.isEmpty())
                    {
                        Response response = new Response();
                        response.setStatus(Status.COMPLETE);
                        return Promise.pure(response);
                    }
                    else
                    {
                        Exception exc = new Exception("No providers => fail");
                        return Promise.pure(exc);
                    }
                }
                else if (!providers.isEmpty())
                {
                    ApiResponse response = new ApiResponse();
                    response.setStatus(Status.COMPLETE);
                    response.setAsset(providers.get(0));
                    return Promise.pure((Response) response);
                }
                Exception exc = new Exception("No providers => fail");
                return Promise.pure(exc);
            }
        });

        when(mockAsync.sequence(any(ApiRequest.class))).thenAnswer(new Answer<Promise<List<ApiResponse>>>() {
            @Override
            public Promise<List<ApiResponse>> answer(InvocationOnMock invocation)
            {
                List<ApiResponse> responses = new ArrayList<ApiResponse>();
                for (ServiceProvider provider : providers)
                {
                    ApiResponse response = new ApiResponse();
                    response.setStatus(Status.COMPLETE);
                    response.setAsset(provider);
                    responses.add(response);
                }
                return Promise.pure(responses);
            }
        });

        ServiceAdapter mockAdapter = mock(ServiceAdapter.class, Mockito.CALLS_REAL_METHODS);
        Whitebox.setInternalState(mockAdapter, "_registry", mockRegistry);

        // Try failure cases first
        ServiceProvider sp = new ServiceProvider();
        sp.setId(10);
        Asset parent = null;
        Promise<Asset> promise = mockAdapter.createAsset(sp, parent);
        completePromise(promise, providers);

        promise = mockAdapter.getAsset(ServiceAdapter.class.getName(), 1);
        completePromise(promise, providers);

        promise = mockAdapter.updateAsset(sp, parent);
        completePromise(promise, providers);

        promise = mockAdapter.deleteAsset(sp, parent);
        completePromise(promise, providers);

        List<Property> params = null;
        Promise<List<Asset>> promiseAssets = mockAdapter.getAssets(ServiceAdapter.class.getName(), params);
        completePromise(promiseAssets, providers);

        // Now success cases
        providers.add(sp);
        ServiceProvider sp2 = new ServiceProvider();
        sp2.setId(20);
        providers.add(sp2);

        promise = mockAdapter.createAsset(sp, parent);
        completePromise(promise, providers);

        promise = mockAdapter.getAsset(ServiceAdapter.class.getName(), 1);
        completePromise(promise, providers);

        promise = mockAdapter.updateAsset(sp, parent);
        completePromise(promise, providers);

        promise = mockAdapter.deleteAsset(sp, parent);
        completePromise(promise, providers);

        params = new ArrayList<Property>();
        promiseAssets = mockAdapter.getAssets(ServiceAdapter.class.getName(), params);
        completePromise(promiseAssets, providers);

        params.add(new Property());
        promiseAssets = mockAdapter.getAssets(ServiceAdapter.class.getName(), params);
        completePromise(promiseAssets, providers);
    }

    private void completePromise(Promise<?> promise, List<ServiceProvider> providers) throws Exception
    {
        boolean succeed = (!providers.isEmpty());
        try
        {
            Object obj = promise.get();
            if (succeed)
            {
                Assert.assertTrue(promise.isCompleted());

                if (obj instanceof ServiceProvider)
                {
                    ServiceProvider sp = (ServiceProvider) obj;
                    Assert.assertEquals(providers.get(0).getId(), sp.getId());
                }
                else
                {
                    @SuppressWarnings("unchecked")
                    List<ServiceProvider> sps = (List<ServiceProvider>) obj;
                    Assert.assertEquals(providers.size(), sps.size());
                    for (int i = 0; i < providers.size(); i++)
                    {
                        Assert.assertEquals(providers.get(i).getId(), sps.get(i).getId());
                    }
                }
            }
            else
            {
                Assert.assertTrue(promise.isFailed());
            }
        }
        catch (Throwable t)
        {
            if (succeed)
            {
                Assert.fail("Expected success, throwable=" + t);
            }
        }
    }
}
