package org.eclipse.kapua.service.device.call.message.app.response;

import org.eclipse.kapua.service.device.call.message.DevicePayload;

public interface DeviceResponsePayload extends DevicePayload
{
    public <C extends DeviceResponseCode> C getResponseCode();

    public String getExceptionMessage();

    public String getExceptionStack();

    public byte[] getResponseBody();
}
