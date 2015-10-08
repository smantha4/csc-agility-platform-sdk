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

/**
 * A callback handler for consuming the result of an HTTP operation.
 * 
 * <p> The {@link IHttpClient#execute(IHttpRequest, IHttpCallback)} method allows for an
 * optional callback completion handler to be specified to consume the result of
 * the operation. The {@link #onCompletion onCompletion} method is invoked on
 * successful completion of the HTTP I/O operation. The {@link #decoder decoder}
 * method is invoked on successful completion to translate the IHttpResponse to
 * a consumable object. The {@link #onCancel onCancel} method is invoked if a
 * cancel is requested for the operation. The {@link #onFailure onFailure} method
 * is invoked if the HTTP I/O operation fails.
 *
 * @param <V>  The result type of the operation.
 */
public interface IHttpCallback<V>
{
    /**
     * Invoked when the HTTP I/O operation has completed. This only indicates
     * that the HTTP operation was processed. The HTTP response will still need
     * to be inspected for success.
     * 
     * @param value The result of the operation.
     */
    public void onCompletion(final V value);

    /**
     * Invoked when the HTTP I/O operation has completed to translate the response
     * to a consumable object. This only indicates that the HTTP operation was
     * processed. The HTTP response will still need to be inspected for success.
     * 
     * @param response The response from the operation.
     * 
     * @return The translated consumable object.
     */
    public V decoder(final IHttpResponse response);

    /**
     * Invoked when the operation is canceled.
     */
    public void onCancel();

    /**
     * Invoked when the I/O operation fails.
     * 
     * @param th  The exception indicating the reason for the failure.
     */
    public void onFailure(final Throwable th);
}
