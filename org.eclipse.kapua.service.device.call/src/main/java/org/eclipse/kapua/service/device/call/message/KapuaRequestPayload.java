package org.eclipse.kapua.service.device.call.message;

public interface KapuaRequestPayload extends org.eclipse.kapua.client.message.KapuaPayload
{

    public void setRequestId(String requestId);
    // {
    // addMetric("request.id", requestId);
    // }

    public void setRequesterId(String requesterId);
    // {
    // addMetric("requester.id", requesterId);
    // }
}
