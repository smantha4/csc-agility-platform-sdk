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

package com.servicemesh.io.rsh.winrm;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.servicemesh.io.http.HttpClientFactory;
import com.servicemesh.io.http.IHttpClient;
import com.servicemesh.io.http.IHttpClientConfig;
import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.http.IHttpHeader;
import com.servicemesh.io.http.IHttpRequest;
import com.servicemesh.io.http.IHttpResponse;
import com.servicemesh.io.rsh.winrm.WinRM;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WinRMTest
{
	public static final String BADResultXML = " ";
	public static final String CmdIDResponseXML = "<s:Envelope xml:lang='en-US' xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:x='http://schemas.xmlsoap.org/ws/2004/09/transfer' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd'><s:Header><a:Action>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandResponse</a:Action><a:MessageID>uuid:3BC228AB-B4F8-4EED-B1E2-477743168C02</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:8518d6f6-a3c4-4b3a-bc65-cf662bf90d7a</a:RelatesTo></s:Header><s:Body><rsp:CommandResponse><rsp:CommandId>119F1507-FEA6-46A3-B2EA-64E073707A32</rsp:CommandId></rsp:CommandResponse></s:Body></s:Envelope>"; 
	public static final String CmdResultXML = "<s:Envelope xml:lang='en-US' xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd'><s:Header><a:Action>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/ReceiveResponse</a:Action><a:MessageID>uuid:C740D105-BDC6-4C00-969F-C668C930E332</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:f866df1a-e502-4cb5-9851-cf44e4504af7</a:RelatesTo></s:Header><s:Body><rsp:ReceiveResponse><rsp:Stream Name='stdout' CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32'>DQo=</rsp:Stream><rsp:Stream Name='stdout' CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32'>TENJRCAgICAgICAgICAgICBOYW1lICAgICAgICAgICAgIERpc3BsYXlOYW1lICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIA==</rsp:Stream><rsp:Stream Name='stdout' CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32'>DQotLS0tICAgICAgICAgICAgIC0tLS0gICAgICAgICAgICAgLS0tLS0tLS0tLS0gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgDQoxMDMzICAgICAgICAgICAgIGVuLVVTICAgICAgICAgICAgRW5nbGlzaCAoVW5pdGVkIFN0YXRlcykgICAgICAgICAgICAgICAgICAgICAgDQoNCg0K</rsp:Stream><rsp:Stream Name='stdout' CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' End='true'></rsp:Stream><rsp:Stream Name='stderr' CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' End='true'></rsp:Stream><rsp:CommandState CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' State='http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Done'><rsp:ExitCode>0</rsp:ExitCode></rsp:CommandState></rsp:ReceiveResponse></s:Body></s:Envelope>";
	public static final String CreateShellResponseXML = "<s:Envelope xml:lang='en-US' xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:x='http://schemas.xmlsoap.org/ws/2004/09/transfer' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd'><s:Header><a:Action>http://schemas.xmlsoap.org/ws/2004/09/transfer/CreateResponse</a:Action><a:MessageID>uuid:122F0C35-82D3-41A3-BE61-506BB8E4A906</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:7d665c21-8c38-43a7-a9da-259904d0d225</a:RelatesTo></s:Header><s:Body><x:ResourceCreated><a:Address>http://192.168.71.66:5985/wsman</a:Address><a:ReferenceParameters><w:ResourceURI>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/cmd</w:ResourceURI><w:SelectorSet><w:Selector Name='ShellId'>7999D6FA-229B-4EC3-9DEE-5466472C9DD2</w:Selector></w:SelectorSet></a:ReferenceParameters></x:ResourceCreated><rsp:Shell xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell'><rsp:ShellId>7999D6FA-229B-4EC3-9DEE-5466472C9DD2</rsp:ShellId><rsp:ResourceUri>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/cmd</rsp:ResourceUri><rsp:Owner>administrator</rsp:Owner><rsp:ClientIP>172.17.11.146</rsp:ClientIP><rsp:IdleTimeOut>PT7200.000S</rsp:IdleTimeOut><rsp:InputStreams>stdin</rsp:InputStreams><rsp:OutputStreams>stdout stderr</rsp:OutputStreams><rsp:ShellRunTime>P0DT0H0M0S</rsp:ShellRunTime><rsp:ShellInactivity>P0DT0H0M0S</rsp:ShellInactivity></rsp:Shell></s:Body></s:Envelope>";
	public static final String DeleteCmdShellXML = "<s:Envelope xml:lang='en-US' xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd'><s:Header><a:Action>http://schemas.xmlsoap.org/ws/2004/09/transfer/DeleteResponse</a:Action><a:MessageID>uuid:B00EE686-CE0B-48BA-8AF3-F60D04F2ADF6</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:edf6cb96-780c-4011-9038-c2e26ee05448</a:RelatesTo></s:Header><s:Body></s:Body></s:Envelope>";
	public static final String CreateXMLDocXML = "<s:Envelope xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd'> <s:Header><a:To>{Url}</a:To><w:ResourceURI s:mustUnderstand='true'>{ResourceUri}</w:ResourceURI><a:ReplyTo><a:Address s:mustUnderstand='true'>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:Address></a:ReplyTo><a:Action s:mustUnderstand='true'>{ActionUri}</a:Action><w:MaxEnvelopeSize s:mustUnderstand='true'>153600</w:MaxEnvelopeSize><a:MessageID>uuid:{MessageId}</a:MessageID><w:Locale xml:lang='en-US' s:mustUnderstand='false'></w:Locale><w:OperationTimeout>PT60.000S</w:OperationTimeout></s:Header><s:Body><!--Body--></s:Body></s:Envelope>";
	public static final String CMDRunningResponseXML = "<s:Envelope xmlns:s='http://www.w3.org/2003/05/soap-envelope'  xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd' xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xml:lang='en-US'><s:Header><a:Action>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/ReceiveResponse</a:Action><a:MessageID>uuid:7803C4DE-28FA-4571-A6B7-B5AF432CD2D3</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:913ADDBE-32A1-4A9E-9732-BA891612DC4A</a:RelatesTo></s:Header><s:Body><rsp:ReceiveResponse><rsp:Stream CommandId='4A3B7C6D3F48-4A0D-B5C8-2BD8CF16EC1C' Name='stdout'>dXNlciBhZGRlZCBzdWNjZXNzZnVsbHkhIQ==</rsp:Stream><rsp:Stream CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' Name='stdout'>Cg==</rsp:Stream><rsp:CommandState CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' State='http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Running'/></rsp:ReceiveResponse></s:Body></s:Envelope>";
	private final long DEFAULT_WINRM_TEST_TIMEOUT = 60;

	WinRM _winrmShell;
	IHttpClientConfigBuilder _configBuilder;
	IHttpClientConfigBuilder _adaptedConfigBuilder;
	IHttpClientConfig _clientConfig;
	IHttpClientConfig _adaptedClientConfig;
	IHttpClient _client;
	IHttpClient _adpatedClient;
	IHttpRequest _request;
	IHttpResponse _response;
	HttpClientFactory _clientFactory;
	IHttpHeader _header;

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(wireMockConfig().port(5985).httpsPort(5986));

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    @Before
    public void setup()
        throws Exception
    {
        _configBuilder = HttpClientFactory.getInstance().getConfigBuilder();
        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CreateShellResponseXML)));

    	_winrmShell = new WinRM(_configBuilder, "test ", "test", "localhost", 5986, true, 
    	        1, 0);
    } 

    @After
    public void cleanUp()
        throws Exception
    {
        //setResponseXMLFromString(DeleteCmdShellXML);
        //_winrmShell.close();
    }

    @Test
    public void testCommandIDCreation() throws Exception
    {
        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdIDResponseXML)));
	    String commandID = _winrmShell.executeCommand("powershell get-Culture", DEFAULT_WINRM_TEST_TIMEOUT);

	    Assert.assertTrue(commandID.contains("119F1507"));
    }

    @Test
    public void testPowerShellCommand() throws Exception
    {
    	int timeout = 60000;

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CmdIDResponseXML)));

        String commandID = _winrmShell.executeCommand("powershell get-Culture", DEFAULT_WINRM_TEST_TIMEOUT);

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdResultXML)));

        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();

        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout);
        Assert.assertTrue(stdout.toString().contains("en-US"));

        // Test reconnect
        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdIDResponseXML)));

        _winrmShell.reconnect();
        commandID = _winrmShell.executeCommand("powershell get-Culture", DEFAULT_WINRM_TEST_TIMEOUT);

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdResultXML)));

        stdout = new StringBuilder();
        stderr = new StringBuilder();

        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout);
        Assert.assertTrue(stdout.toString().contains("en-US"));

        // Test reconnect with close
        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdIDResponseXML)));

        _winrmShell.close();
        _winrmShell.reconnect();
        commandID = _winrmShell.executeCommand("powershell get-Culture", DEFAULT_WINRM_TEST_TIMEOUT);

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdResultXML)));

        stdout = new StringBuilder();
        stderr = new StringBuilder();

        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout);
        Assert.assertTrue(stdout.toString().contains("en-US"));
    }
    
    
    @Test(expected = java.lang.NullPointerException.class)
    @Ignore
    public void testPowerShellCommandwithBadResponse() throws Exception
    {
    	int timeout = 1;

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CmdIDResponseXML)));

    	String commandID = _winrmShell.executeCommand("powershell get-Culture",DEFAULT_WINRM_TEST_TIMEOUT);

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(BADResultXML)));

        StringBuilder sbout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();

        _winrmShell.receiveResultSet(commandID, sbout, stderr, timeout);
    }

    @Test(expected = java.lang.Exception.class)
    public void TestCommandInRunningState() throws Exception
    {
    	int timeout = 2;

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CmdIDResponseXML)));

    	String commandID = _winrmShell.executeCommand("powershell -ExecutionPolicy Bypass -Command '&amp;{./c678350d-7c8b-4ac2-9d32-785d856453b8_wrapper.ps1;exit $LASTEXITCODE}'",DEFAULT_WINRM_TEST_TIMEOUT);

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CMDRunningResponseXML)));

        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        String pwd ="aaa";
        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout,null,pwd );
    }

    @Test
    public void TestPasswordChange() throws Exception
    {
    	int timeout = 2;
        String CMDResponse = "<s:Envelope xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd' xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xml:lang='en-US'><s:Header><a:Action>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/ReceiveResponse</a:Action><a:MessageID>uuid:D980EE64-B4B5-4C6D-8B0C-AA35BFD26E84</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:8B6475AC-5057-4938-856F-45130F5F706A</a:RelatesTo></s:Header><s:Body><rsp:ReceiveResponse><rsp:Stream CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' Name='stdout'>dXNlciBhZGRlZCBzdWNjZXNzZnVsbHkhIQ==</rsp:Stream><rsp:Stream CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' Name='stdout'>Cg==</rsp:Stream><rsp:Stream CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' End='true' Name='stdout'/><rsp:Stream CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' End='true' Name='stderr'/><rsp:CommandState CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' State='http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Done'><rsp:ExitCode>0</rsp:ExitCode></rsp:CommandState></rsp:ReceiveResponse></s:Body></s:Envelope>";

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdIDResponseXML)));

    	String commandID = _winrmShell.executeCommand("powershell -ExecutionPolicy Bypass -Command '&amp;{./c678350d-7c8b-4ac2-9d32-785d856453b8_wrapper.ps1;exit $LASTEXITCODE}'",DEFAULT_WINRM_TEST_TIMEOUT);

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CMDResponse)));

        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        String pwd ="NewPassword";
        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout, null, pwd );
        Assert.assertTrue(_winrmShell.getPassword().toString().contains(pwd));
    }

    @Test
    public void TestForUserAddScriptFailure() throws Exception
    {
    	int timeout = 2;
        String CMDResponse = "<s:Envelope xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd' xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xml:lang='en-US'><s:Header><a:Action>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/ReceiveResponse</a:Action><a:MessageID>uuid:D980EE64-B4B5-4C6D-8B0C-AA35BFD26E84</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:8B6475AC-5057-4938-856F-45130F5F706A</a:RelatesTo></s:Header><s:Body><rsp:ReceiveResponse><rsp:Stream CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' Name='stdout'>dXNlciBhZGRlZCBzdWNjZXNzZnVsbHkhIQ==</rsp:Stream><rsp:Stream CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' Name='stdout'>Cg==</rsp:Stream><rsp:Stream CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' End='true' Name='stdout'/><rsp:Stream CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' End='true' Name='stderr'/><rsp:CommandState CommandId='119F1507-FEA6-46A3-B2EA-64E073707A32' State='http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Done'><rsp:ExitCode>-1</rsp:ExitCode></rsp:CommandState></rsp:ReceiveResponse></s:Body></s:Envelope>";

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdIDResponseXML)));

    	String commandID = _winrmShell.executeCommand("PowerShell -ExecutionPolicy Bypass -Command '&amp;{./c678350d-7c8b-4ac2-9d32-785d856453b8_wrapper.ps1;exit $LASTEXITCODE}'",DEFAULT_WINRM_TEST_TIMEOUT);

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CMDResponse)));
            
        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        
        String pwd ="NewPassword";
        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout, null, pwd);
        Assert.assertTrue(_winrmShell.getPassword().toString().contains("test"));
    }

    @Test
    public void TestStdOutOutput() throws Exception
    {
    	int timeout = 3600;
    	final String CMDSTDOUT = "<s:Envelope xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd' xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell' xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xml:lang='en-US'><s:Header><a:Action>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/ReceiveResponse</a:Action><a:MessageID>uuid:FBAF3D07-D043-4D4B-BCD4-DCC91AD1EE2C</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:725A4502-1BB5-42D2-BA53-D98F87130CB8</a:RelatesTo></s:Header><s:Body><rsp:ReceiveResponse><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>Q29weWluZyBza2VsZXRvbiBmaWxlcy4K</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>VGhlc2UgZmlsZXMgYXJlIGZvciB0aGUgdXNlcnMgdG8gcGVyc29uYWxpc2UgdGhlaXIgY3lnd2luIGV4cGVyaWVuY2UuCgpUaGV5IHdpbGwgbmV2ZXIgYmUgb3ZlcndyaXR0ZW4gbm9yIGF1dG9tYXRpY2FsbHkgdXBkYXRlZC4KCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLmJhc2hyYycgLT4gYC9ob21lL2FkbWluLy8uYmFzaHJjJwo=</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLmJhc2hfcHJvZmlsZScgLT4gYC9ob21lL2FkbWluLy8uYmFzaF9wcm9maWxlJwo=</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLmlucHV0cmMnIC0+IGAvaG9tZS9hZG1pbi8vLmlucHV0cmMnCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLnByb2ZpbGUnIC0+IGAvaG9tZS9hZG1pbi8vLnByb2ZpbGUnCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>WW91ciBncm91cCBpcyBjdXJyZW50bHkgIm1rcGFzc3dkIi4gIFRoaXMgaW5kaWNhdGVzIHRoYXQgeW91cgo=</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>Z2lkIGlzIG5vdCBpbiAvZXRjL2dyb3VwIGFuZCB5b3VyIHVpZCBpcyBub3QgaW4gL2V0Yy9wYXNzd2QuCgpUaGUgL2V0Yy9wYXNzd2QgKGFuZCBwb3NzaWJseSAvZXRjL2dyb3VwKSBmaWxlcyBzaG91bGQgYmUgcmVidWlsdC4KU2VlIHRoZSBtYW4gcGFnZXMgZm9yIG1rcGFzc3dkIGFuZCBta2dyb3VwIHRoZW4sIGZvciBleGFtcGxlLCBydW4KCm1rcGFzc3dkIC1sIFstZF0gPiAvZXRjL3Bhc3N3ZApta2dyb3VwICAtbCBbLWRdID4gL2V0Yy9ncm91cAoKTm90ZSB0aGF0IHRoZSAtZCBzd2l0Y2ggaXMgbmVjZXNzYXJ5IGZvciBkb21haW4gdXNlcnMuCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' End='true' Name='stdout'/><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' End='true' Name='stderr'/><rsp:CommandState CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' State='http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Done'><rsp:ExitCode>0</rsp:ExitCode></rsp:CommandState></rsp:ReceiveResponse></s:Body></s:Envelope>";

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CmdIDResponseXML)));

    	String commandID = _winrmShell.executeCommand("powershell -ExecutionPolicy Bypass -Command '&amp;{./c678350d-7c8b-4ac2-9d32-785d856453b8_wrapper.ps1;exit $LASTEXITCODE}'",DEFAULT_WINRM_TEST_TIMEOUT);

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CMDSTDOUT)));

        final StringBuilder stdout = new StringBuilder();
        final StringBuilder stderr = new StringBuilder();

        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout);
        Assert.assertTrue(stdout.toString().contains("Copying skeleton files"));
    }

    @Test
    public void TestStdErrorOutput() throws Exception
    {
    	int timeout = 3600;
    	final String CMDSTDOUT = "<s:Envelope xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd' xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell' xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xml:lang='en-US'><s:Header><a:Action>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/ReceiveResponse</a:Action><a:MessageID>uuid:FBAF3D07-D043-4D4B-BCD4-DCC91AD1EE2C</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:725A4502-1BB5-42D2-BA53-D98F87130CB8</a:RelatesTo></s:Header><s:Body><rsp:ReceiveResponse><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>Q29weWluZyBza2VsZXRvbiBmaWxlcy4K</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>VGhlc2UgZmlsZXMgYXJlIGZvciB0aGUgdXNlcnMgdG8gcGVyc29uYWxpc2UgdGhlaXIgY3lnd2luIGV4cGVyaWVuY2UuCgpUaGV5IHdpbGwgbmV2ZXIgYmUgb3ZlcndyaXR0ZW4gbm9yIGF1dG9tYXRpY2FsbHkgdXBkYXRlZC4KCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLmJhc2hyYycgLT4gYC9ob21lL2FkbWluLy8uYmFzaHJjJwo=</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLmJhc2hfcHJvZmlsZScgLT4gYC9ob21lL2FkbWluLy8uYmFzaF9wcm9maWxlJwo=</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLmlucHV0cmMnIC0+IGAvaG9tZS9hZG1pbi8vLmlucHV0cmMnCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLnByb2ZpbGUnIC0+IGAvaG9tZS9hZG1pbi8vLnByb2ZpbGUnCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>WW91ciBncm91cCBpcyBjdXJyZW50bHkgIm1rcGFzc3dkIi4gIFRoaXMgaW5kaWNhdGVzIHRoYXQgeW91cgo=</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>Z2lkIGlzIG5vdCBpbiAvZXRjL2dyb3VwIGFuZCB5b3VyIHVpZCBpcyBub3QgaW4gL2V0Yy9wYXNzd2QuCgpUaGUgL2V0Yy9wYXNzd2QgKGFuZCBwb3NzaWJseSAvZXRjL2dyb3VwKSBmaWxlcyBzaG91bGQgYmUgcmVidWlsdC4KU2VlIHRoZSBtYW4gcGFnZXMgZm9yIG1rcGFzc3dkIGFuZCBta2dyb3VwIHRoZW4sIGZvciBleGFtcGxlLCBydW4KCm1rcGFzc3dkIC1sIFstZF0gPiAvZXRjL3Bhc3N3ZApta2dyb3VwICAtbCBbLWRdID4gL2V0Yy9ncm91cAoKTm90ZSB0aGF0IHRoZSAtZCBzd2l0Y2ggaXMgbmVjZXNzYXJ5IGZvciBkb21haW4gdXNlcnMuCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' End='true' Name='stdout'/><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' End='true' Name='stderr'>VGhlc2UgZmlsZXMgYXJlIGZvciB0aGUgdXNlcnMgdG8gcGVyc29uYWxpc2UgdGhlaXIgY3lnd2luIGV4cGVyaWVuY2UuCgpUaGV5IHdpbGwgbmV2ZXIgYmUgb3ZlcndyaXR0ZW4gbm9yIGF1dG9tYXRpY2FsbHkgdXBkYXRlZC4KCg==</rsp:Stream><rsp:CommandState CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' State='http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Done'><rsp:ExitCode>0</rsp:ExitCode></rsp:CommandState></rsp:ReceiveResponse></s:Body></s:Envelope>";

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CmdIDResponseXML)));

    	String commandID = _winrmShell.executeCommand("powershell -ExecutionPolicy Bypass -Command '&amp;{./c678350d-7c8b-4ac2-9d32-785d856453b8_wrapper.ps1;exit $LASTEXITCODE}'",DEFAULT_WINRM_TEST_TIMEOUT);

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CMDSTDOUT)));

        final StringBuilder stdout = new StringBuilder();
        final StringBuilder stderr = new StringBuilder();

        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout);
        Assert.assertTrue(stderr.toString().contains("They will never be overwritten"));
    }

    @Test
    public void TestStdOutOutputWithNegativeThreeExitCode() throws Exception
    {
    	int timeout = 3600;
    	final String CMDSTDOUT = "<s:Envelope xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:p='http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd' xmlns:rsp='http://schemas.microsoft.com/wbem/wsman/1/windows/shell' xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd' xml:lang='en-US'><s:Header><a:Action>http://schemas.microsoft.com/wbem/wsman/1/windows/shell/ReceiveResponse</a:Action><a:MessageID>uuid:FBAF3D07-D043-4D4B-BCD4-DCC91AD1EE2C</a:MessageID><a:To>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:To><a:RelatesTo>uuid:725A4502-1BB5-42D2-BA53-D98F87130CB8</a:RelatesTo></s:Header><s:Body><rsp:ReceiveResponse><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>Q29weWluZyBza2VsZXRvbiBmaWxlcy4K</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>VGhlc2UgZmlsZXMgYXJlIGZvciB0aGUgdXNlcnMgdG8gcGVyc29uYWxpc2UgdGhlaXIgY3lnd2luIGV4cGVyaWVuY2UuCgpUaGV5IHdpbGwgbmV2ZXIgYmUgb3ZlcndyaXR0ZW4gbm9yIGF1dG9tYXRpY2FsbHkgdXBkYXRlZC4KCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLmJhc2hyYycgLT4gYC9ob21lL2FkbWluLy8uYmFzaHJjJwo=</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLmJhc2hfcHJvZmlsZScgLT4gYC9ob21lL2FkbWluLy8uYmFzaF9wcm9maWxlJwo=</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLmlucHV0cmMnIC0+IGAvaG9tZS9hZG1pbi8vLmlucHV0cmMnCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>YC4vLnByb2ZpbGUnIC0+IGAvaG9tZS9hZG1pbi8vLnByb2ZpbGUnCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>WW91ciBncm91cCBpcyBjdXJyZW50bHkgIm1rcGFzc3dkIi4gIFRoaXMgaW5kaWNhdGVzIHRoYXQgeW91cgo=</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' Name='stdout'>Z2lkIGlzIG5vdCBpbiAvZXRjL2dyb3VwIGFuZCB5b3VyIHVpZCBpcyBub3QgaW4gL2V0Yy9wYXNzd2QuCgpUaGUgL2V0Yy9wYXNzd2QgKGFuZCBwb3NzaWJseSAvZXRjL2dyb3VwKSBmaWxlcyBzaG91bGQgYmUgcmVidWlsdC4KU2VlIHRoZSBtYW4gcGFnZXMgZm9yIG1rcGFzc3dkIGFuZCBta2dyb3VwIHRoZW4sIGZvciBleGFtcGxlLCBydW4KCm1rcGFzc3dkIC1sIFstZF0gPiAvZXRjL3Bhc3N3ZApta2dyb3VwICAtbCBbLWRdID4gL2V0Yy9ncm91cAoKTm90ZSB0aGF0IHRoZSAtZCBzd2l0Y2ggaXMgbmVjZXNzYXJ5IGZvciBkb21haW4gdXNlcnMuCg==</rsp:Stream><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' End='true' Name='stdout'/><rsp:Stream CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' End='true' Name='stderr'/><rsp:CommandState CommandId='8EE34B99-7CD1-4A34-BE66-6ED3259B019B' State='http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Done'><rsp:ExitCode>4294967293</rsp:ExitCode></rsp:CommandState></rsp:ReceiveResponse></s:Body></s:Envelope>";

    	stubFor(post(urlEqualTo("/wsman"))
    	        .willReturn(aResponse().withStatus(200)
    	                               .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
    	                               .withBody(CmdIDResponseXML)));

    	String commandID = _winrmShell.executeCommand("powershell -ExecutionPolicy Bypass -Command '&amp;{./c678350d-7c8b-4ac2-9d32-785d856453b8_wrapper.ps1;exit $LASTEXITCODE}'",DEFAULT_WINRM_TEST_TIMEOUT);

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CMDSTDOUT)));

        final StringBuilder stdout = new StringBuilder();
        final StringBuilder stderr = new StringBuilder();

        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout);
        Assert.assertTrue(stdout.toString().contains("Copying skeleton files"));
    }

    @Test(expected=ExecutionException.class)
    @Ignore
    public void testSocketTimeouts()
        throws Exception
    {
        final int timeout = 1;

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdIDResponseXML)));

        String commandID = _winrmShell.executeCommand("powershell -ExecutionPolicy Bypass -Command '&amp;{./c678350d-7c8b-4ac2-9d32-785d856453b8_wrapper.ps1;exit $LASTEXITCODE}'",DEFAULT_WINRM_TEST_TIMEOUT);

        stubFor(post(urlEqualTo("/wsman"))
                .willReturn(aResponse().withStatus(200)
                                       .withHeader("Content-Type", "application/soap+xml;charset=UTF-8")
                                       .withBody(CmdResultXML)
                                       .withFixedDelay(2000)));

        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();

        _winrmShell.receiveResultSet(commandID, stdout, stderr, timeout);
    }
}
