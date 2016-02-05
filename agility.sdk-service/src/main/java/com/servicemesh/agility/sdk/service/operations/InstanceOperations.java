package com.servicemesh.agility.sdk.service.operations;

import com.servicemesh.agility.sdk.service.msgs.InstancePostBootRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostStartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePostStopRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreBootRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreProvisionRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreReconfigureRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreReleaseRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreRestartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreStartRequest;
import com.servicemesh.agility.sdk.service.msgs.InstancePreStopRequest;
import com.servicemesh.agility.sdk.service.msgs.InstanceResponse;
import com.servicemesh.agility.sdk.service.spi.IInstanceLifecycle;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Status;

public class InstanceOperations implements IInstanceLifecycle
{

    @Override
    public Promise<InstanceResponse> preProvision(InstancePreProvisionRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> postProvision(InstancePostProvisionRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> preBoot(InstancePreBootRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> postBoot(InstancePostBootRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> preStop(InstancePreStopRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> postStop(InstancePostStopRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> preStart(InstancePreStartRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> postStart(InstancePostStartRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> preRestart(InstancePreRestartRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> postRestart(InstancePostRestartRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> preRelease(InstancePreReleaseRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> postRelease(InstancePostReleaseRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> preReconfigure(InstancePreReconfigureRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

    @Override
    public Promise<InstanceResponse> postReconfigure(InstancePostReconfigureRequest request)
    {
        InstanceResponse response = new InstanceResponse();
        response.setStatus(Status.COMPLETE);
        return Promise.pure(response);
    }

}
