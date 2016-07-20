package org.eclipse.kapua.service.device.call.message.app.request.kura;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;

public class KuraRequestMessage extends KuraMessage<KuraRequestChannel, KuraRequestPayload> implements DeviceRequestMessage<KuraRequestChannel, KuraRequestPayload>
{
    public KuraRequestMessage()
    {
        super();
    }

    public KuraRequestMessage(KuraRequestChannel channel, Date timestamp, KuraRequestPayload payload)
    {
        super(channel, timestamp, payload);
    }

}
