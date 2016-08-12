package com.servicemesh.io.http;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

//
// Utility filter used to tag all untagged nodes w/ the required namespace.
//

public class NamespaceFilter extends XMLFilterImpl
{

    private String _namespace;
    private boolean _attributes;

    public NamespaceFilter(String namespace, boolean attributes)
    {
        super();
        _namespace = namespace;
        _attributes = attributes;
    }

    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
    }

    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes attr) throws SAXException
    {

        // Get the number of attribute
        int length = attr.getLength();

        // Process each attribute
        if (_attributes)
        {
            AttributesImpl impl = new AttributesImpl();
            impl.clear();
            for (int i = 0; i < length; i++)
            {
                impl.addAttribute(_namespace, attr.getLocalName(i), attr.getQName(i), attr.getType(i), attr.getValue(i));
            }
            super.startElement(_namespace, arg1, arg2, impl);
        }
        else
        {
            super.startElement(_namespace, arg1, arg2, attr);
        }
    }

    @Override
    public void endElement(String arg0, String arg1, String arg2) throws SAXException
    {
        super.endElement(_namespace, arg1, arg2);
    }

    @Override
    public void startPrefixMapping(String prefix, String url) throws SAXException
    {
    }

}
