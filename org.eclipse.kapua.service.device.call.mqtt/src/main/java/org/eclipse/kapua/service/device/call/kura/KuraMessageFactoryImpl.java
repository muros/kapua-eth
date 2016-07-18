package org.eclipse.kapua.service.device.call.kura;

import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;

public class KuraMessageFactoryImpl implements org.eclipse.kapua.service.device.call.DeviceMessageFactory
{
    @Override
    public DeviceRequestChannel newChannel()
    {
        return new KuraRequestChannel();
    }

    @Override
    public KuraRequestChannel newRequestChannel()
    {
        return new KuraRequestChannel();
    }

}
