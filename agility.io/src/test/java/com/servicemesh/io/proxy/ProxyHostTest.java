package com.servicemesh.io.proxy;

import org.junit.Assert;
import org.junit.Test;

public class ProxyHostTest
{
    private final String HOST = "192.168.31.22";
    private final int PORT = 10880;
    private final String PROXYHOST_CHAINED = "192.168.31.23";
    private final int PROXYPORT_CHAINED = 3128;
    private final String SCHEME = "https";
    private final ProxyType PROXY_TYPE = ProxyType.SOCKS5_PROXY;
    private final ProxyType PROXY_TYPE_CHAINED = ProxyType.HTTP_PROXY;
    private final String ADMIN = "proxyAdmin";
    private final String ADMIN_PWD = "proxyPassword";
    private final String HOST_TOSTRING1 = HOST + ":" + PORT;
    private final String HOST_TOSTRING2 = SCHEME + "//" + HOST + ":" + PORT;
    private final String PROXYHOST_TOSTRING1 = ProxyType.SOCKS5_PROXY.getScheme() + "//" + HOST + ":" + PORT;
    private final String PROXYHOST_TOSTRING2 =
            ProxyType.HTTP_PROXY.getScheme() + "//" + PROXYHOST_CHAINED + ":" + PROXYPORT_CHAINED + "->" + PROXYHOST_TOSTRING1;

    @Test
    public void testHost() throws Exception
    {
        Host host = new Host(HOST, PORT);
        Assert.assertEquals(HOST, host.getHostname());
        Assert.assertEquals(PORT, host.getPort());
        Assert.assertNull(host.getScheme());
        Assert.assertEquals(HOST_TOSTRING1, host.toString());

        host = new Host(HOST, PORT, SCHEME);
        Assert.assertEquals(HOST, host.getHostname());
        Assert.assertEquals(PORT, host.getPort());
        Assert.assertEquals(SCHEME, host.getScheme());
        Assert.assertEquals(HOST_TOSTRING2, host.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingHostname() throws Exception
    {
        new Host((String) null, PORT);
    }

    @Test
    public void testProxyHost() throws Exception
    {
        Proxy proxy = new Proxy(HOST, PORT, PROXY_TYPE, (Host) null);
        Assert.assertEquals(HOST, proxy.getHostname());
        Assert.assertEquals(PORT, proxy.getPort());
        Assert.assertNull(proxy.getScheme());
        Assert.assertEquals(PROXY_TYPE, proxy.getType());
        Assert.assertNull(proxy.getTargetHost());
        Assert.assertNull(proxy.getAdmin());
        Assert.assertNull(proxy.getPassword());
        Assert.assertEquals(PROXYHOST_TOSTRING1, proxy.toString());

        Proxy proxyChained = new Proxy(PROXYHOST_CHAINED, PROXYPORT_CHAINED, PROXY_TYPE_CHAINED, proxy, ADMIN, ADMIN_PWD);
        Assert.assertEquals(PROXYHOST_CHAINED, proxyChained.getHostname());
        Assert.assertEquals(PROXYPORT_CHAINED, proxyChained.getPort());
        Assert.assertNull(proxyChained.getScheme());
        Assert.assertEquals(PROXY_TYPE_CHAINED, proxyChained.getType());
        Assert.assertEquals(proxy, proxyChained.getTargetHost());
        Assert.assertEquals(ADMIN, proxyChained.getAdmin());
        Assert.assertEquals(ADMIN_PWD, proxyChained.getPassword());
        Assert.assertEquals(PROXYHOST_TOSTRING2, proxyChained.toString());
    }

    @Test
    public void testMissingArguments() throws Exception
    {
        try
        {
            new Proxy((String) null, PORT, PROXY_TYPE, (Host) null);
            Assert.fail("Passed test with missing hostname");
        }
        catch (IllegalArgumentException ex)
        {
        }

        try
        {
            new Proxy(HOST, PORT, (ProxyType) null, (Host) null);
            Assert.fail("Passed test with missing proxy type");
        }
        catch (IllegalArgumentException ex)
        {
        }
    }
}
