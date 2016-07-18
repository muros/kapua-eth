package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestPayload;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseChannel;

@SuppressWarnings("rawtypes")
public interface KapuaDeviceCallFactory<RQD extends DeviceRequestChannel, RQP extends DeviceRequestPayload, RSD extends DeviceResponseChannel, RSM extends DeviceMessage, H extends DeviceCallback>
                                       extends KapuaObjectFactory
{
    public DeviceCall newDeviceCall(RQD requestDestination, RQP requestPayload, Long timeout);

    public DeviceCall newDeviceCall(RQD requestDestination, RQP requestPayload, RSD responseDestination, Long timeout);

}
