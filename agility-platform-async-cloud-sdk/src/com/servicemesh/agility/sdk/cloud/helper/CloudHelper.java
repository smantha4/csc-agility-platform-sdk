package com.servicemesh.agility.sdk.cloud.helper;

import com.servicemesh.agility.api.Cloud;
import com.servicemesh.agility.api.Property;

public class CloudHelper
{

    public static void insertProperty(Cloud cloud, String name, String value)
    {
        for (Property prop : cloud.getProperties())
        {
            if (prop.getName() != null && prop.getName().equals(name))
            {
                prop.setValue(value);
                return;
            }
        }
        Property nprop = new Property();
        nprop.setName(name);
        nprop.setValue(value);
        cloud.getProperties().add(nprop);
    }

    public static String getProperty(Cloud cloud, String name)
    {
        for (Property prop : cloud.getProperties())
        {
            if (prop.getName() != null && prop.getName().equals(name))
            {
                return prop.getValue();
            }
        }
        return null;
    }

}
