package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestChannel;

public interface DeviceMessageFactory extends KapuaObjectFactory
{

    public DeviceRequestChannel newChannel();

    public DeviceRequestChannel newRequestChannel();

}
