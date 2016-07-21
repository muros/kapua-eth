package org.eclipse.kapua.service.device.call.kura;

import org.eclipse.kapua.service.device.call.DeviceCallFactory;

public class KuraDeviceCallFactoryImpl implements DeviceCallFactory
{

    @Override
    public KuraDeviceCallImpl newDeviceCall()
    {
        return new KuraDeviceCallImpl();
    }

}
