/*
 * Copyright (C) 2016 Computer Science Corporation
 * All rights reserved.
 *
 */
package com.servicemesh.io.http;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author srirammantha
 */
public class QueryParamsTest
{

    @Test
    public void testAsQueryStringWithNoParamValueWithUseAsTokenTrue()
    {
        QueryParams queryParams = new QueryParams();

        QueryParam qp = new QueryParam("schema");
        queryParams.add(qp);

        queryParams.setUseAsToken(true);

        Assert.assertEquals("?schema", queryParams.asQueryString(false));

        //Flip Case sensitive flag
        queryParams.setCaseSensitive(false);
        Assert.assertEquals("?schema", queryParams.asQueryString(false));

        //Flip the maintain order flag
        queryParams.setMaintainOrder(true);
        Assert.assertEquals("?schema", queryParams.asQueryString(false));

    }

    @Test
    public void testAsQueryStringWithNoParamValueWithUseAsTokenFalse()
    {
        QueryParams queryParams = new QueryParams();

        QueryParam qp = new QueryParam("schema");
        queryParams.add(qp);

        queryParams.setUseAsToken(false);
        Assert.assertEquals("?schema=", queryParams.asQueryString(false));

        queryParams.setCaseSensitive(false);
        Assert.assertEquals("?schema=", queryParams.asQueryString(false));

        queryParams.setMaintainOrder(true);
        Assert.assertEquals("?schema=", queryParams.asQueryString(false));

    }

    @Test
    public void testAsQueryStringWithParamValueWithUseAsTokenTrue()
    {
        QueryParams queryParams = new QueryParams();

        QueryParam qp = new QueryParam("schema", "1.0");
        queryParams.add(qp);

        queryParams.setUseAsToken(true);

        Assert.assertEquals("?schema=1.0", queryParams.asQueryString(false));

        //Flip Case sensitive flag
        queryParams.setCaseSensitive(false);
        Assert.assertEquals("?schema=1.0", queryParams.asQueryString(false));

        //Flip the maintain order flag
        queryParams.setMaintainOrder(true);
        Assert.assertEquals("?schema=1.0", queryParams.asQueryString(false));

    }

    @Test
    public void testAsQueryStringWithParamValueWithUseAsTokenFalse()
    {
        QueryParams queryParams = new QueryParams();

        QueryParam qp = new QueryParam("schema", "1.0");
        queryParams.add(qp);

        queryParams.setUseAsToken(false);

        Assert.assertEquals("?schema=1.0", queryParams.asQueryString(false));

        //Flip Case sensitive flag
        queryParams.setCaseSensitive(false);
        Assert.assertEquals("?schema=1.0", queryParams.asQueryString(false));

        //Flip the maintain order flag
        queryParams.setMaintainOrder(true);
        Assert.assertEquals("?schema=1.0", queryParams.asQueryString(false));

    }

}