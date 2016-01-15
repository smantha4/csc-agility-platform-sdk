/**
 *              COPYRIGHT (C) 2008-2015 SERVICEMESH, INC.
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

package com.servicemesh.core.async.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;

import com.servicemesh.core.async.CompletablePromise;
import com.servicemesh.core.async.Function0;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.async.PromiseFactory;
import com.servicemesh.core.reactor.TimerReactor;
import com.servicemesh.core.reactor.WorkReactor;

public class PromiseFactoryTest
{
    @Test
    public void testPureCompleted() throws Throwable
    {
        final Promise<Integer> promise = Promise.pure(101);

        Assert.assertEquals(Integer.valueOf(101), promise.get(1, TimeUnit.SECONDS));
        Assert.assertTrue(promise.isCompleted());
        Assert.assertFalse(promise.isCancelled());
        Assert.assertFalse(promise.isFailed());
    }

    @Test
    public void testPureFailed() throws Throwable
    {
        final Promise<Integer> promise = Promise.pure(new NullPointerException("Test"));

        try
        {
            promise.get(1, TimeUnit.SECONDS);
            Assert.fail("Succeeded when exception set");
        }
        catch (NullPointerException ex)
        {
            Assert.assertTrue("Test".equals(ex.getMessage()));
        }

        Assert.assertFalse(promise.isCompleted());
        Assert.assertFalse(promise.isCancelled());
        Assert.assertTrue(promise.isFailed());
    }

    @Test
    public void testSequencePromise() throws Throwable
    {
        final CompletablePromise<Integer> promise1 = PromiseFactory.create();
        final CompletablePromise<Integer> promise2 = PromiseFactory.create();
        final CompletablePromise<Integer> promise3 = PromiseFactory.create();
        final List<Promise<Integer>> promises = new ArrayList<Promise<Integer>>();

        promises.add(promise1);
        promises.add(promise2);
        promises.add(promise3);

        final Promise<List<Integer>> sequence = Promise.sequence(promises);

        try
        {
            sequence.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Expected timeout exception");
        }
        catch (TimeoutException ex)
        {
            // Ignore, expected
        }

        promise1.complete(101);
        try
        {
            sequence.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Expected timeout exception");
        }
        catch (TimeoutException ex)
        {
            // Ignore, expected
        }

        promise2.complete(202);
        try
        {
            sequence.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Expected timeout exception");
        }
        catch (TimeoutException ex)
        {
            // Ignore, expected
        }

        promise3.complete(303);
        final List<Integer> completedValues = sequence.get(1, TimeUnit.SECONDS);
        int promise1Found = 0;
        int promise2Found = 0;
        int promise3Found = 0;

        Assert.assertEquals(3, completedValues.size());

        for (final Integer nextValue : completedValues)
        {
            if (101 == nextValue)
            {
                ++promise1Found;
            }
            else if (202 == nextValue)
            {
                ++promise2Found;
            }
            else if (303 == nextValue)
            {
                ++promise3Found;
            }
        }

        Assert.assertTrue(promise1Found == 1);
        Assert.assertTrue(promise2Found == 1);
        Assert.assertTrue(promise3Found == 1);
    }

    @Test
    public void testSequenceEmpty() throws Throwable
    {
        final Promise<List<Integer>> sequence = Promise.sequence(new ArrayList<Promise<Integer>>());
        final List<Integer> completedValues = sequence.get(1, TimeUnit.SECONDS);

        Assert.assertEquals(0, completedValues.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSequenceNull() throws Throwable
    {
        Promise.sequence(null);
    }

    @Test
    public void testSequenceFailure() throws Throwable
    {
        final Promise<Integer> promise1 = PromiseFactory.create();
        final CompletablePromise<Integer> promise2 = PromiseFactory.create();
        final Promise<Integer> promise3 = PromiseFactory.create();
        final List<Promise<Integer>> promises = new ArrayList<Promise<Integer>>();

        promises.add(promise1);
        promises.add(promise2);
        promises.add(promise3);

        final Promise<List<Integer>> sequence = Promise.sequence(promises);

        promise2.failure(new NullPointerException("Test"));

        try
        {
            sequence.get(1, TimeUnit.SECONDS);
            Assert.fail("Succeeded when exception set");
        }
        catch (NullPointerException ex)
        {
            Assert.assertTrue("Test".equals(ex.getMessage()));
        }

        Assert.assertFalse(sequence.isCompleted());
        Assert.assertFalse(sequence.isCancelled());
        Assert.assertTrue(sequence.isFailed());
    }

    @Test
    public void testSequenceCancel() throws Throwable
    {
        final Promise<Integer> promise1 = PromiseFactory.create();
        final Promise<Integer> promise2 = PromiseFactory.create();
        final Promise<Integer> promise3 = PromiseFactory.create();
        final List<Promise<Integer>> promises = new ArrayList<Promise<Integer>>();

        promises.add(promise1);
        promises.add(promise2);
        promises.add(promise3);

        final Promise<List<Integer>> sequence = Promise.sequence(promises);

        // Cancels propagate inside out
        sequence.cancel();

        try
        {
            promise1.get(1, TimeUnit.SECONDS);
            Assert.fail("Promise had been cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }

        try
        {
            promise2.get(1, TimeUnit.SECONDS);
            Assert.fail("Promise had been cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }

        try
        {
            promise3.get(1, TimeUnit.SECONDS);
            Assert.fail("Promise had been cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }

        try
        {
            sequence.get(1, TimeUnit.SECONDS);
            Assert.fail("Promise had been cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }
    }

    @Test
    public void testSequenceCancelWrapped() throws Throwable
    {
        final Promise<Integer> promise1 = PromiseFactory.create();
        final Promise<Integer> promise2 = PromiseFactory.create();
        final Promise<Integer> promise3 = PromiseFactory.create();
        final List<Promise<Integer>> promises = new ArrayList<Promise<Integer>>();

        promises.add(promise1);
        promises.add(promise2);
        promises.add(promise3);

        final Promise<List<Integer>> sequence = Promise.sequence(promises);

        promise2.cancel();

        try
        {
            sequence.get(1, TimeUnit.SECONDS);
            Assert.fail("Succeeded when wrapped promise was cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }

        // Make sure sequence doesn't throw an IllegalStateException
        promise3.cancel();
    }

    @Test
    public void testSequenceAnyPromise() throws Throwable
    {
        final CompletablePromise<Integer> promise1 = PromiseFactory.create();
        final CompletablePromise<String> promise2 = PromiseFactory.create();
        final CompletablePromise<Long> promise3 = PromiseFactory.create();
        final List<Promise<?>> promises = new ArrayList<Promise<?>>();

        promises.add(promise1);
        promises.add(promise2);
        promises.add(promise3);

        final Promise<List<Object>> sequence = Promise.sequenceAny(promises);

        try
        {
            sequence.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Expected timeout exception");
        }
        catch (TimeoutException ex)
        {
            // Ignore, expected
        }

        promise1.complete(101);
        try
        {
            sequence.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Expected timeout exception");
        }
        catch (TimeoutException ex)
        {
            // Ignore, expected
        }

        promise2.complete("Completed");
        try
        {
            sequence.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Expected timeout exception");
        }
        catch (TimeoutException ex)
        {
            // Ignore, expected
        }

        promise3.complete(303L);
        final List<Object> completedValues = sequence.get(1, TimeUnit.SECONDS);
        int promise1Found = 0;
        int promise2Found = 0;
        int promise3Found = 0;

        Assert.assertEquals(3, completedValues.size());

        for (final Object nextValue : completedValues)
        {
            if ((nextValue instanceof Integer) && (101 == ((Integer) nextValue).intValue()))
            {
                ++promise1Found;
            }
            else if ((nextValue instanceof String) && ("Completed".equals((String) nextValue)))
            {
                ++promise2Found;
            }
            else if ((nextValue instanceof Long) && (303L == ((Long) (nextValue)).longValue()))
            {
                ++promise3Found;
            }
        }

        Assert.assertTrue(promise1Found == 1);
        Assert.assertTrue(promise2Found == 1);
        Assert.assertTrue(promise3Found == 1);
    }

    @Test
    public void testSequenceAnyEmpty() throws Throwable
    {
        final Promise<List<Object>> sequence = Promise.sequenceAny(new ArrayList<Promise<?>>());
        final List<Object> completedValues = sequence.get(1, TimeUnit.SECONDS);

        Assert.assertEquals(0, completedValues.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSequenceAnyNull() throws Throwable
    {
        Promise.sequenceAny(null);
    }

    @Test
    public void testSequenceAnyFailure() throws Throwable
    {
        final Promise<Integer> promise1 = PromiseFactory.create();
        final CompletablePromise<String> promise2 = PromiseFactory.create();
        final Promise<Long> promise3 = PromiseFactory.create();
        final List<Promise<?>> promises = new ArrayList<Promise<?>>();

        promises.add(promise1);
        promises.add(promise2);
        promises.add(promise3);

        final Promise<List<Object>> sequence = Promise.sequenceAny(promises);

        promise2.failure(new NullPointerException("Test"));

        try
        {
            sequence.get(1, TimeUnit.SECONDS);
            Assert.fail("Succeeded when exception set");
        }
        catch (NullPointerException ex)
        {
            Assert.assertTrue("Test".equals(ex.getMessage()));
        }

        Assert.assertFalse(sequence.isCompleted());
        Assert.assertFalse(sequence.isCancelled());
        Assert.assertTrue(sequence.isFailed());
    }

    @Test
    public void testSequenceAnyCancel() throws Throwable
    {
        final Promise<Integer> promise1 = PromiseFactory.create();
        final Promise<String> promise2 = PromiseFactory.create();
        final Promise<Long> promise3 = PromiseFactory.create();
        final List<Promise<?>> promises = new ArrayList<Promise<?>>();

        promises.add(promise1);
        promises.add(promise2);
        promises.add(promise3);

        final Promise<List<Object>> sequence = Promise.sequenceAny(promises);

        // Cancels propagate in
        sequence.cancel();

        try
        {
            promise1.get(1, TimeUnit.SECONDS);
            Assert.fail("Promise had been cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }

        try
        {
            promise2.get(1, TimeUnit.SECONDS);
            Assert.fail("Promise had been cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }

        try
        {
            promise3.get(1, TimeUnit.SECONDS);
            Assert.fail("Promise had been cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }

        try
        {
            sequence.get(1, TimeUnit.SECONDS);
            Assert.fail("Promise had been cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }
    }

    @Test
    public void testSequenceAnyCancelWrapped() throws Throwable
    {
        final Promise<Integer> promise1 = PromiseFactory.create();
        final Promise<String> promise2 = PromiseFactory.create();
        final Promise<Long> promise3 = PromiseFactory.create();
        final List<Promise<?>> promises = new ArrayList<Promise<?>>();

        promises.add(promise1);
        promises.add(promise2);
        promises.add(promise3);

        final Promise<List<Object>> sequence = Promise.sequenceAny(promises);

        promise2.cancel();
        try
        {
            sequence.get(1, TimeUnit.SECONDS);
            Assert.fail("Succeeded when wrapped promise was cancelled");
        }
        catch (CancellationException ex)
        {
            // Ignore, expected
        }

        // Make sure sequence doesn't throw an IllegalStateException
        promise3.cancel();
    }

    @Test
    public void testWorker() throws Throwable
    {
        final Promise<Integer> promise = Promise.promise(WorkReactor.getDefaultWorkReactor(), new Function0<Integer>() {
            @Override
            public Integer exec()
            {
                return 111;
            }
        });

        Assert.assertEquals(Integer.valueOf(111), promise.get(1, TimeUnit.SECONDS));
    }

    @Test
    public void testWorkerLambda() throws Throwable
    {
        final Promise<Integer> promise = Promise.promise(WorkReactor.getDefaultWorkReactor(), () -> 111);

        Assert.assertEquals(Integer.valueOf(111), promise.get(1, TimeUnit.SECONDS));
    }

    @Test
    public void testWorkerFailure() throws Throwable
    {
        final Promise<Integer> promise = Promise.promise(WorkReactor.getDefaultWorkReactor(), new Function0<Integer>() {
            @Override
            public Integer exec()
            {
                throw new NullPointerException("Worker Failure");
            }
        });

        try
        {
            promise.get(1, TimeUnit.SECONDS);
            Assert.fail("Succeeded when worker failed");
        }
        catch (NullPointerException ex)
        {
            Assert.assertEquals("Worker Failure", ex.getMessage());
        }
    }

    @Test
    public void testDelayed() throws Throwable
    {
        final Promise<Integer> promise = Promise.delayed(TimerReactor.getDefaultTimerReactor(), 250, new Function0<Integer>() {
            @Override
            public Integer exec()
            {
                return 222;
            }
        });

        Assert.assertEquals(Integer.valueOf(222), promise.get(2, TimeUnit.SECONDS));
    }

    @Test
    public void testDelayedLambda() throws Throwable
    {
        final Promise<Integer> promise = Promise.delayed(TimerReactor.getDefaultTimerReactor(), 250, () -> 222);

        Assert.assertEquals(Integer.valueOf(222), promise.get(2, TimeUnit.SECONDS));
    }

    @Test
    public void testDelayedFailure() throws Throwable
    {
        final Promise<Integer> promise = Promise.delayed(TimerReactor.getDefaultTimerReactor(), 250, new Function0<Integer>() {
            @Override
            public Integer exec()
            {
                throw new NullPointerException("Delayed Failure");
            }
        });

        try
        {
            promise.get(1, TimeUnit.SECONDS);
            Assert.fail("Succeeded when delayed worker failed");
        }
        catch (NullPointerException ex)
        {
            Assert.assertEquals("Delayed Failure", ex.getMessage());
        }
    }

    @Test
    public void testTimeout() throws Throwable
    {
        final Promise<Integer> promise = Promise.timeout(TimerReactor.getDefaultTimerReactor(), 250, 333);

        Assert.assertEquals(Integer.valueOf(333), promise.get(2, TimeUnit.SECONDS));
    }
}
