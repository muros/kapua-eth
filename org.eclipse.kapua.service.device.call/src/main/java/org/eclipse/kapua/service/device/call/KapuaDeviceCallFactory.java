package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.device.call.message.KapuaRequestDestination;
import org.eclipse.kapua.service.device.call.message.KapuaRequestPayload;
import org.eclipse.kapua.service.device.call.message.KapuaResponseDestination;
import org.eclipse.kapua.service.device.call.message.KapuaResponseMessage;

public interface KapuaDeviceCallFactory<RQD extends KapuaRequestDestination, RQP extends KapuaRequestPayload, RSD extends KapuaResponseDestination, RSM extends KapuaResponseMessage, H extends KapuaDeviceCallHandler<RSM>>
                                       extends KapuaObjectFactory
{
    @SuppressWarnings("rawtypes")
    public KapuaDeviceCall newInstance(RQD requestDestination, RQP requestPayload, Long timeout);

    @SuppressWarnings("rawtypes")
    public KapuaDeviceCall newInstance(RQD requestDestination, RSD responseDestination, RQP requestPayload, Long timeout);

}
