package com.servicemesh.io.util;

import java.util.Calendar;

// crypt function used to encode passwords passwd to applets/nx client
public class Crypt
{
    static final String dummyString = "{{{{";
    static final String validChars = "!#$%&()*+-.0123456789:;<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]_abcdefghijklmnopqrstuvwxyz{|}";

    static char getRandomValidCharFromList()
    {
        return validChars.charAt((int) Calendar.getInstance().get(Calendar.SECOND));
    }

    public static String encodeString(String s)
    {
        int i;
        StringBuffer ret = new StringBuffer();

        if (s.length() > 0)
        {
            ret.append(":");
            for (i = 0; i < s.length(); i++)
            {
                ret.append(String.format("%1$d:", s.charAt(i) + i + 1));
            }
        }
        return ret.toString();
    }

    public static String decodeString(String s)
    {
        String val = new String(s);
        StringBuffer ret = new StringBuffer();

        if (val.charAt(0) == ':' && val.length() > 1 && val.charAt(val.length() - 1) == ':')
        {
            int idx = 1;
            val = val.substring(1);
            while (val.length() > 0)
            {
                int p = val.indexOf(":");
                if (p != -1)
                {
                    int l;
                    l = Integer.parseInt(val.substring(0, p));
                    ret.append((char) (l - idx++));
                }
                else
                {
                    break;
                }
                val = val.substring(p + 1);
            }
        }
        return ret.toString();
    }

    public static String encryptString(String s)
    {
        int i;
        StringBuffer sRet = new StringBuffer();

        if (s == null || s.length() == 0)
        {
            return s;
        }

        String str = encodeString(s);

        if (str.length() < 32)
        {
            str += dummyString;
        }

        // Reverse string
        for (i = (str.length() - 1); (int) i >= 0; i--)
        {
            sRet.append(str.charAt(i));
        }

        if (sRet.length() < 32)
        {
            sRet.append(dummyString);
        }

        char k = getRandomValidCharFromList();
        int l = (k + sRet.length()) - 2;
        sRet.insert(0, k);

        for (i = 1; i < sRet.length(); i++)
        {
            int j = validChars.indexOf(sRet.charAt(i));
            if (j == -1)
            {
                return s;
            }
            sRet.setCharAt(i, validChars.charAt((j + l * (i + 1)) % validChars.length()));
        }

        char c = (char) (getRandomValidCharFromList() + 2);
        sRet.append(c);

        return sRet.toString();
    }

    public static String decryptString(String s)
    {
        int i;
        StringBuffer sRet;

        if (s == null || s.length() < 5)
        {
            return s;
        }

        sRet = new StringBuffer(s.substring(0, s.length() - 1));

        int n = (sRet.charAt(0) + sRet.length()) - 3;

        for (i = 1; i < sRet.length(); i++)
        {
            int j = validChars.indexOf(sRet.charAt(i));
            if (j == -1)
            {
                return s;
            }

            int k = j - (n * (i + 1)) % validChars.length();

            if (k < 0)
            {
                k = validChars.length() + k;
            }
            sRet.setCharAt(i, validChars.charAt(k));
        }

        sRet = new StringBuffer(sRet.substring(1));

        if (sRet.indexOf(dummyString) == 0)
        {
            sRet = new StringBuffer(sRet.substring(dummyString.length()));
        }

        StringBuffer str = sRet;
        sRet = new StringBuffer();
        // Reverse string
        for (i = (str.length() - 1); (int) i >= 0; i--)
        {
            sRet.append(str.charAt(i));
        }

        if (sRet.indexOf(dummyString) == 0)
        {
            sRet = new StringBuffer(sRet.substring(dummyString.length()));
        }

        return decodeString(sRet.toString());
    }
}
