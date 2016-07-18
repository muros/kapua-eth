package org.eclipse.kapua.message.device.app.request;

import org.eclipse.kapua.message.KapuaPayload;

public interface KapuaRequestPayload extends KapuaPayload
{
    public void setRequestId(String requestId);

    public void setRequesterClientId(String requesterClientId);
}
