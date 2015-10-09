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

package com.servicemesh.io.util;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.net.ServerSocket;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

public class JunitWireMockClassRule
    extends WireMockClassRule
{

    public JunitWireMockClassRule(final String keystorePath, final boolean getHttpsPort)
    {
        super(createConfiguration(keystorePath, getHttpsPort));
    }

    private static WireMockConfiguration createConfiguration(final String keystorePath, final boolean getHttpsPort)
    {
        WireMockConfiguration configuration = null;

        try (ServerSocket httpServerSocket = new ServerSocket(0);
             ServerSocket httpsServerSocket = new ServerSocket(0))
        {
            int httpPort = httpServerSocket.getLocalPort();
            configuration = wireMockConfig().port(httpPort);

            if (getHttpsPort) {
                int httpsPort = httpsServerSocket.getLocalPort();

                configuration = configuration.httpsPort(httpsPort);
            }

            if ((keystorePath != null) && !keystorePath.isEmpty()) {
                configuration = configuration.keystorePath(keystorePath);
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }

        return configuration;
    }
}
