package com.servicemesh.agility.sdk.cloud.spi;

public interface IReconfigure
{
    /*
     * This interface isn't really implemented but placed here to denote that the existing IInstance start
     * and reboot methods will implement the 'reconfigure' options as part of the request.  There's no need
     * to have extra methods for that defined here.
     * 
     * The async brige will implement the com.servicemesh.agility.cloud.adapter.IReconfigure interface and
     * inject the reconfigure flag into the start or reboot request.
     */
    //public ICancellable start(InstanceStartRequest request, ResponseHandler<InstanceResponse> handler);
    //public ICancellable reboot(InstanceRebootRequest request, ResponseHandler<InstanceResponse> handler);

}
