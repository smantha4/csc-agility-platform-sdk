package com.servicemesh.agility.sdk.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.servicemesh.agility.sdk.service.msgs.RegistrationRequest;
import com.servicemesh.agility.sdk.service.msgs.RegistrationResponse;
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.core.async.AsyncService;
import com.servicemesh.core.async.ResponseHandler;

public class AsyncTrackerTest
{

    AsyncTracker asyncTrackerInTest;

    private ServiceAdapter mockServiceAdapter = mock(ServiceAdapter.class);
    private BundleContext mockBundleContext = mock(BundleContext.class);
    private AsyncService mockServiceProvider = mock(AsyncService.class);
    private Appender mockAppender = mock(Appender.class);
    private ServiceReference<AsyncService> mockServiceRef = mock(ServiceReference.class);
    private RegistrationRequest mockRegistrationRequest = mock(RegistrationRequest.class);

    private Throwable expectedExceptionForGetRegistrationFailure =
            new RuntimeException("MOCK-EXCEPTION-GET-REGISTRATION-FAILURE");
    private Throwable expectedExceptionForSendRequestFailure = new RuntimeException("MOCK-EXCEPTION-SEND-REQUEST-FAILURE");
    private Throwable expectedExceptionForResponseHandlerFailure =
            new RuntimeException("MOCK-EXCEPTION-RESPONSE-HANDLER-ON-RESPONSE-FAILURE");
    private String mockVersion = "MOCK-VERSION";
    private String serviceTypeProperty = "service";
    private String serviceProviderTypeProperty = "framework";

    private ArgumentCaptor<LoggingEvent> logEventCaptor;
    private ArgumentCaptor<ResponseHandler> sendRequestResponseHandlerCaptor;
    private RegistrationResponse mockRegistrationResponse = mock(RegistrationResponse.class);

    @Before
    public void setupMocks()
    {
        asyncTrackerInTest = new AsyncTracker(mockServiceAdapter, mockBundleContext, mockVersion);
        Logger loggerForAsyncTracker = (Logger) LogManager.getLogger(AsyncTracker.class);
        when(mockAppender.getName()).thenReturn("MOCK-APPPENDER");
        loggerForAsyncTracker.setLevel(Level.INFO);
        loggerForAsyncTracker.addAppender(mockAppender);
        logEventCaptor = ArgumentCaptor.forClass(LoggingEvent.class);
    }

    @After
    public void resetMocks()
    {
        Logger loggerForAsyncTracker = (Logger) LogManager.getLogger(AsyncTracker.class);
        loggerForAsyncTracker.removeAllAppenders();
    }

    @Test
    public void TestForAdapterRegistrationFailuresShouldBeLogged()
    {
        Throwable actualException = null;
        when(mockBundleContext.getService(mockServiceRef)).thenReturn(mockServiceProvider);
        when(mockServiceRef.getProperty("serviceType")).thenReturn(serviceTypeProperty);
        when(mockServiceRef.getProperty("serviceProviderType")).thenReturn(serviceProviderTypeProperty);
        when(mockServiceRef.getProperty("version")).thenReturn(mockVersion);
        when(mockServiceAdapter.getRegistrationRequest()).thenThrow(expectedExceptionForGetRegistrationFailure);
        try
        {
            asyncTrackerInTest.addingService(mockServiceRef);
        }
        catch (Throwable th)
        {
            actualException = th;
        }
        verify(mockAppender).doAppend(logEventCaptor.capture());
        String actualLogMessage = (String) logEventCaptor.getValue().getMessage();
        Assert.assertTrue(actualLogMessage.contains(expectedExceptionForGetRegistrationFailure.getMessage()));
        Assert.assertTrue(expectedExceptionForGetRegistrationFailure.equals(actualException));
    }

    @Test
    public void TestForServiceProviderSendRequestFailuresShouldBeLogged()
    {
        Throwable actualException = null;
        when(mockBundleContext.getService(mockServiceRef)).thenReturn(mockServiceProvider);
        when(mockServiceRef.getProperty("serviceType")).thenReturn(serviceTypeProperty);
        when(mockServiceRef.getProperty("serviceProviderType")).thenReturn(serviceProviderTypeProperty);
        when(mockServiceRef.getProperty("version")).thenReturn(mockVersion);
        when(mockServiceAdapter.getRegistrationRequest()).thenReturn(mockRegistrationRequest);
        doThrow(expectedExceptionForSendRequestFailure).when(mockServiceAdapter).onRegistration(any(RegistrationResponse.class));
        when(mockServiceProvider.sendRequest(any(RegistrationRequest.class), any(ResponseHandler.class)))
                .thenThrow(expectedExceptionForSendRequestFailure);
        try
        {
            asyncTrackerInTest.addingService(mockServiceRef);
        }
        catch (Throwable th)
        {
            actualException = th;
        }
        verify(mockAppender).doAppend(logEventCaptor.capture());
        String actualLogMessage = (String) logEventCaptor.getValue().getMessage();
        Assert.assertTrue(actualLogMessage.contains(expectedExceptionForSendRequestFailure.getMessage()));
        Assert.assertTrue(expectedExceptionForSendRequestFailure.equals(actualException));
    }

    @Test
    public void TestForResponseHandlerFailuresShouldBeLogged()
    {
        long sendRequestReturnVal = 1;
        Throwable actualException = null;
        sendRequestResponseHandlerCaptor = ArgumentCaptor.forClass(ResponseHandler.class);
        when(mockBundleContext.getService(mockServiceRef)).thenReturn(mockServiceProvider);
        when(mockServiceRef.getProperty("serviceType")).thenReturn(serviceTypeProperty);
        when(mockServiceRef.getProperty("serviceProviderType")).thenReturn(serviceProviderTypeProperty);
        when(mockServiceRef.getProperty("version")).thenReturn(mockVersion);
        when(mockServiceAdapter.getRegistrationRequest()).thenReturn(mockRegistrationRequest);
        doThrow(expectedExceptionForResponseHandlerFailure).when(mockServiceAdapter)
                .onRegistration(any(RegistrationResponse.class));
        when(Long.valueOf(
                mockServiceProvider.sendRequest(any(RegistrationRequest.class), sendRequestResponseHandlerCaptor.capture())))
                        .thenReturn(sendRequestReturnVal);
        try
        {
            asyncTrackerInTest.addingService(mockServiceRef);
            ResponseHandler<RegistrationResponse> actualResponseHandler = sendRequestResponseHandlerCaptor.getValue();
            actualResponseHandler.onResponse(mockRegistrationResponse);
        }
        catch (Throwable th)
        {
            actualException = th;
        }
        verify(mockAppender).doAppend(logEventCaptor.capture());
        String actualLogMessage = (String) logEventCaptor.getValue().getMessage();
        Assert.assertTrue(actualLogMessage.contains(expectedExceptionForResponseHandlerFailure.getMessage()));
        Assert.assertTrue(expectedExceptionForResponseHandlerFailure.equals(actualException));
    }
}
