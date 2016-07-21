package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.device.call.message.DeviceChannel;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;
import org.eclipse.kapua.service.device.call.message.DevicePayload;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestPayload;

@SuppressWarnings("rawtypes")
public interface DeviceMessageFactory extends KapuaObjectFactory
{
    public DeviceMessage newMessage();

    public DeviceRequestMessage newRequestMessage();

    public DeviceChannel newChannel();

    public DeviceRequestChannel newRequestChannel();

    public DevicePayload newPayload();

    public DeviceRequestPayload newRequestPayload();

}
