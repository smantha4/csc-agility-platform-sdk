package com.servicemesh.agility.sdk.cloud.helper;

import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.Property;

public class InstanceHelper
{

    public static void insertProperty(Instance instance, String name, String value)
    {
        for (Property prop : instance.getProperties())
        {
            if (prop.getName() != null && prop.getName().equals(name))
            {
                prop.setValue(value);
                return;
            }
        }
        Property prop = new Property();
        prop.setName(name);
        prop.setValue(value);
        instance.getProperties().add(prop);
    }

    public static String getProperty(Instance instance, String name)
    {
        for (Property prop : instance.getProperties())
        {
            if (prop.getName() != null && prop.getName().equals(name))
            {
                return prop.getValue();
            }
        }
        return null;
    }

}
