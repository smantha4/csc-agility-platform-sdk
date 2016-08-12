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

package com.servicemesh.io.http.impl;

import org.apache.http.HttpStatus;

import com.google.common.base.Preconditions;
import com.servicemesh.io.http.IHttpResponse;

public class DefaultServerBusyRetryStrategy
{
    private final static int HTTP_SERVICE_UNAVAILABLE = HttpStatus.SC_SERVICE_UNAVAILABLE;
    private final static int HTTP_TOO_MANY_REQUESTS = 429; // Apache doesn't have this defined yet
    private final static int DEFAULT_RETRIES = 3;
    protected static final long DEFAULT_BUSY_RETRY_INTERVAL = 10000; // In milliseconds

    private final long _retryInterval; // In milliseconds

    private int _retries;

    /**
     * Default constructor. Will create a strategy object with three retries and a ten second retry interval configured.
     */
    public DefaultServerBusyRetryStrategy()
    {
        this(DEFAULT_RETRIES, DEFAULT_BUSY_RETRY_INTERVAL);
    }

    /**
     * Strategy constructor with the specified retries and retry interval. Retries will be attempted for HTTP status codes 429 and
     * 503.
     *
     * @param retries
     *            The number of retries to attempt.
     * @param retryInterval
     *            The retry interval in milliseconds.
     */
    public DefaultServerBusyRetryStrategy(final int retries, final long retryInterval)
    {
        Preconditions.checkArgument(retries >= 0, "Retries must be greater than zero");
        Preconditions.checkArgument(retryInterval > 0, "Retry interval must be greater than zero");

        _retries = retries;
        _retryInterval = retryInterval;
    }

    /**
     * Reports if a retry can be attempted.
     *
     * @param response
     *            The response from the HTTP request.
     * @return Returns true if a retry could be attempted, otherwise, false.
     */
    public boolean retry(final IHttpResponse response)
    {
        Preconditions.checkNotNull(response, "Response cannot be null");

        boolean rv = false;
        final int status = response.getStatusCode();

        if ((_retries > 0) && ((status == HTTP_SERVICE_UNAVAILABLE) || (status == HTTP_TOO_MANY_REQUESTS)))
        {
            _retries--;
            rv = true;
        }

        return rv;
    }

    /**
     * Returns the configured retry interval.
     *
     * @return The retry interval in milliseconds.
     */
    public long retryInterval()
    {
        return _retryInterval;
    }
}
