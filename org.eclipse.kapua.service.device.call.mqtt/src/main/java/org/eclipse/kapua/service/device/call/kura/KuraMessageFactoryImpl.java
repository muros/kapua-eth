package org.eclipse.kapua.service.device.call.kura;

import org.eclipse.kapua.service.device.call.DeviceMessageFactory;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;
import org.eclipse.kapua.service.device.call.message.DevicePayload;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestPayload;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;

@SuppressWarnings("rawtypes")
public class KuraMessageFactoryImpl implements DeviceMessageFactory
{
    @Override
    public DeviceRequestChannel newChannel()
    {
        return new KuraRequestChannel();
    }

    @Override
    public DeviceRequestChannel newRequestChannel()
    {
        return new KuraRequestChannel();
    }

    @Override
    public DevicePayload newPayload()
    {
        return new KuraPayload();
    }

    @Override
    public DeviceRequestPayload newRequestPayload()
    {
        return new KuraRequestPayload();
    }

    @Override
    public DeviceMessage newMessage()
    {
        return new KuraMessage();
    }

    @Override
    public DeviceRequestMessage newRequestMessage()
    {
        return new KuraRequestMessage();
    }

}
