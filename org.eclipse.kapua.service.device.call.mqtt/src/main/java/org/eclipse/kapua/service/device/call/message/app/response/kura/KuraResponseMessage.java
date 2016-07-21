package org.eclipse.kapua.service.device.call.message.app.response.kura;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;

public class KuraResponseMessage extends KuraMessage<KuraResponseChannel, KuraPayload> implements DeviceResponseMessage<KuraResponseChannel, KuraPayload>
{
    public KuraResponseMessage(KuraResponseChannel channel, Date timestamp, KuraPayload payload)
    {
        super(channel, timestamp, payload);
    }
}
