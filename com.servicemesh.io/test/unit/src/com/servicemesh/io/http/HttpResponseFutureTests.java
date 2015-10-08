/**
 *              COPYRIGHT (C) 2008-2014 SERVICEMESH, INC.
 *                        ALL RIGHTS RESERVED.
 *                   CONFIDENTIAL AND PROPRIETARY.
 *
 *  ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS
 *  (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT
 *  ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.
 *  ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE
 *  AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED
 *  IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY
 *  OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY
 *  SERVICEMESH IS STRICTLY PROHIBITED.
 */

package com.servicemesh.io.http;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.servicemesh.io.http.impl.DefaultHttpResponse;
import com.servicemesh.io.http.impl.DefaultHttpResponseFuture;

public class HttpResponseFutureTests
{
    @Test
    public void testSuccess()
        throws Exception
    {
        DefaultHttpResponseFuture<IHttpResponse> defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        IHttpResponseFuture<IHttpResponse> future = defaultFuture;
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());

        IHttpResponse testResponse = new DefaultHttpResponse();
        HttpStatus testStatus = new HttpStatus(HttpVersion.HTTP_1_1, 200, null);
        ((DefaultHttpResponse)testResponse).setStatus(testStatus);
        ((DefaultHttpResponse)testResponse).setContent("success".getBytes());

        IHttpResponse testResponse2 = new DefaultHttpResponse();
        HttpStatus testStatus2 = new HttpStatus(HttpVersion.HTTP_1_1, 404, null);
        ((DefaultHttpResponse)testResponse2).setStatus(testStatus2);
        ((DefaultHttpResponse)testResponse2).setContent("not found".getBytes());

        Assert.assertTrue(defaultFuture.set(testResponse));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        IHttpResponse responseBack = future.get();
        Assert.assertNotNull(responseBack);
        Assert.assertEquals(200, responseBack.getStatusCode());
        Assert.assertEquals("success", responseBack.getContent());

        try {
            responseBack = future.get(100, TimeUnit.MILLISECONDS);
            Assert.assertNotNull(responseBack);
            Assert.assertEquals(200, responseBack.getStatusCode());
            Assert.assertEquals("success", responseBack.getContent());
        } catch (TimeoutException ex) {
            Assert.fail("Timeout fired when retrieving a set future");
        }

        // Shouldn't be able to complete twice
        Assert.assertFalse(defaultFuture.set(testResponse2));
        responseBack = future.get();
        Assert.assertNotNull(responseBack);
        Assert.assertEquals(200, responseBack.getStatusCode());
        Assert.assertEquals("success", responseBack.getContent());

