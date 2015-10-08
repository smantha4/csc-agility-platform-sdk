/*
Copyright (c) 2009 - 2010, Intel Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer
      in the documentation and/or other materials provided with the distribution.
    * Neither the name of Intel Corporation nor the names of its contributors may be used to endorse or promote products derived from
      this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

//----------------------------------------------------------------------------
//
//  File:       WsmanUtils.java
//
//  Contents:   XML utility functions
//
//  Notes:      
//
//----------------------------------------------------------------------------

package intel.management.wsman;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateCrtKeySpec;



/**
 * Provides utility functions for various tasks such as XML processing,
 * base64 encoding and decoding, and static namespace definitions.
 *
 *
 * <P>
 *
 *
 * @see #enablePrettyFormatting(Boolean)
 * @see #loadDocument(InputStream)
 * @see #loadDocument(String)
 * @see #saveDocument(Document,OutputStream)
 * @see #formatDocument(Document)
 *
 */
public class WsmanUtils {

	
	
	   
	// common Wsman Namespaces
    public static final String SOAP_NAMESPACE ="http://www.w3.org/2003/05/soap-envelope";
    public static final String WSMAN_NAMESPACE ="http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd";
    public static final String ADDRESSING_NAMESPACE="http://schemas.xmlsoap.org/ws/2004/08/addressing";
    public static final String ENUMERATION_NAMESPACE="http://schemas.xmlsoap.org/ws/2004/09/enumeration";
    public static final String XMLSCHEMA_NAMESPACE="http://www.w3.org/2001/XMLSchema";
    public static final String WSMANTRANSFER_NAMESPACE = "http://schemas.xmlsoap.org/ws/2004/09/transfer";
    public static final String WSMANSHELL_NAMESPACE =  "http://schemas.microsoft.com/wbem/wsman/1/windows/shell";
	public static final String WSMANFAULT_NAMESPACE = "http://schemas.microsoft.com/wbem/wsman/1/wsmanfault";
    public static final String WINRSURI_NAMESPACE = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell";
    public static final String WINRSCMDURI_NAMESPACE = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/cmd";
    public static final String WINRSCREATEURI_NAMESPACE = "http://schemas.xmlsoap.org/ws/2004/09/transfer/Create";
    
    public static final String WINRSCOMMANDDONEURI_NAMESPACE = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Done";
    public static final String WINRSCOMMANDURI_NAMESPACE = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Command";
    public static final String WINRSSENDURI_NAMESPACE = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Send";
    public static final String WINRSRECEIVEURI_NAMESPACE = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Receive";
    public static final String WINRSDELETEURI_NAMESPACE = "http://schemas.xmlsoap.org/ws/2004/09/transfer/Delete";
    
    public static final String WINRSCREATEBODY = "<Shell xmlns='" + WINRSURI_NAMESPACE + "'><InputStreams>stdin</InputStreams><OutputStreams>stdout stderr</OutputStreams></Shell>";
    public static final String WINRSCOMMANDOPTION = "<w:OptionSet><w:Option Name='WINRS_CONSOLEMODE_STDIN'>FALSE</w:Option><w:Option Name='WINRS_SKIP_CMD_SHELL'>FALSE</w:Option></w:OptionSet>";
    public static final String WINRSCOMMANDBODY_BEGIN = "<CommandLine xmlns='" + WINRSURI_NAMESPACE  + "'><Command>{Command}</Command>";
    public static final String WINRSCOMMANDBODY_END = "</CommandLine>";
    public static final String WINRSRECEIVEBODY = "<Receive SequenceId='{SequenceId}' xmlns='" + WINRSURI_NAMESPACE + "'><DesiredStream CommandId='{CommandId}'>stdout stderr</DesiredStream></Receive>";
    public static final String WINRSSENDBODY_BEGIN = "<Send xmlns='" + WINRSURI_NAMESPACE + "'><Stream Name='stdin' SequenceId='{SequenceId}' CommandId='{CommandId}' End='{End}'>";
    public static final String WINRSSENDBODY_END = "</Stream></Send>";
   
