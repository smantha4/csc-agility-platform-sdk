package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.CredentialCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialResponse;
import com.servicemesh.core.async.ResponseHandler;

/**
 * Provides operations for managing credentials (ssh keys) within the cloud provider.
 */
public interface ICredential
{
    /**
     * Called to create a new ssh key that is used by the cloud provider to authenticate logins to virtual machines created with
     * the specified key. The call typically returns a public/private keypair. The public key is added to the ssh configuration of
     * the virtual machine to enable authentication via the associated private key.
     * 
     * @param request
     *            Specifies the name assigned to the key.
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable create(CredentialCreateRequest request, ResponseHandler<CredentialResponse> handler);

    /**
     * Called to delete a named ssh key mantained/managed by the cloud provider.
     * 
     * @param request
     *            Specifies the name assigned to the key.
     * @param handler
     *            Interface to asynchronously signal completion (or error) of the requested operation.
     * @return An instance of ICancellable which can be used by the platform to cancel the pending operation.
     */
    public ICancellable delete(CredentialDeleteRequest request, ResponseHandler<CredentialResponse> handler);
}
