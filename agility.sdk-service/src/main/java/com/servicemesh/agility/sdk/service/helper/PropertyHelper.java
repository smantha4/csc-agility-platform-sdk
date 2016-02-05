/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.helper;

import java.io.Serializable;
import java.util.List;

import com.servicemesh.agility.api.AssetProperty;
import com.servicemesh.agility.api.AssetType;
import com.servicemesh.agility.api.Link;
import com.servicemesh.agility.api.Property;

/**
 * This abstract class provides utility classes that may prove useful to any adapter implementation.
 *
 * @author henry
 */
public abstract class PropertyHelper implements Serializable
{
    private static final long serialVersionUID = 20140117;

    /**
     * This method will find a property value from a list given the property key. If not found, null will be returned.
     * 
     * @param props
     *            List of Agility property objects
     * @param key
     *            Name of property for which to search
     * @param defaultValue
     *            Default value to return if the property was not found
     * @return String - the value associated with the property name. Null will be returned if the property is not found
     */
    public static String getValue(List<Property> props, String key, String defaultValue)
    {
        String retval = defaultValue;
        if ((props != null) && isValued(key))
        {
            for (Property property : props)
            {
                if (property.getName().equals(key))
                {
                    retval = property.getValue();
                    break;
                }
            }
        }

        return retval;
    }

    public static String getString(List<AssetProperty> props, String key, String defaultValue)
    {
        String retval = defaultValue;

        if ((props != null) && isValued(key))
        {
            for (AssetProperty property : props)
            {
                if (property.getName().equals(key))
                {
                    retval = property.getStringValue();
                    break;
                }
            }
        }

        return retval;
    }

    public static void setString(List<AssetProperty> props, String key, String value)
    {
        if ((props != null) && isValued(key))
        {
            for (AssetProperty property : props)
            {
                if (property.getName().equals(key))
                {
                    property.setStringValue(value);
                    ;
                    return;
                }
            }
            Link string_type = new Link();
            string_type.setName("string-any");
            string_type.setType("application/" + AssetType.class.getName() + "+xml");

            AssetProperty property = new AssetProperty();
            property.setName(key);
            ;
            property.setStringValue(value);
            property.setPropertyType(string_type);
            props.add(property);
        }
    }

    public static void setValue(List<Property> props, String key, String value)
    {
        if ((props != null) && isValued(key))
        {
            for (Property property : props)
            {
                if (property.getName().equals(key))
                {
                    property.setValue(value);
                    ;
                    return;
                }
            }
            Link string_type = new Link();
            string_type.setName("string-any");
            string_type.setType("application/" + AssetType.class.getName() + "+xml");

            Property property = new Property();
            property.setName(key);
            ;
            property.setValue(value);
            props.add(property);
        }
    }

    public static Integer getInteger(List<AssetProperty> props, String key, Integer defaultValue)
    {
        Integer retval = defaultValue;

        if ((props != null) && isValued(key))
        {
            for (AssetProperty property : props)
            {
                if (property.getName().equals(key))
                {
                    retval = property.getIntValue();
                    break;
                }
            }
        }

        return retval;
    }

    public static Boolean getBoolean(List<AssetProperty> props, String key, Boolean defaultValue)
    {
        Boolean retval = defaultValue;

        if ((props != null) && isValued(key))
        {
            for (AssetProperty property : props)
            {
                if (property.getName().equals(key))
                {
                    retval = property.isBooleanValue();
                    break;
                }
            }
        }

        return retval;
    }

    /**
     * Gets a property with the default value being null.
     * 
     * @see #getValue(List, String, String)
     */
    public static String getProperty(List<Property> props, String key)
    {
        return getValue(props, key, null);
    }

    /**
     * This method will check to see if a string object has a value. This means it cannot be null or empty.
     * 
     * @param s
     *            String value to be checked
     * @return boolean - true if the value is not null and not empty
     */
    public static boolean isValued(String s)
    {
        return ((s != null) && !s.isEmpty());
    }

    /**
     * Create a property without a description.
     * 
     * @see #newProperty(String, String, String)
     */
    public static Property newProperty(String name, String value)
    {
        return newProperty(name, null, value);
    }

    /**
     * This method will return a populated Agility Property object give the individual values.
     * 
     * @param name
     *            Name of the property being created
     * @param description
     *            Description of the property
     * @param value
     *            Value of the property
     * @return Property - populated object. Null will not be returned; however, the name will be null if not provided or the name
     *         parameter is null.
     */
    public static Property newProperty(String name, String description, String value)
    {
        Property property = new Property();

        property.setName(name);
        property.setDescription(description);
        property.setValue(value);

        return property;
    }

}
