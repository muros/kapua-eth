package org.eclipse.kapua.service.device.call.message.app.request;

import org.eclipse.kapua.service.device.call.message.DevicePayload;

public interface DeviceRequestPayload extends DevicePayload
{
    public String getRequestId();

    public void setRequestId(String requestId);

    public String getRequesterClientId();

    public void setRequesterClientId(String requesterId);
}
