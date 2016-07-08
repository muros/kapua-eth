package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.service.device.message.request.KapuaRequestDestination;
import org.eclipse.kapua.service.device.message.request.KapuaRequestMessage;
import org.eclipse.kapua.service.device.message.request.KapuaRequestPayload;
import org.eclipse.kapua.service.device.message.response.KapuaResponseDestination;
import org.eclipse.kapua.service.device.message.response.KapuaResponseMessage;

public interface KapuaDeviceCallFactory<RQM extends KapuaRequestMessage, RSM extends KapuaResponseMessage, RQD extends KapuaRequestDestination, RSD extends KapuaResponseDestination, H extends KapuaDeviceCallHandler<RSM>>
                                       extends KapuaEntityFactory
{
    public KapuaDeviceCall<RQM, RSM, RQD, RSD, H> newInstance(String requestTopic, KapuaRequestPayload requestPayload, Long timeout);

    public KapuaDeviceCall<RQM, RSM, RQD, RSD, H> newInstance(String requestTopic, String responseTopic, KapuaRequestPayload requestPayload, Long timeout);
}
