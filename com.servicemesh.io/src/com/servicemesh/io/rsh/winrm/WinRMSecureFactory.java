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

package com.servicemesh.io.rsh.winrm;

import org.apache.log4j.Logger;

public class WinRMSecureFactory extends WinRMFactory 
{
	private final static Logger logger = Logger.getLogger(WinRMSecureFactory.class);
    protected final static int DEFAULT_PORT = 5986;
    private static final WinRMSecureFactory instance;
    
    static 
    {
        try 
        {
            instance = new WinRMSecureFactory();
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Unable to create WinRMSecureFactory singleton!", e);
        }
    }
    
    private WinRMSecureFactory()
    {
    	super();
    }
    
    public static WinRMSecureFactory getInstance() 
    {
        return instance;
    }    
    
    @Override
    protected void logTraceMessage(String message)
    {
        logger.trace(message);    	
    }
    
    @Override
    protected int getDefaultPort() 
    {
    	return DEFAULT_PORT;
    }
	
}
