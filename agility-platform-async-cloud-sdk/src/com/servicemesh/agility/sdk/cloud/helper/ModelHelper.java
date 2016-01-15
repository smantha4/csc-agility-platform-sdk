package com.servicemesh.agility.sdk.cloud.helper;

import com.servicemesh.agility.api.Model;
import com.servicemesh.agility.api.Property;

public class ModelHelper
{

    public static void insertProperty(Model model, String name, String value)
    {
        for (Property prop : model.getProperties())
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
        model.getProperties().add(prop);
    }

    public static String getProperty(Model model, String name)
    {
        for (Property prop : model.getProperties())
        {
            if (prop.getName() != null && prop.getName().equals(name))
            {
                return prop.getValue();
            }
        }
        return null;
    }

}
