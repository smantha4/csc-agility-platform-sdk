package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.agility.sdk.cloud.msgs.CustomerGatewayDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.DHCPOptionsAssociateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.DHCPOptionsCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.DHCPOptionsDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.InternetGatewayDetachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.RunningInstancesStopRequest;
import com.servicemesh.agility.sdk.cloud.msgs.SubnetAddRequest;
import com.servicemesh.agility.sdk.cloud.msgs.SubnetChangeRequest;
import com.servicemesh.agility.sdk.cloud.msgs.SubnetDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPCCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPCDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPCResponse;
import com.servicemesh.agility.sdk.cloud.msgs.VPCSubnetsDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNConnectionDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNConnectionStateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNConnectionStateResponse;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayAttachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayAttachmentsRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayAttachmentsResponse;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayConnectionCreateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayConnectionDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayDeleteRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayDetachRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayStateRequest;
import com.servicemesh.agility.sdk.cloud.msgs.VPNGatewayStateResponse;
import com.servicemesh.core.async.ResponseHandler;

public interface IVPC
{

    public ICancellable create(VPCCreateRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable delete(VPCDeleteRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable subnetAdd(SubnetAddRequest request, ResponseHandler<VPCResponse> handler);

    //  Note: I think the changeSubnet can be done outside the cloud adapter...
    public ICancellable subnetChange(SubnetChangeRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable subnetDelete(SubnetDeleteRequest request, ResponseHandler<VPCResponse> handler);

    //  Gateway operations are under review..

    public ICancellable vpnGatewayConnectionCreate(VPNGatewayConnectionCreateRequest request,
            ResponseHandler<VPCResponse> handler);

    public ICancellable vpnGatewayConnectionDelete(VPNGatewayConnectionDeleteRequest request,
            ResponseHandler<VPCResponse> handler);

    public ICancellable vpnGatewayAttach(VPNGatewayAttachRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable vpnGatewayDetach(VPNGatewayDetachRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable vpnConnectionState(VPNConnectionStateRequest request,
            ResponseHandler<VPNConnectionStateResponse> handler);

    public ICancellable vpnGatewayState(VPNGatewayStateRequest request, ResponseHandler<VPNGatewayStateResponse> handler);

    public ICancellable vpnConnectionDelete(VPNConnectionDeleteRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable vpnGatewayDelete(VPNGatewayDeleteRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable customerGatewayDelete(CustomerGatewayDeleteRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable runningInstancesStop(RunningInstancesStopRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable internetGatewayDetach(InternetGatewayDetachRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable vpcSubnetsDelete(VPCSubnetsDeleteRequest request, ResponseHandler<VPCResponse> handler);

    //  DHCPOptions methods:
    public ICancellable dhcpOptionsCreate(DHCPOptionsCreateRequest request, ResponseHandler<VPCResponse> handler);

    public ICancellable dhcpOptionsDelete(DHCPOptionsDeleteRequest request, ResponseHandler<VPCResponse> hanlder);

    public ICancellable dhcpOptionsAssociate(DHCPOptionsAssociateRequest request, ResponseHandler<VPCResponse> handler);

    //  Get the attachments for a VPN Gateway:
    public ICancellable vpnGatewayAttachments(VPNGatewayAttachmentsRequest request,
            ResponseHandler<VPNGatewayAttachmentsResponse> handler);

}
