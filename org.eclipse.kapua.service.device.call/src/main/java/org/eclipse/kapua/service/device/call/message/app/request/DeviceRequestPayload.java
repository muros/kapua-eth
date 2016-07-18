package org.eclipse.kapua.service.device.call.message.app.request;

import org.eclipse.kapua.service.device.call.message.DevicePayload;

public interface DeviceRequestPayload extends DevicePayload
{
    public void setRequestId(String requestId);

    public void setRequesterClientId(String requesterId);
}
