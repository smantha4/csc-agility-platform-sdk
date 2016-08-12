package com.servicemesh.agility.sdk.cloud.helper;

import com.servicemesh.agility.api.Image;
import com.servicemesh.agility.api.Property;

public class ImageHelper
{

    public static void insertProperty(Image image, String name, String value)
    {
        for (Property prop : image.getProperties())
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
        image.getProperties().add(prop);
    }

    public static String getProperty(Image image, String name)
    {
        for (Property prop : image.getProperties())
        {
            if (prop.getName() != null && prop.getName().equals(name))
            {
                return prop.getValue();
            }
        }
        return null;
    }

}
