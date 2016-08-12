package com.servicemesh.io.http;

import java.io.Serializable;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * This abstract class contains utility methods for the HTTP and supporting libraries. All methods should be defined as static.
 *
 * @author henry
 */
public abstract class HttpUtil implements Serializable
{
    private static final long serialVersionUID = 20140314;
    private static final Logger logger = Logger.getLogger(HttpUtil.class);

    /**
     * This method will filter values of key words that could be considered password or key information. This hides values in a
     * string that may be written to a log file.
     *
     * @param str
     *            Text that may have security-type key words
     * @return String - text with the possible security values masked
     */
    public static String filterpw(String str)
    {
        String filtered = str.replaceAll("(password=\")[^\"]*?(\")", "$1*********$2");
        filtered = filtered.replaceAll("(token id=\")[^\"]*?(\")", "$1*********$2");
        filtered = filtered.replaceAll("(<private_key>)[^>]*?(</private_key>)", "$1******$2");
        filtered = filtered.replaceAll("(adminPass=\")[^\"]*?(\")", "$1******$2");

        return filtered;
    }

    /**
     * This method will take an XML string along with a JAXB context object and convert the XML to a domain object. If the
     * namespace parameter is provided, the parser will be configured to be namespace aware, i.e. expect the XML to contain a
     * namespace prefix.
     *
     * @param xml
     *            XML string to be converted
     * @param namespace
     *            Value of the namespace prefix in the XML file
     * @param context
     *            JAXB context from which the domain object can be created
     * @return Object - this is a generic object that will need to be cast by the consumer of this method
     * @throws HttpClientException
     *             - wraps exceptions that may be thrown by the parsing and conversion process
     */
    @SuppressWarnings("rawtypes")
    public static Object decodeObject(String xml, String namespace, JAXBContext context) throws HttpClientException
    {
        Object retval = null;

        if (isValued(xml))
        {
            xml = xml.trim();

            logger.debug(filterpw(xml));

            try
            {
                Unmarshaller u = context.createUnmarshaller();

                if (namespace != null)
                {
                    SAXParserFactory sax = SAXParserFactory.newInstance();

                    sax.setNamespaceAware(true);

                    // Create an XMLReader to use with our filter
                    XMLReader xmlreader = sax.newSAXParser().getXMLReader();

                    // Create the filter (to add namespace) and set the xmlReader as its parent.
                    NamespaceFilter inFilter = new NamespaceFilter(namespace, false);
                    inFilter.setParent(xmlreader);

                    // Prepare the input, in this case a java.io.File (output)
                    InputSource is = new InputSource(new StringReader(xml));

                    // Create a SAXSource specifying the filter
                    SAXSource s = new SAXSource(inFilter, is);
                    retval = u.unmarshal(s);
                }
                else
                {
                    retval = u.unmarshal(new StringReader(xml));
                }

                if (retval instanceof JAXBElement)
                {
                    retval = ((JAXBElement) retval).getValue();
                }
            }
            catch (Exception e)
            {
                String msg = "An exception occurred while decoding response data.";

                logger.error(msg, e);
                throw new HttpClientException(e);
            }
        }
        else
        {
            logger.warn("The XML content was null.  Object cannot be created.");
        }

        return retval;
    }

    private static boolean isValued(String s)
    {
        return (s != null && !s.isEmpty());
    }
}
