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

package com.servicemesh.core.async.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Should not be used directly - see IPromise.sequence
 */

public class SequencePromise<T> extends DefaultCompletablePromise<List<T>>
{
    private volatile int _pending = 0;
    private List<T> _results = Collections.synchronizedList(new ArrayList<T>());

    public SequencePromise(final int pending)
    {
        _pending = pending;
    }

    public void add(final T result)
    {
        boolean completed = false;
        final List<T> results = new ArrayList<T>();

        synchronized (this)
        {
            _results.add(result);

            if (--_pending == 0)
            {
                completed = true;

                synchronized (_results)
                {
                    results.addAll(_results);
                }
            }
        }

        if (completed)
        {
            complete(results);
        }
    }
}
