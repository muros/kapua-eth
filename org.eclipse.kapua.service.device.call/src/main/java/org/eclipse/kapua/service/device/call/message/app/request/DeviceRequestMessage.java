package org.eclipse.kapua.service.device.call.message.app.request;

import org.eclipse.kapua.service.device.call.message.DeviceMessage;

public interface DeviceRequestMessage<D extends DeviceRequestChannel, P extends DeviceRequestPayload> extends DeviceMessage<D, P>
{

}
