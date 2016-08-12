package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.CredentialSelectRequest;
import com.servicemesh.agility.sdk.cloud.msgs.CredentialSelectResponse;
import com.servicemesh.core.async.ResponseHandler;

public interface ICredentialSelector
{

    public ICancellable select(CredentialSelectRequest request, ResponseHandler<CredentialSelectResponse> handler);

}
