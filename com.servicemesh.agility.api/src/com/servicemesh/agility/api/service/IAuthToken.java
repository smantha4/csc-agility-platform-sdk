package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.AuthToken;

public interface IAuthToken
{

    /**
     * Create authentication token for a given username
     * 
     * @param username
     * @return AuthToken which was generated for a given username
     * @throws Exception
     *             if user doesn't have user proxy permission unless token it generated for itself
     */
    public AuthToken createToken(String username) throws Exception;

    /**
     * Delete authentication token
     * 
     * @param token
     *            to delete
     * @return true if token was deleted, otherwise false
     * @throws Exception
     *             if user doesn't have user proxy permission unless token it generated for itself
     */
    public boolean deleteToken(String token) throws Exception;

    /**
     * Validate authentication token
     * 
     * @param token
     *            to authenticate
     * @return true if token is valid, otherwise returns false
     * @throws Exception
     */
    public boolean validateToken(String token) throws Exception;
}
