package com.servicemesh.io.http;

import java.io.Serializable;

/**
 * This abstract class contains common constant values used by the HTTP and supporting libraries.
 * 
 * @author henry
 *
 */
public abstract class HttpConstants implements Serializable {
    private static final long serialVersionUID = 20140314;

    public static final String ACCEPT           = "Accept";
    public static final String CONTENT_TYPE     = "Content-Type";
    public static final String TEXT_XML         = "text/xml";
    public static final String APPLICATION_JSON = "application/json";
}
