package com.servicemesh.io.http;

import java.util.HashMap;
import java.util.Map;

public class Credentials
{
    public enum CredentialsType
    {
        CREDENTIALS_TYPE_USERNAMEPASSORD(0, "UsernamePassword"), CREDENTIALS_TYPE_NTCREDS(1, "NTCredentials");

        private final int _id;
        private final String _name;

        private CredentialsType(int id, final String name)
        {
            _id = id;
            _name = name;
        }

        public String getName()
        {
            return _name;
        }
    }

    public final static String USERNAME_KEY = "username";
    public final static String PASSWORD_KEY = "password";
    public final static String DOMAIN_KEY = "domain";

    private final Map<String, Object> _properties = new HashMap<String, Object>();
    private final CredentialsType _type;

    public Credentials(CredentialsType type)
    {
        _type = type;
    }

    public CredentialsType getType()
    {
        return _type;
    }

    public void setUsername(String username)
    {
        _properties.put(USERNAME_KEY, username);
    }

    public String getUsername()
    {
        return (String) _properties.get(USERNAME_KEY);
    }

    public void setPassword(String password)
    {
        _properties.put(PASSWORD_KEY, password);
    }

    public String getPassword()
    {
        return (String) _properties.get(PASSWORD_KEY);
    }

    public void setDomain(String domain)
    {
        _properties.put(DOMAIN_KEY, domain);
    }

    public String getDomain()
    {
        return (String) _properties.get(DOMAIN_KEY);
    }
}
