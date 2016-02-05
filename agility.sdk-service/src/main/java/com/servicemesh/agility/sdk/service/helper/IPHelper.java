/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.sdk.service.helper;

import java.io.Serializable;

/**
 * This abstract class provides utility classes that may prove useful to any adapter implementation.
 *
 * @author henry
 */
public abstract class IPHelper implements Serializable
{
    private static final long serialVersionUID = 20140117;

    public static final int[] MASKS = new int[] { 0xFFFFFFFF, 0x7FFFFFFF, 0x3FFFFFFF, 0x1FFFFFFF, 0x0FFFFFFF, 0x07FFFFFF,
            0x03FFFFFF, 0x01FFFFFF, 0x00FFFFFF, 0x007FFFFF, 0x003FFFFF, 0x001FFFFF, 0x000FFFFF, 0x0007FFFF, 0x0003FFFF,
            0x0001FFFF, 0x0000FFFF, 0x00007FFF, 0x00003FFF, 0x00001FFF, 0x00000FFF, 0x000007FF, 0x000003FF, 0x000001FF,
            0x000000FF, 0x0000007F, 0x0000003F, 0x0000001F, 0x0000000F, 0x00000007, 0x00000003, 0x00000001, 0x00000000 };

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
