/*
Copyright (c) 2013, ServiceMesh Inc.
All rights reserved.

*/

//----------------------------------------------------------------------------
//
//  File:       WinRMUtils.java
//
//  Contents:   
//
//  Notes:      
//              
//
//----------------------------------------------------------------------------

package com.servicemesh.io.rsh.winrm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import intel.management.wsman.WsmanUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class WinRMUtils {

	String TargetAddress;
	
	public static final WinRMUtils newInstance(String targetAddress)
	{
		return new WinRMUtils(targetAddress);
	}
	
	public WinRMUtils(String targetAddress)
	{
		TargetAddress = targetAddress;
	
	}
	
	 /**
     * Initialize XML request document ( To address and message identifier - unique GUID
     *
     * @param request document
     *
     *
     *
     */    
	public void InitializeMessage(Document document)
	{
		
	    Element headerElt=WsmanUtils.findChild(document.getDocumentElement(),
	            WsmanUtils.SOAP_NAMESPACE,
	            "Header");
	
	    Element aElt=WsmanUtils.findChild(headerElt,
	            WsmanUtils.ADDRESSING_NAMESPACE,
	            "To");
	    
	    aElt.setTextContent(TargetAddress);
	
	    Element msgElt=WsmanUtils.findChild(headerElt,
	            WsmanUtils.ADDRESSING_NAMESPACE,
	            "MessageID");
	    
	    if (msgElt != null) {
	    	  String messageId=null;
	        StringBuffer buffer = new StringBuffer();
	        buffer.append("uuid:");
	        buffer.append(UUID.randomUUID().toString().toUpperCase());
	        messageId=buffer.toString();
	        msgElt.setTextContent(messageId);
	    }
	
	    
	}

/**
 * Set Body text 
 *
 * @param request document and body text ( format should be XML )
 *
 *
 *
 */
	public  void SetBody(String bodyText, Document document)
	{
	
		Element bElt=WsmanUtils.findChild(document.getDocumentElement(),
	            WsmanUtils.SOAP_NAMESPACE,
	            "Body");
	    
		try {
			Element node =  DocumentBuilderFactory
				    .newInstance()
				    .newDocumentBuilder()
				    .parse(new ByteArrayInputStream(bodyText.getBytes()))
				    .getDocumentElement();
			bElt.appendChild(document.importNode(node, true));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	
	 /**
     * Retrieve Command ID
     *
     * @param response XML document
     *
     *
     *
     */
	
	public  String getCommandId(Document document)
	{
		
        Element headrefElt=WsmanUtils.findChild(document.getDocumentElement(),
        		WsmanUtils.SOAP_NAMESPACE,
                "Body");

        Element resElt=WsmanUtils.findChild(headrefElt,
	        		WsmanUtils.WINRSURI_NAMESPACE,
	                "CommandResponse");
        
        Element refElt=WsmanUtils.findChild(resElt,
        		WsmanUtils.WINRSURI_NAMESPACE,
                "CommandId");
 
        String result="";
        if ( refElt != null )
        	result = refElt.getTextContent();
        return result;
	}
	
	 /**
     * Retrieve result set
     *
     * @param response document
     *
     */
	
	public <T extends OutputStream> Integer getOutputValues(Document document, T stdout, T stderr) throws IOException
	{	
		NodeList nodes=null;
	    Element headrefElt=WsmanUtils.findChild(document.getDocumentElement(),
	        		WsmanUtils.SOAP_NAMESPACE,
	                "Body");
	         
	    Element resElt=WsmanUtils.findChild(headrefElt,
		        		WsmanUtils.WINRSURI_NAMESPACE,
		                "ReceiveResponse");
	         
	    if (resElt!=null)
            nodes  = resElt.getChildNodes();

	    for (int i=0; nodes!=null && i< nodes.getLength();i++) 
        {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
            	continue;
            
            String nodeName = node.getLocalName();
            if(nodeName.equals("Stream") && ((Element)node).hasAttribute("Name"))
            {	            	
            	String name = ((Element)node).getAttribute("Name");
            	if(stdout != null && name.equals("stdout"))
            	{
	            	//Base64Decoder base64 = new Base64();
	            	byte[] decode = WsmanUtils.decode(node.getTextContent());
	            	stdout.write(decode);
            	}
            	else if (stderr != null && name.equals("stderr"))
            	{
	            	//Base64Decoder base64 = new Base64();
	            	byte[] decode = WsmanUtils.decode(node.getTextContent());
	            	stderr.write(decode);	            		
            	}
            }
            
            if(nodeName.equals("CommandState") && ((Element)node).hasAttribute("State"))
            {
            	String state = ((Element)node).getAttribute("State");
            	if(state.equals(WsmanUtils.WINRSCOMMANDDONEURI_NAMESPACE))
            	{
	            	NodeList children = node.getChildNodes();
	            	for(int c=0; children!=null && c < children.getLength(); c++)
	            	{
	            		Node child = children.item(c);
	            		if(child.getLocalName().equals("ExitCode"))
	            		{
	            			String exitCode = child.getTextContent();
	            			long longExitCode = Long.parseLong(exitCode);
	            			return (int)longExitCode;
	            		}
	            	}
            	}
            }
        }
        return null;
	}

	 /**
     * Retrieve result set
     *
     * @param response document
     *
     */
	
	public Integer getOutputValues(Document document, StringBuilder stdout, StringBuilder stderr) throws IOException
	{	
		NodeList nodes=null;
	    Element headrefElt=WsmanUtils.findChild(document.getDocumentElement(),
	        		WsmanUtils.SOAP_NAMESPACE,
	                "Body");
	         
	    Element resElt=WsmanUtils.findChild(headrefElt,
		        		WsmanUtils.WINRSURI_NAMESPACE,
		                "ReceiveResponse");
	         
	    if (resElt!=null)
            nodes  = resElt.getChildNodes();

	    for (int i=0; nodes!=null && i< nodes.getLength();i++) 
        {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
            	continue;
            
            String nodeName = node.getLocalName();
            if(nodeName.equals("Stream") && ((Element)node).hasAttribute("Name"))
            {	            	
            	String name = ((Element)node).getAttribute("Name");
            	if(stdout != null && name.equals("stdout"))
            	{
	            	//Base64Decoder base64 = new Base64();
	            	byte[] decode = WsmanUtils.decode(node.getTextContent());
	            	stdout.append(new String(decode));
            	}
            	else if (stderr != null && name.equals("stderr"))
            	{
	            	//Base64Decoder base64 = new Base64();
	            	byte[] decode = WsmanUtils.decode(node.getTextContent());
	            	stderr.append(new String(decode));	            		
            	}
            }
            
            if(nodeName.equals("CommandState") && ((Element)node).hasAttribute("State"))
            {
            	String state = ((Element)node).getAttribute("State");
            	if(state.equals(WsmanUtils.WINRSCOMMANDDONEURI_NAMESPACE))
            	{
	            	NodeList children = node.getChildNodes();
	            	for(int c=0; children!=null && c < children.getLength(); c++)
	            	{
	            		Node child = children.item(c);
	            		if(child.getLocalName().equals("ExitCode"))
	            		{
	            			String exitCode = child.getTextContent();
	            			long longExitCode = Long.parseLong(exitCode);
	            			return (int)longExitCode;
	            		}
	            	}
            	}
            }
        }
        return null;
	}

	/**
     * Set the Resource identifier
     *
     * @param request document, name of resourceURI
     *
     *
     *
     */
 
 public  void SetResourceURI(String resourceURI, Document document) 
 {
        

	 Element headerElt=WsmanUtils.findChild(document.getDocumentElement(),
                WsmanUtils.SOAP_NAMESPACE,
                "Header");

     Element resElt=WsmanUtils.findChild(headerElt,
                WsmanUtils.WSMAN_NAMESPACE,
                "ResourceURI");
         
     resElt.setTextContent(resourceURI);
 }
 
 /**
     * Set the Action identifier
     *
     * @param request document, name of Action URI string
     *
     *
     *
     */
 public  void SetActionURI(String actionURI, Document document)
 {
	 Element headerElt=WsmanUtils.findChild(document.getDocumentElement(),
                WsmanUtils.SOAP_NAMESPACE,
                "Header");
	 
	 Element   resElt=WsmanUtils.findChild(headerElt,
	                WsmanUtils.ADDRESSING_NAMESPACE,
	                "Action");
	        
	 resElt.setTextContent(actionURI);
        
    }
	
}
