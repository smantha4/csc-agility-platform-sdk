package com.servicemesh.agility.sdk.cloud.helper;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Link;

public class LinkHelper
{

    public static <T extends Asset> Link getLink(T asset)
    {
        if (asset != null)
        {
            Link link = new Link();
            link.setId(asset.getId() != null ? asset.getId() : 0);
            link.setName(asset.getName());
            link.setPosition(0);
            String href = asset.getClass().getSimpleName().toLowerCase() + "/" + ((asset.getId() != null) ? asset.getId() : 0);
            link.setHref(href);
            link.setType("application/" + asset.getClass().getName() + "+xml");
            link.setRel("up");
            return link;
        }
        return null;
    }

}
