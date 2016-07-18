package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponsePayload;

@SuppressWarnings("rawtypes")
public interface DeviceCall<DRC extends DeviceResponseChannel, DRP extends DeviceResponsePayload, DRM extends DeviceResponseMessage, DC extends DeviceCallback>
{
    public DRM send()
        throws KapuaException;

    public DRC sendAsync()
        throws KapuaException;

    public DRC sendAsync(DC kapuaDeviceCallHandler)
        throws KapuaException;
}
