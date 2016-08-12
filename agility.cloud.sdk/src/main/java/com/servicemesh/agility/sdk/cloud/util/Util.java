/**
 *              COPYRIGHT (C) 2008-2012 SERVICEMESH, INC.
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

package com.servicemesh.agility.sdk.cloud.util;

import java.io.Serializable;
import java.util.List;

import com.servicemesh.agility.api.Property;
import com.servicemesh.agility.sdk.cloud.msgs.FileSystemDeviceMapping;
import com.servicemesh.agility.sdk.cloud.msgs.OSVersionSpecificDevice;
import com.servicemesh.agility.sdk.cloud.msgs.PropertyName;
import com.servicemesh.agility.sdk.cloud.msgs.SyncKey;

/**
 * This abstract class provides utility classes that may prove useful to any adapter implementation.
 *
 * @author henry
 */
public abstract class Util implements Serializable
{
    private static final long serialVersionUID = 20140117;

    public static final int[] MASKS = new int[] { 0xFFFFFFFF, 0x7FFFFFFF, 0x3FFFFFFF, 0x1FFFFFFF, 0x0FFFFFFF, 0x07FFFFFF,
            0x03FFFFFF, 0x01FFFFFF, 0x00FFFFFF, 0x007FFFFF, 0x003FFFFF, 0x001FFFFF, 0x000FFFFF, 0x0007FFFF, 0x0003FFFF,
            0x0001FFFF, 0x0000FFFF, 0x00007FFF, 0x00003FFF, 0x00001FFF, 0x00000FFF, 0x000007FF, 0x000003FF, 0x000001FF,
            0x000000FF, 0x0000007F, 0x0000003F, 0x0000001F, 0x0000000F, 0x00000007, 0x00000003, 0x00000001, 0x00000000 };

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
    public static String getProperty(List<Property> props, String key, String defaultValue)
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

    /**
     * Gets a property with the default value being null.
     * 
     * @see #getProperty(List, String, String)
     */
    public static String getProperty(List<Property> props, String key)
    {
        return getProperty(props, key, null);
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
        return ((s != null) && !s.trim().isEmpty());
    }

    /**
     * This method will return a populated SyncKey object give the individual values.
     * 
     * @param aClass
     *            Class associated with the field
     * @param fieldName
     *            Name of the key field
     * @return SyncKey - populated object. Null will not be returned; however, the class may be null if the class parameter is
     *         null.
     */
    public static SyncKey newSyncKey(Class<?> aClass, String fieldName)
    {
        SyncKey meta = new SyncKey();

        meta.setType((aClass != null ? aClass.getName() : null));
        meta.setKey(fieldName);

        return meta;
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
     * If the name parameter is null or the value inside the object is null, the resulting property object name will be null.
     * 
     * @see #newProperty(String, String, String)
     */
    public static Property newProperty(PropertyName name, String description, String value)
    {
        return newProperty((name != null ? name.value() : null), description, value);
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

    /**
     * This method will create a new mapping for o/s and filesystem and device names.
     * 
     * @param operatingSystemName
     *            Name of operating system to associate with filesystem and device name
     * @param fileSystemName
     *            Name of filesystem to associate with o/s and device name
     * @param deviceString
     *            Name of device to associate with o/s and filesystem name
     * @param operatingSystemSubVersion
     *            OS version number
     * @return FileSystemDeviceMapping - populated object. Null will not be returned.
     */
    public static FileSystemDeviceMapping newFileSystemDeviceMapping(String operatingSystemName, String fileSystemName,
            String deviceString, String operatingSystemSubVersion)
    {
        FileSystemDeviceMapping fsdMapping = new FileSystemDeviceMapping();
        OSVersionSpecificDevice osvDevice = new OSVersionSpecificDevice();

        fsdMapping.setFileSystemName(fileSystemName);
        fsdMapping.setOperatingSystemName(operatingSystemName);

        osvDevice.setDeviceString(deviceString);
        osvDevice.setOperatingSystemSubVersion(operatingSystemSubVersion);

        fsdMapping.getDevices().add(osvDevice);

        return fsdMapping;
    }

    /**
     * This method will convert an IP address to a numeric value. From the old EC2Cloud class.
     * 
     * @param strIP
     *            IP address to be converted.
     * @return long - numeric representation of the IP address. Zero will be returned if the IP address is empty.
     */
    public static long ipToLong(String strIP)
    {
        long retval = 0;
        long[] ip = new long[4];
        String[] ipSec = strIP.split("\\.");

        if ((strIP != null) && !strIP.isEmpty())
        {
            for (int k = 0; k < 4; k++)
            {
                ip[k] = Long.valueOf(ipSec[k]);
            }

            retval = (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
        }

        return retval;
    }

    /**
     * This method will convert a numeric value to an IP address. From the old EC2Cloud class.
     * 
     * @param longIP
     *            Numeric value that is to be converted to IP address
     * @return String - IP address represented by numeric value
     */
    public static String longToIP(long longIP)
    {
        StringBuffer sb = new StringBuffer("");

        sb.append(String.valueOf(longIP >>> 24));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(longIP & 0x000000FF));

        return sb.toString();
    }

}
