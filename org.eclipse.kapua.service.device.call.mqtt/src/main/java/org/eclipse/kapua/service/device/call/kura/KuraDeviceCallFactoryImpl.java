package org.eclipse.kapua.service.device.call.kura;

import org.eclipse.kapua.service.device.call.KapuaDeviceCall;
import org.eclipse.kapua.service.device.call.KapuaDeviceCallFactory;
import org.eclipse.kapua.service.device.call.message.kura.KuraRequestDestination;
import org.eclipse.kapua.service.device.call.message.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.KuraResponseDestination;
import org.eclipse.kapua.service.device.call.message.kura.KuraResponseMessage;

public class KuraDeviceCallFactoryImpl implements
                                       KapuaDeviceCallFactory<KuraRequestDestination, KuraRequestPayload, KuraResponseDestination, KuraResponseMessage, KuraDeviceCallHandler>
{

    @Override
    public KapuaDeviceCall newInstance(KuraRequestDestination requestDestination, KuraRequestPayload requestPayload, Long timeout)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public KapuaDeviceCall newInstance(KuraRequestDestination requestDestination, KuraResponseDestination responseDestination, KuraRequestPayload requestPayload, Long timeout)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
