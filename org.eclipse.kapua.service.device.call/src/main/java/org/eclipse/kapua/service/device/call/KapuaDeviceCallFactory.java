package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;

@SuppressWarnings("rawtypes")
public interface KapuaDeviceCallFactory<M extends DeviceMessage> extends KapuaObjectFactory
{
    public DeviceCall newDeviceCall(M requestDestination, Long timeout);
}
