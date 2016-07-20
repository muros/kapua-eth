package org.eclipse.kapua.service.device.call.kura;

import org.eclipse.kapua.service.device.call.KapuaDeviceCallFactory;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;

public class KuraDeviceCallFactoryImpl implements KapuaDeviceCallFactory<KuraRequestMessage>
{

    @Override
    public KuraDeviceCallImpl newDeviceCall(KuraRequestMessage requestDestination, Long timeout)
    {
        return new KuraDeviceCallImpl(requestDestination, timeout);
    }

}
