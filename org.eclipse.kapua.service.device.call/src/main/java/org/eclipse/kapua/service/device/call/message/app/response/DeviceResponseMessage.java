package org.eclipse.kapua.service.device.call.message.app.response;

import org.eclipse.kapua.service.device.call.message.DeviceMessage;

public interface DeviceResponseMessage<D extends DeviceResponseChannel, P extends DeviceResponsePayload> extends DeviceMessage<D, P>
{
}
