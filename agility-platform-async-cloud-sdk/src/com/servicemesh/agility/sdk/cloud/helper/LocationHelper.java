package com.servicemesh.agility.sdk.cloud.helper;

import com.servicemesh.agility.api.Location;
import com.servicemesh.agility.api.Property;

public class LocationHelper
{

    public static void insertProperty(Location location, String name, String value)
    {
        for (Property prop : location.getProperties())
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
        location.getProperties().add(prop);
    }

    public static String getProperty(Location location, String name)
    {
        for (Property prop : location.getProperties())
        {
            if (prop.getName() != null && prop.getName().equals(name))
            {
                return prop.getValue();
            }
        }
        return null;
    }
}
