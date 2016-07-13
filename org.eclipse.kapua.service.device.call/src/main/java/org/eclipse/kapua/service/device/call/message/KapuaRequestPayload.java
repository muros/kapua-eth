package org.eclipse.kapua.service.device.call.message;

public interface KapuaRequestPayload extends org.eclipse.kapua.message.KapuaPayload
{

    public void setRequestId(String requestId);
    // {
    // addMetric("request.id", requestId);
    // }

    public void setRequesterClientId(String requesterId);
    // {
    // addMetric("requester.id", requesterId);
    // }
}