        // Test completion after cancel
        defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        future = defaultFuture;
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(future.cancel(true));
        Assert.assertFalse(defaultFuture.set(testResponse));
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());

        try {
            future.get();
            Assert.fail("Retrieved result after cancellation");
        } catch (CancellationException ex) {
            Assert.assertEquals("Task was cancelled.", ex.getMessage());
        }

        // Test completion after failure
        defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        future = defaultFuture;
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        defaultFuture.setException(new IllegalArgumentException("bad parameters"));
        Assert.assertFalse(defaultFuture.set(testResponse));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());

        try {
            future.get();
            Assert.fail("Retrieved result after failure");
        } catch (ExecutionException ex) {
            Assert.assertTrue(ex.getCause() instanceof IllegalArgumentException);
            Assert.assertEquals("bad parameters", ex.getCause().getMessage());
        }
    }

    @Test
    public void testAsyncSuccess()
        throws Exception
    {
        final IHttpResponse testResponse = new DefaultHttpResponse();
        HttpStatus testStatus = new HttpStatus(HttpVersion.HTTP_1_1, 200, null);
        ((DefaultHttpResponse)testResponse).setStatus(testStatus);
        ((DefaultHttpResponse)testResponse).setContent("success".getBytes());
        final DefaultHttpResponseFuture<IHttpResponse> futureComplete = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        final Thread completeThread = new Thread() {
            @Override
            public void run()
            {
                try {
                    Thread.sleep(100);
                    futureComplete.set(testResponse);
                } catch (final InterruptedException boom) {
                }
            }
        };

        completeThread.setDaemon(true);
        completeThread.start();
        Assert.assertSame(testResponse, futureComplete.get());
        Assert.assertTrue(futureComplete.isDone());
        Assert.assertFalse(futureComplete.isCancelled());

        final DefaultHttpResponseFuture<IHttpResponse> futureCompleteTimeout = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        final Thread completeThreadTimeout = new Thread() {
            @Override
            public void run()
            {
                try {
                    Thread.sleep(100);
                    futureCompleteTimeout.set(testResponse);
                } catch (final InterruptedException boom) {
                }
            }
        };

        completeThreadTimeout.setDaemon(true);
        completeThreadTimeout.start();
        Assert.assertSame(testResponse, futureCompleteTimeout.get(5, TimeUnit.SECONDS));
        Assert.assertTrue(futureCompleteTimeout.isDone());
        Assert.assertFalse(futureCompleteTimeout.isCancelled());
    }

    @Test
    public void testCancel()
        throws Exception
    {
        IHttpResponseFuture<IHttpResponse> future = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(future.cancel(true));
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());

        try {
            future.get();
            Assert.fail("Retrieved result after cancellation");
        } catch (CancellationException ex) {
            Assert.assertEquals("Task was cancelled.", ex.getMessage());
        }

        try {
            future.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Retrieved result after cancellation");
        } catch (CancellationException ex) {
            Assert.assertEquals("Task was cancelled.", ex.getMessage());
        }

        // Shouldn't be able to cancel twice
        Assert.assertFalse(future.cancel(true));
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());

        // Test cancel after successful completion
        DefaultHttpResponseFuture<IHttpResponse> defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        future = defaultFuture;
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(defaultFuture.set(new DefaultHttpResponse()));
        Assert.assertFalse(future.cancel(true));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());

        // Test cancel after failure
        defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        future = defaultFuture;
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(defaultFuture.setException(new IllegalArgumentException("bad parameters")));
        Assert.assertFalse(future.cancel(true));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void testAsyncCancel()
        throws Exception
    {
        final IHttpResponseFuture<IHttpResponse> futureCancel = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        final Thread cancelThread = new Thread() {
            @Override
            public void run()
            {
                try {
                    Thread.sleep(100);
                    futureCancel.cancel(true);
                } catch (final InterruptedException ex) {
                }
            }
        };

        cancelThread.setDaemon(true);
        cancelThread.start();

        try {
            futureCancel.get();
            Assert.fail("Retrieved result after cancellation");
        } catch (CancellationException ex) {
            Assert.assertEquals("Task was cancelled.", ex.getMessage());
        }

        Assert.assertTrue(futureCancel.isDone());
        Assert.assertTrue(futureCancel.isCancelled());

        final IHttpResponseFuture<IHttpResponse> futureCancelTimeout = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        final Thread cancelThreadTimeout = new Thread() {
            @Override
            public void run()
            {
                try {
                    Thread.sleep(100);
                    futureCancelTimeout.cancel(true);
                } catch (final InterruptedException ex) {
                }
            }
        };

        cancelThreadTimeout.setDaemon(true);
        cancelThreadTimeout.start();

        try {
            futureCancelTimeout.get(5, TimeUnit.SECONDS);
            Assert.fail("Retrieved result after cancellation");
        } catch (CancellationException ex) {
            Assert.assertEquals("Task was cancelled.", ex.getMessage());
        }

        Assert.assertTrue(futureCancelTimeout.isDone());
        Assert.assertTrue(futureCancelTimeout.isCancelled());
    }

    @Test
    public void testFail()
        throws Exception
    {
        DefaultHttpResponseFuture<IHttpResponse> defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        IHttpResponseFuture<IHttpResponse> future = defaultFuture;
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        defaultFuture.setException(new IllegalArgumentException("bad parameters"));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());

        try {
            future.get();
            Assert.fail("Retrieved result after failure");
        } catch (ExecutionException ex) {
            Assert.assertTrue(ex.getCause() instanceof IllegalArgumentException);
            Assert.assertEquals("bad parameters", ex.getCause().getMessage());
        }

        try {
            future.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Retrieved result after failure");
        } catch (ExecutionException ex) {
            Assert.assertTrue(ex.getCause() instanceof IllegalArgumentException);
            Assert.assertEquals("bad parameters", ex.getCause().getMessage());
        }

        // Second fail should not take
        Assert.assertFalse(defaultFuture.setException(new IllegalArgumentException("invalid parms")));
        try {
            future.get();
            Assert.fail("Retrieved result after failure");
        } catch (ExecutionException ex) {
            Assert.assertTrue(ex.getCause() instanceof IllegalArgumentException);
            Assert.assertEquals("bad parameters", ex.getCause().getMessage());
        }

        // Test fail after successful completion
        defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        future = defaultFuture;
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(defaultFuture.set(new DefaultHttpResponse()));
        Assert.assertFalse(defaultFuture.setException(new IllegalArgumentException("bad parameters")));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        Assert.assertNotNull(future.get());

        // Test failure after cancel
        defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        future = defaultFuture;
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(future.cancel(true));
        Assert.assertFalse(defaultFuture.setException(new IllegalArgumentException("bad parameters")));
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());

        try {
            future.get();
            Assert.fail("Retrieved result after cancellation");
        } catch (CancellationException ex) {
            Assert.assertEquals("Task was cancelled.", ex.getMessage());
        }
    }

    @Test
    public void testAsyncFail()
        throws Exception
    {
        final DefaultHttpResponseFuture<IHttpResponse> futureFail = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        final Exception failure = new IllegalArgumentException("rogue parameters");
        final Thread failThread = new Thread() {
            @Override
            public void run()
            {
                try {
                    Thread.sleep(100);
                    futureFail.setException(failure);
                } catch (final InterruptedException ex) {
                }
            }
        };

        failThread.setDaemon(true);
        failThread.start();

        try {
            futureFail.get();
            Assert.fail("Successfully retrieved future value with failure set");
        } catch (final ExecutionException ex) {
            Assert.assertTrue(ex.getCause() instanceof IllegalArgumentException);
            Assert.assertEquals("rogue parameters", ex.getCause().getMessage());
        }

        Assert.assertTrue(futureFail.isDone());
        Assert.assertFalse(futureFail.isCancelled());

        final DefaultHttpResponseFuture<IHttpResponse> futureFailTimeout = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        final Thread failThreadTimeout = new Thread() {
            @Override
            public void run()
            {
                try {
                    Thread.sleep(100);
                    futureFailTimeout.setException(failure);
                } catch (final InterruptedException ex) {
                }
            }
        };

        failThreadTimeout.setDaemon(true);
        failThreadTimeout.start();

        try {
            futureFailTimeout.get(5, TimeUnit.SECONDS);
            Assert.fail("Successfully retrieved future value with failure set");
        } catch (final ExecutionException ex) {
            Assert.assertTrue(ex.getCause() instanceof IllegalArgumentException);
            Assert.assertEquals("rogue parameters", ex.getCause().getMessage());
        }

        Assert.assertTrue(futureFailTimeout.isDone());
        Assert.assertFalse(futureFailTimeout.isCancelled());
    }

    @Test
    public void testTimeout()
        throws Exception
    {
        IHttpResponseFuture<IHttpResponse> future = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());

        try {
            future.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Timeout did not fire");
        } catch (TimeoutException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Timeout waiting for task.", ex.getMessage());
        }

        // Test with negative timeout
        future = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        try {
            future.get(-1, TimeUnit.MILLISECONDS);
            Assert.fail("Timeout did not fire");
        } catch (TimeoutException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Timeout waiting for task.", ex.getMessage());
        }

        // Test with timeout = 0
        future = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
        try {
            future.get(0, TimeUnit.MILLISECONDS);
            Assert.fail("Timeout did not fire");
        } catch (TimeoutException ex) {
            Assert.assertNotNull(ex.getMessage());
            Assert.assertEquals("Timeout waiting for task.", ex.getMessage());
        }
    }

    @Test
    public void testListenerCompletion()
        throws Exception
    {
        Integer completionValue = Integer.valueOf(3);
        AtomicInteger resultInt = new AtomicInteger(0);
        AtomicInteger cancelInt = new AtomicInteger(0);
        AtomicInteger failInt = new AtomicInteger(0);
        IntegerCallback callback1 = new IntegerCallback(resultInt, cancelInt, failInt);
        IntegerCallback callback2 = new IntegerCallback(resultInt, cancelInt, failInt);
        DefaultHttpResponseFuture<Integer> integerFuture = new DefaultHttpResponseFuture<Integer>(new IntegerCallback(resultInt, cancelInt, failInt));
        IHttpResponseFuture<Integer> future = integerFuture;

        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        future.addListener(callback1);
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());

        // Make sure we can't add it twice
        try {
            future.addListener(callback1);
            Assert.fail("Added same listener twice");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Listener already registered", ex.getMessage());
        }

        try {
            future.addListener(null);
            Assert.fail("Added null listener");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Emtpy listener", ex.getMessage());
        }

        // Test completion
        Assert.assertTrue(integerFuture.set(completionValue));
        Assert.assertEquals(6, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        Assert.assertFalse(integerFuture.set(completionValue));
        Assert.assertEquals(6, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());

        // Adding new listener after completion should execute it immediately
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());
        future.addListener(callback2);
        Assert.assertEquals(9, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());

        // Test listener chain
        resultInt.set(0);
        cancelInt.set(0);
        failInt.set(0);
        callback1 = new IntegerCallback(resultInt, cancelInt, failInt);
        callback2 = new IntegerCallback(resultInt, cancelInt, failInt);

        IntegerCallback callback3 = new IntegerCallback(resultInt, cancelInt, failInt);
        integerFuture = new DefaultHttpResponseFuture<Integer>(new IntegerCallback(resultInt, cancelInt, failInt));
        future = integerFuture;

        future.addListener(callback1);
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        future.addListener(callback2);
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());
        future.addListener(callback3);
        Assert.assertEquals(-1, callback3.getResult());
        Assert.assertEquals(0, callback3.getCancelCount());
        Assert.assertEquals(0, callback3.getFailCount());
        Assert.assertNull(callback3.getFailure());
        Assert.assertTrue(integerFuture.set(completionValue));
        Assert.assertEquals(6, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        Assert.assertEquals(9, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());
        Assert.assertEquals(12, callback3.getResult());
        Assert.assertEquals(0, callback3.getCancelCount());
        Assert.assertEquals(0, callback3.getFailCount());
        Assert.assertNull(callback3.getFailure());
    }

    @Test
    public void testListenerCancel()
        throws Exception
    {
        AtomicInteger resultInt = new AtomicInteger(0);
        AtomicInteger cancelInt = new AtomicInteger(0);
        AtomicInteger failInt = new AtomicInteger(0);
        IntegerCallback callback1 = new IntegerCallback(resultInt, cancelInt, failInt);
        IntegerCallback callback2 = new IntegerCallback(resultInt, cancelInt, failInt);
        DefaultHttpResponseFuture<Integer> integerFuture = new DefaultHttpResponseFuture<Integer>(new IntegerCallback(resultInt, cancelInt, failInt));
        IHttpResponseFuture<Integer> future = integerFuture;

        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        future.addListener(callback1);
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());

        // Test cancel
        Assert.assertTrue(future.cancel(true));
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(2, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        Assert.assertFalse(future.cancel(true));
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(2, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());

        // Adding new listener after completion should execute it immediately
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());
        future.addListener(callback2);
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(3, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());

        // Test listener chain
        resultInt.set(0);
        cancelInt.set(0);
        failInt.set(0);
        callback1 = new IntegerCallback(resultInt, cancelInt, failInt);
        callback2 = new IntegerCallback(resultInt, cancelInt, failInt);

        IntegerCallback callback3 = new IntegerCallback(resultInt, cancelInt, failInt);
        integerFuture = new DefaultHttpResponseFuture<Integer>(new IntegerCallback(resultInt, cancelInt, failInt));
        future = integerFuture;

        future.addListener(callback1);
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        future.addListener(callback2);
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());
        future.addListener(callback3);
        Assert.assertEquals(-1, callback3.getResult());
        Assert.assertEquals(0, callback3.getCancelCount());
        Assert.assertEquals(0, callback3.getFailCount());
        Assert.assertNull(callback3.getFailure());
        Assert.assertTrue(future.cancel(true));
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(2, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(3, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());
        Assert.assertEquals(-1, callback3.getResult());
        Assert.assertEquals(4, callback3.getCancelCount());
        Assert.assertEquals(0, callback3.getFailCount());
        Assert.assertNull(callback3.getFailure());
    }

    @Test
    public void testListenerFail()
        throws Exception
    {
        Exception ex1 = new IllegalArgumentException("bad parameters");
        Exception ex2 = new IllegalArgumentException("invalid parameters");
        AtomicInteger resultInt = new AtomicInteger(0);
        AtomicInteger cancelInt = new AtomicInteger(0);
        AtomicInteger failInt = new AtomicInteger(0);
        IntegerCallback callback1 = new IntegerCallback(resultInt, cancelInt, failInt);
        IntegerCallback callback2 = new IntegerCallback(resultInt, cancelInt, failInt);
        DefaultHttpResponseFuture<Integer> integerFuture = new DefaultHttpResponseFuture<Integer>(new IntegerCallback(resultInt, cancelInt, failInt));
        IHttpResponseFuture<Integer> future = integerFuture;

        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        future.addListener(callback1);
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());

        // Test fail
        Assert.assertTrue(integerFuture.setException(ex1));
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(2, callback1.getFailCount());
        Assert.assertEquals(ex1, callback1.getFailure());
        Assert.assertEquals("bad parameters", callback1.getFailure().getMessage());
        Assert.assertFalse(integerFuture.setException(ex2));
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(2, callback1.getFailCount());
        Assert.assertEquals(ex1, callback1.getFailure());
        Assert.assertEquals("bad parameters", callback1.getFailure().getMessage());

        // Adding new listener after completion should execute it immediately
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());
        future.addListener(callback2);
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(3, callback2.getFailCount());
        Assert.assertEquals(ex1, callback2.getFailure());
        Assert.assertEquals("bad parameters", callback2.getFailure().getMessage());

        // Test listener chain
        resultInt.set(0);
        cancelInt.set(0);
        failInt.set(0);
        callback1 = new IntegerCallback(resultInt, cancelInt, failInt);
        callback2 = new IntegerCallback(resultInt, cancelInt, failInt);

        IntegerCallback callback3 = new IntegerCallback(resultInt, cancelInt, failInt);
        integerFuture = new DefaultHttpResponseFuture<Integer>(new IntegerCallback(resultInt, cancelInt, failInt));
        future = integerFuture;

        future.addListener(callback1);
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(0, callback1.getFailCount());
        Assert.assertNull(callback1.getFailure());
        future.addListener(callback2);
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(0, callback2.getFailCount());
        Assert.assertNull(callback2.getFailure());
        future.addListener(callback3);
        Assert.assertEquals(-1, callback3.getResult());
        Assert.assertEquals(0, callback3.getCancelCount());
        Assert.assertEquals(0, callback3.getFailCount());
        Assert.assertNull(callback3.getFailure());
        Assert.assertTrue(integerFuture.setException(ex1));
        Assert.assertEquals(-1, callback1.getResult());
        Assert.assertEquals(0, callback1.getCancelCount());
        Assert.assertEquals(2, callback1.getFailCount());
        Assert.assertEquals(ex1, callback1.getFailure());
        Assert.assertEquals("bad parameters", callback1.getFailure().getMessage());
        Assert.assertEquals(-1, callback2.getResult());
        Assert.assertEquals(0, callback2.getCancelCount());
        Assert.assertEquals(3, callback2.getFailCount());
        Assert.assertEquals(ex1, callback2.getFailure());
        Assert.assertEquals("bad parameters", callback2.getFailure().getMessage());
        Assert.assertEquals(-1, callback3.getResult());
        Assert.assertEquals(0, callback3.getCancelCount());
        Assert.assertEquals(4, callback3.getFailCount());
        Assert.assertEquals(ex1, callback3.getFailure());
        Assert.assertEquals("bad parameters", callback3.getFailure().getMessage());
    }

    @Test
    public void testInterrupts()
        throws Exception
    {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        final Callable<Exception> interruptible = new Callable<Exception>() {
            @Override
            public Exception call()
            {
                Exception rv = null;
                DefaultHttpResponseFuture<IHttpResponse> defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
                IHttpResponseFuture<IHttpResponse> future = defaultFuture;
                Assert.assertFalse(future.isCancelled());
                Assert.assertFalse(future.isDone());

                try {
                    future.get();
                } catch (Exception ex) {
                    rv = ex;
                }

                return rv;
            }
        };

        Thread.sleep(200);
        Future<Exception> future = pool.submit(interruptible);
        pool.shutdownNow();
        Assert.assertTrue(future.get() instanceof InterruptedException);

        ExecutorService pool2 = Executors.newFixedThreadPool(1);
        final Callable<Exception> interruptible2 = new Callable<Exception>() {
            @Override
            public Exception call()
            {
                Exception rv = null;
                DefaultHttpResponseFuture<IHttpResponse> defaultFuture = new DefaultHttpResponseFuture<IHttpResponse>(getDefaultCallback());
                IHttpResponseFuture<IHttpResponse> future = defaultFuture;
                Assert.assertFalse(future.isCancelled());
                Assert.assertFalse(future.isDone());

                try {
                    future.get();
                } catch (Exception ex) {
                    rv = ex;
                }

                return rv;
            }
        };

        Thread.sleep(200);
        Future<Exception> future2 = pool2.submit(interruptible2);
        pool2.shutdownNow();
        Assert.assertTrue(future2.get(5, TimeUnit.MINUTES) instanceof InterruptedException);
    }

    private IHttpCallback<IHttpResponse> getDefaultCallback()
    {
        IHttpCallback<IHttpResponse> callback = new IHttpCallback<IHttpResponse>() {
            @Override
            public void onCompletion(final IHttpResponse value) {}

            @Override
            public IHttpResponse decoder(final IHttpResponse response)
            {
                return response;
            }

            @Override
            public void onCancel() {}

            @Override
            public void onFailure(final Throwable th) {}
        };

        return callback;
    }

    private static class IntegerCallback
        implements IHttpCallback<Integer>
    {
        private final AtomicInteger resultInt;
        private final AtomicInteger cancelInt;
        private final AtomicInteger failInt;
        private int result = -1;
        private int cancelCount = 0;
        private Throwable failure = null;
        private int failCount = 0;

        public IntegerCallback(final AtomicInteger resultInt, final AtomicInteger cancelInt, final AtomicInteger failInt)
        {
            this.resultInt = resultInt;
            this.cancelInt = cancelInt;
            this.failInt = failInt;
        }

        @Override
        public void onCompletion(final Integer value)
        {
            result = resultInt.addAndGet(value.intValue());
        }

        @Override
        public Integer decoder(final IHttpResponse response)
        {
            return Integer.valueOf(-1);
        }

        @Override
        public void onCancel()
        {
            cancelCount = cancelInt.incrementAndGet();
        }

        @Override
        public void onFailure(final Throwable th)
        {
            failure = th;
            failCount = failInt.incrementAndGet();
        }

        public int getResult()
        {
            return result;
        }

        public int getCancelCount()
        {
            return cancelCount;
        }

        public Throwable getFailure()
        {
            return failure;
        }

        public int getFailCount()
        {
            return failCount;
        }
    }
}
