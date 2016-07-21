package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.model.KapuaObjectFactory;

@SuppressWarnings("rawtypes")
public interface DeviceCallFactory extends KapuaObjectFactory
{
    public DeviceCall newDeviceCall();
}
