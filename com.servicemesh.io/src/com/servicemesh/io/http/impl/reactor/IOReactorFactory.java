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

package com.servicemesh.io.http.impl.reactor;

import java.io.IOException;

import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.impl.nio.reactor.IOReactorConfig.Builder;
import org.apache.log4j.Logger;

public class IOReactorFactory
{
    private static final Logger logger = Logger.getLogger(IOReactorFactory.class);

    private static class FactoryHolder
    {
        private static final IOReactorFactory factoryInstance = new IOReactorFactory();
    }

    private IOReactorFactory() {}

    public static IOReactorFactory getInstance()
    {
        return FactoryHolder.factoryInstance;
    }

    public ConnectingIOReactor createConnectingReactor()
        throws IOException
    {
        Builder configBuilder = IOReactorConfig.copy(IOReactorConfig.DEFAULT);

        configBuilder.setIoThreadCount(1);
        configBuilder.setSoKeepAlive(true);

        return new DefaultConnectingIOReactor(configBuilder.build());
    }
}
