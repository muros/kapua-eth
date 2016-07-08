package org.eclipse.kapua.service.device.call.kura;

import org.eclipse.kapua.service.device.call.KapuaDeviceCall;
import org.eclipse.kapua.service.device.call.KapuaDeviceCallFactory;
import org.eclipse.kapua.service.device.message.request.KapuaRequestPayload;

public class KuraDeviceCallFactoryImpl implements KapuaDeviceCallFactory
{

    @Override
    public KapuaDeviceCall newInstance(String requestTopic, KapuaRequestPayload kapuaPayload, Long timeout)
    {
        return new KuraDeviceCallImpl(requestTopic, kapuaPayload, timeout);
    }

    @Override
    public KapuaDeviceCall newInstance(String requestTopic, String responseTopic, KapuaRequestPayload kapuaPayload, Long timeout)
    {
        return new KuraDeviceCallImpl(requestTopic, responseTopic, kapuaPayload, timeout);
    }

}
