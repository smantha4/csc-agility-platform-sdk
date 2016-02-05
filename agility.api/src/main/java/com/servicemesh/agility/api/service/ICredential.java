package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Credential;

public interface ICredential
{

    /**
     * Return a Credential with all keys decrypted
     * 
     * @param id
     *            the id of the Credential to fetch
     * @throws Exception
     */
    public Credential fetch(int id) throws Exception;

    /**
     * Return the decrypted String value of the specified key of the credential
     * 
     * @param id
     *            the id of the credential to fetch
     * @param fieldName
     *            the name of the field to fetch. Valid values are "privateKey" and "publicKey".
     * @throws Exception
     */
    public String decryptField(int id, String fieldName) throws Exception;

    /**
     * Update the specified key of the credential with the specified value
     * 
     * @param id
     *            the id of the Credential to update
     * @param fieldName
     *            the field to update. Valid values are "privateKey" and "publicKey".
     * @param fieldValue
     *            the value to store into the field
     * @throws Exception
     */
    public void updateField(int id, String fieldName, String fieldValue) throws Exception;
}
