package org.eclipse.kapua.service.device.call.kura;

import org.eclipse.kapua.service.device.call.KapuaDeviceCallFactory;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;

public class KuraDeviceCallFactoryImpl implements
                                       KapuaDeviceCallFactory<KuraRequestChannel, KuraRequestPayload, KuraResponseChannel, KuraResponseMessage, KuraDeviceResponseContainer>
{

    @Override
    public KuraDeviceCallImpl newDeviceCall(KuraRequestChannel requestDestination, KuraRequestPayload requestPayload, Long timeout)
    {
        return new KuraDeviceCallImpl(requestDestination, requestPayload, timeout);
    }

    @Override
    public KuraDeviceCallImpl newDeviceCall(KuraRequestChannel requestDestination, KuraRequestPayload requestPayload, KuraResponseChannel responseDestination, Long timeout)
    {
        return new KuraDeviceCallImpl(requestDestination, requestPayload, responseDestination, timeout);
    }

}
