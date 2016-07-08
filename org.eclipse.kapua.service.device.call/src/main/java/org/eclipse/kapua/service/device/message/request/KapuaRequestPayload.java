package org.eclipse.kapua.service.device.message.request;

import org.eclipse.kapua.message.KapuaPayload;

public class KapuaRequestPayload extends KapuaPayload
{

    public void setRequestId(String requestId)
    {
        addMetric("request.id", requestId);
    }

    public void setRequesterId(String requesterId)
    {
        addMetric("requester.id", requesterId);
    }
}