    public static final String WSMANXMLTemplate = "<s:Envelope xmlns:s='http://www.w3.org/2003/05/soap-envelope' xmlns:a='http://schemas.xmlsoap.org/ws/2004/08/addressing' xmlns:w='http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd'> <s:Header><a:To>{Url}</a:To><w:ResourceURI s:mustUnderstand='true'>{ResourceUri}</w:ResourceURI><a:ReplyTo><a:Address s:mustUnderstand='true'>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:Address></a:ReplyTo><a:Action s:mustUnderstand='true'>{ActionUri}</a:Action><w:MaxEnvelopeSize s:mustUnderstand='true'>153600</w:MaxEnvelopeSize><a:MessageID>uuid:{MessageId}</a:MessageID><w:Locale xml:lang='en-US' s:mustUnderstand='false'></w:Locale><w:OperationTimeout>PT60.000S</w:OperationTimeout></s:Header><s:Body><!--Body--></s:Body></s:Envelope>";

   
    private DocumentBuilder builder;
    private Transformer transformer;

	private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static int[]  toInt   = new int[128];

    /**
     * Constructs a WsmanUtils object that can be used for XML rendering
     *
     *
     */
    public WsmanUtils() {

            for(int i=0; i< ALPHABET.length; i++){
                toInt[ALPHABET[i]]= i;
            }

        try {
            TransformerFactory transfac = TransformerFactory.newInstance();
            transformer = transfac.newTransformer();

            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


            dbf.setNamespaceAware(true);
            dbf.setIgnoringComments(true);
            builder = dbf.newDocumentBuilder();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
    
    private static String getCodes() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        buffer.append("abcdefghijklmnopqrstuvwxyz");
        buffer.append("0123456789");
        buffer.append("+/");
        return buffer.toString();
    }
    
    private static int getUInt(byte b) {
        return b & 0xFF;
    }



    /**
     * Translates the specified Base64 string into a byte array.
     *
     * @param s the Base64 string (not null)
     * @return the byte array (not null)
     */
    public static byte[] decode(String s)
    {

        int delta = s.endsWith( "==" ) ? 2 : s.endsWith( "=" ) ? 1 : 0;
        byte[] buffer = new byte[s.length()*3/4 - delta];
        int mask = 0xFF;
        int index = 0;
        for(int i=0; i< s.length(); i+=4){
            int c0 = toInt[s.charAt( i )];
            int c1 = toInt[s.charAt( i + 1)];
            buffer[index++]= (byte)(((c0 << 2) | (c1 >> 4)) & mask);
            if(index >= buffer.length){
                return buffer;
            }
            int c2 = toInt[s.charAt( i + 2)];
            buffer[index++]= (byte)(((c1 << 4) | (c2 >> 2)) & mask);
            if(index >= buffer.length){
                return buffer;
            }
            int c3 = toInt[s.charAt( i + 3 )];
            buffer[index++]= (byte)(((c2 << 6) | c3) & mask);
        }
        return buffer;
    } 

   
    /**
     * Get the bytes represented by a base64 encoded string
     *
     *
     * @param input A base64 encoded string
     *
     *
     *
     * @return
     * The bytes represented by the base64 encoded string
     *
     *
     */
    public static byte[] getBase64Bytes(String input) {
        // implements RFC 3548
        String codes = getCodes();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        int i=0;
        while (i < input.length()) {
            int enc1 = codes.indexOf(input.charAt(i++));
            int enc2 = codes.indexOf(input.charAt(i++));
            int enc3 = codes.indexOf(input.charAt(i++));
            int enc4 = codes.indexOf(input.charAt(i++));
            
            int chr1 = (enc1 << 2) | (enc2 >> 4);
            int chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            int chr3 = ((enc3 & 3) << 6) | enc4;

            out.write(chr1);
            if (enc3>=0 && enc3 != 64) {
                out.write(chr2);
            }
            
            if (enc4>=0 && enc4 != 64) {
                out.write(chr3);
            }
        }
        return out.toByteArray();
    }


     /**
     * Get base64 string encoded as an array of bytes
     *
     *
     * @param input An array of bytes
     *
     *
     *
     * @return
     * A base64 encoded string
     *
     *
     */
    public static String getBase64String(byte[] input)
    {
    	StringBuffer buffer = new StringBuffer();
    	getBase64String(input,input.length,buffer);
    	return buffer.toString();
    }
    
    public static void getBase64String(byte[] input, int count, StringBuffer buffer)  
    {
        // implements RFC 3548
        String codes = getCodes();
        int padding = (3 - (count % 3)) % 3;
        byte[] paddedInput = new byte[count+padding];

        System.arraycopy(input, 0, paddedInput, 0, count);
        
        for (int i = 0; i < paddedInput.length; i += 3) {
            int j = (getUInt(paddedInput[i]) << 16) +
                    (getUInt(paddedInput[i + 1]) << 8) +
                    getUInt(paddedInput[i + 2]);

            buffer.append((char)codes.charAt((j >> 18) & 0x3f));
            buffer.append((char)codes.charAt((j >> 12) & 0x3f));
            buffer.append((char)codes.charAt((j >> 6) & 0x3f));
            buffer.append((char)codes.charAt(j & 0x3f));
        }
        
        for (int i=0; i< padding;i++)
            buffer.setCharAt(buffer.length() - (i+1), '=');
    }


    /**
     * Enable pretty formatting when converting XML to a string
     *
     *
     * @param enabled Set to <code>true</code> for pretty formatting
     *
     *
     *
     *
     */
    public void enablePrettyFormatting(Boolean enabled) {
        if (enabled)
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //transformer.setOutputProperty(javax.xml.transform.OutputKeys.
        else
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
    }


     /**
     * Remove all children from a XML parent
     *
     *
     * @param parent The element to remove all children from
     *
     *
     *
     */
    public static void removeChildren(Element parent) {

        Node node = parent.getFirstChild();
        while (node!=null) {
            Node removeNode = node;
            node=node.getNextSibling();
            parent.removeChild(removeNode);
        }
    }

     /**
     * Find the first child element in a XML document
     *
     *
     * @param parent The element to start with
     *
     *@return
     * The first child element or <code>null</code> if none exists
     *
     */
    public static Element findChild(Element parent) {
        Element foundElement = null;
        Node node = parent.getFirstChild();
        while (node!=null) {
            if (node.getNodeType()==Node.ELEMENT_NODE) {
                foundElement = (Element)node;
                break;
            }
            node=node.getNextSibling();
        }
        return foundElement;
    }

     /**
     * Find a named element in a XML document
     *
     *
     * @param parent The element to start with
     * @param namespace The namespace of the element to find
     * @param name The name of the element to find
     *
     *
     *@return
     * The found child element or <code>null</code> if none exists
     */
    public static Element findChild(Element parent, String namespace, String name) {

        Element foundElement = null;
        Node node = parent.getFirstChild();
        while (node!=null) {
            if (node.getNodeType()==Node.ELEMENT_NODE) {
                foundElement = (Element)node;
                String local = foundElement.getLocalName();
                String ns = foundElement.getNamespaceURI();
                if (local !=null && local.equals(name) &&
                        ns !=null && ns.equals(namespace)) {
                    foundElement = (Element)node;
                    break;
                }
            }
            foundElement=null;
            node=node.getNextSibling();
        }
        return foundElement;
    }


     /**
     * Create an endpoint refrence XML document
     *
     *
     * @param address The address element
     * @param ref The reference parameters element
     *
     *
     *@return
     * The XML representation of an endpoint reference
     */
    public static Document createEpr(Element address, Element ref) {

        Document document = address.getOwnerDocument().getImplementation().createDocument(
                ref.getNamespaceURI(),"EndpointReference",null);

        Element elt = document.getDocumentElement();
        
        elt.setPrefix(address.getPrefix());

        Node node = document.importNode(address, true);
        elt.appendChild(node);

        node = document.importNode(ref, true);
        elt.appendChild(node);

        return document;
    }

     /**
     * Set the endpoint reference of a XML request
     *
     *
     * @param src The reference in a source document
     * @param dest The destination document
     *
     *
     *
     */
    public static void setEpr(Node src,Document dest) {

        if (src.getNodeType()==Node.DOCUMENT_NODE)
            src = ((Document)src).getDocumentElement();

        Element refElt=WsmanUtils.findChild((Element)src,
                WsmanUtils.ADDRESSING_NAMESPACE,
                "ReferenceParameters");

        Element srcResElt=WsmanUtils.findChild(refElt,
                WsmanUtils.WSMAN_NAMESPACE,
                "ResourceURI");

        Element srcElt=WsmanUtils.findChild(refElt,
                WsmanUtils.WSMAN_NAMESPACE,
                "SelectorSet");



        Element headerElt=WsmanUtils.findChild(dest.getDocumentElement(),
                WsmanUtils.SOAP_NAMESPACE,
                "Header");

        if (headerElt==null)
             headerElt=WsmanUtils.findChild(dest.getDocumentElement(),
                WsmanUtils.ADDRESSING_NAMESPACE,
                "ReferenceParameters");


        Element dstElt=WsmanUtils.findChild(headerElt,
                WsmanUtils.WSMAN_NAMESPACE,
                "SelectorSet");

        Element dstResElt=WsmanUtils.findChild(headerElt,
                WsmanUtils.WSMAN_NAMESPACE,
                "ResourceURI");

        //Set the selectiors
        if (srcElt !=null ) {
            Node node = dest.importNode(srcElt, true);
            if (dstElt!=null)
                headerElt.removeChild(dstElt);
            headerElt.appendChild(node);
        }

        // Set the resourceURI
        if (dstResElt!=null && srcResElt !=null)
            dstResElt.setTextContent(srcResElt.getTextContent());

    }


     /**
     * Base64 encode a private key
     *
     * @param key The key to encode
     *
     * @return
     * The base64 encoded private key
     *
     */
    public static String EncodePrivateKey(java.security.Key key) throws WsmanException  {
        
        String keyData=null;
        try {
            KeyFactory fact = java.security.KeyFactory.getInstance("RSA");
            
            RSAPrivateCrtKeySpec spec = fact.getKeySpec(key, RSAPrivateCrtKeySpec.class);
            
            
            // get RSA paramaters
            byte[] mod=spec.getModulus().toByteArray();
            byte[] exp=spec.getPublicExponent().toByteArray();
            byte[] p =spec.getPrimeP().toByteArray();
            byte[] q = spec.getPrimeQ().toByteArray();
            byte[] dp = spec.getPrimeExponentP().toByteArray();
            byte[] dq = spec.getPrimeExponentQ().toByteArray();
            byte[] d = spec.getPrivateExponent().toByteArray();
            byte[] iq = spec.getCrtCoefficient().toByteArray();
            byte buffer[] = new byte[4096];
            int index=7;
            
            Object params[] = {mod,exp,d,p,q,dp,dq,iq};
            
            for (int i=0;i<params.length;i++) {
                
                byte param[] = (byte[])params[i];
                buffer[index++]=(byte)2;
                   
                int len = param.length;
       
                //boolean pad = (param[0] & 0x80) >0;
                boolean pad = (param.length > 0x80) && (param[0] !=0);


                if (pad) len++;
                int sizeType=0;
                
                if (len < 0x80)
                    sizeType = 0x80;
                else if (len <= 0xFF)
                    sizeType = 0x81;
                else if (len <= 0xFFFF)
                    sizeType = 0x82;
                
                
                switch (sizeType) {
                    case 0x81:
                       buffer[index++]=(byte)sizeType;
                       buffer[index++]=(byte)len;
                       break;
                    case 0x82:
                        buffer[index++]=(byte)sizeType;
                        buffer[index++]= (byte)(len >>8);//len[1]
                        buffer[index++]=(byte)(len & 0x000000FF);//len[0]
                        break;
                    default:
                        buffer[index++]=(byte)len;
                        break;
                }
                if (pad)
                    buffer[index++]=(byte)0;
                // copy the param             
                for (int j=0;j<param.length;j++)
                    buffer[index++]=param[j];

           }
            
            // private key header
            buffer[4]=(byte)2;//tag
            buffer[5]=(byte)1;//size
            buffer[6]=(byte)0;//version
            if (index<256) {
                
                buffer[1]=(byte)48;
                buffer[2]=(byte)129;
                index=index+3;
                buffer[3]=(byte)index;
                
                //file must be DWORD bock readable
                while ((index % 8)>0) index++;
                
                byte[] temp = new byte[index-1];
                System.arraycopy(buffer, 1, temp, 0, temp.length);
                buffer=temp;
            }
            else {

                int len = index-4;
                buffer[0]=(byte)48;
                buffer[1]=(byte)130;
              
                buffer[2]= (byte)(len >>8);//len[1]
                buffer[3]=(byte)(len & 0x000000FF);//len[0]
                
                while (index < 1190 && (index % 8)>0) index++;

                byte[] temp = new byte[index];
                System.arraycopy(buffer, 0, temp, 0, temp.length);
                buffer=temp;
            }
            keyData= WsmanUtils.getBase64String(buffer);
        }
        catch (Exception e) {
            throw new WsmanException(e);

        }
        return keyData;
    }



    /**
     * Load a document from a given element
     *
     * @param elt The source element to use
     *
     * @return
     * The document with the element loaded
     *
     */
    public static Document loadDocument(Element elt)  {
        Document doc = elt.getOwnerDocument().getImplementation().createDocument(null, null, null);
        Node node = doc.importNode(elt, true);
        doc.appendChild(node);
        return doc;
    }


      

     
     /**
     * Create a XML document for a given class and namespaceURI
     *
     * @param className The class name to use
     * @param namespaceURI The namespace to use
     *
     * @return
     * A newly created XML document
     *
     */
    public Document createDocument(String className, String namespaceURI) {

        Document document = builder.newDocument();

        Element elt = document.createElementNS(className,namespaceURI);
        elt.setPrefix("p");
        document.appendChild(elt);

        return document;

    }
    /**
     * Load a XML document from a resource loader
     *
     * @param name The resource name to load
     *
     * @return
     * The loaded XML document
     *
     */
    public Document loadDocument(String name) throws WsmanException {

        InputStream stream =
                WsmanUtils.class.getResourceAsStream(name);
        return loadDocument(stream);
      
    }

    public Document loadDocumentFromString(String xmlstring) throws Exception{
    	
    	InputStream stream = new ByteArrayInputStream(xmlstring.getBytes());
    	
    	return loadDocument(stream);
    }


    /**
     * Load a XML document from an input stream
     *
     * @param stream The stream to load the document from
     *
     * @return
     * The loaded XML document
     *
     */
    public Document loadDocument(InputStream stream ) throws WsmanException {
       try {
            return builder.parse(stream);
       }
       catch (Exception exception) {
            throw new WsmanException(exception);
       }
    }


/**
 * Format the given Intel(r) AMT Platform UUID into the standard GUID format that management applications expect
 * @param uuid The UUID string provided by <code>CIM_ComputerSystemPackage PlatformGUID</code> property
 */

    public static String formatUUID(String uuid) {

        byte enc[]=null; //temp buffer
        //first remove dashes
        String internal=uuid.replaceAll("-","");
        // assume base64 encoded if string 16*1.37
        if (internal.length()<32) {
        
           enc=WsmanUtils.getBase64Bytes(internal);
        }

        int data[] = new int[16];

        // convert to array of bytes
        int pos=0;
        for (int i=0; i< 16; i++) {

            if (enc!=null)
                data[i]=(int)enc[i];
            else
                data[i]=Integer.valueOf(internal.substring(pos, pos+2), 16);

            if (data[i]<0)
                data[i]=256+data[i];

            pos=pos+2;
        }

        // Swap the bytes
        int t = data[3];
        data[3] = data[0];
        data[0] = t;
        t = data[2];
        data[2] = data[1];
        data[1] = t;
        t = data[5];
        data[5] = data[4];
        data[4] = t;
        t = data[7];
        data[7] = data[6];
        data[6] = t;

        //now rebuild string with dashes
        StringBuilder builder= new StringBuilder();
        for (int i=0; i< 16; i++) {
            if (i==4||i==6||i==8||i==10)
                builder.append("-");
            if (data[i]<16)
                builder.append("0");
            builder.append(Integer.toHexString(data[i]));

        }
        return builder.toString();
    }



     /**
     * Base64 encode a UUID
     *
     * @param uuid The UUID to encode
     *
     * @return
     * The base64 encoded UUID
     *
     */
    public static String getUuidAsBase64(String uuid)
    {
        String internal=uuid.replaceAll("-","");

        byte data[] = new byte[16];


        // convert to array of bytes
        int pos=0;
        for (int i=0; i< 16; i++) {

            int b=Integer.valueOf(internal.substring(pos, pos+2), 16);

            if (b<0)
                b=256+b;

            data[i]=(byte)b;

            pos=pos+2;
        }

         return getBase64String(data);

    }

  

    /**
     * Save a XML document to a stream
     *
     * @param stream The stream to write the XML document to
     *
     *
     */
    public void saveDocument(Document doc, OutputStream stream)
            throws WsmanException {

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(stream);

        try {
            transformer.transform(source, result);
        }
        catch (Exception exception) {
            throw new WsmanException(exception);
        }

    }


     /**
     * Get a XML document as a formatted string
     *
     * @param doc The XML document to format
     *
     * @return
     * The document as formatted XML
     */
    public String formatDocument(Document doc)  {
     
        DOMSource source = new DOMSource(doc);

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
   
        try {
            transformer.transform(source, result);
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return sw.toString();
    }

/**
 * Get the WS-Man Java Library specification version from the package manifest.
 */
    public static String getLibSpecVersion ()
    {
        WsmanUtils utils = new WsmanUtils();
        return utils.getClass().getPackage().getSpecificationVersion();
    }

/**
 * Get the WS-Man Java Library specification vendor from the manifest.
 * This value should always be <b>'Intel Corporation'</b>.
 */
    public static String getLibSpecVendor ()
    {
        WsmanUtils utils = new WsmanUtils();
        return utils.getClass().getPackage().getSpecificationVendor();
    }

 /**
 * Get the WS-Man Java Library specification title from the manifest.
 * This value should always be <b>'Intel WS-Management Java Client Library'</b>.
 */
   public static String getLibSpecTitle ()
    {
        WsmanUtils utils = new WsmanUtils();
        return utils.getClass().getPackage().getSpecificationTitle();
    }

/**
 * Get the WS-Man Java Library implementation version from the package manifest.
 * This value denotes the actual library version in the form <b>X.Y.Z</b>:<br><br>
 *      <b>X =</b> Major Version Number.  A rev of the major version number would likely mean a major rewrite or change to the interface has taken place.<br>
 *	    <b>Y =</b> Minor Version Number.  This value gets bumped every time significant functional additions are made to the library.<br>
 *	    <b>Z =</b> Bug fixes.  This value gets bumped every time bug-fixes or groups of bug fixes are made to the library.<br>
 */
    public static String getLibImplementationVersion()
    {
        WsmanUtils utils = new WsmanUtils();
        return utils.getClass().getPackage().getImplementationVersion();
    }

 /**
 * Get the WS-Man Java Library implementation vendor from the manifest.
 * This value should always reflect the name of the vendor who is publishing the library. If the library has been modified by a 3rd party, then the
  * <b>Implementation-Vendor:</b> entry in the manifest should be updated to reflect the vendors name.
 */
    public static String getLibImplementationVendor()
    {
        WsmanUtils utils = new WsmanUtils();
        return utils.getClass().getPackage().getImplementationVendor();
    }

 /**
 * Get the WS-Man Java Library implementation title from the manifest.
 */
    public static String getLibImplementationTitle()
    {
        WsmanUtils utils = new WsmanUtils();
        return utils.getClass().getPackage().getImplementationTitle();
    }

 /**
 * Get the WS-Man Java Library package name.
 */
    public static String getLibPackageName()
    {
        WsmanUtils utils = new WsmanUtils();
        return utils.getClass().getPackage().getName();
    }

 
}
