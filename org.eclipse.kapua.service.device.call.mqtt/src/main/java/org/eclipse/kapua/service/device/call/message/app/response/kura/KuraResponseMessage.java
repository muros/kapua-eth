package org.eclipse.kapua.service.device.call.message.app.response.kura;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;

public class KuraResponseMessage extends KuraMessage<KuraResponseChannel, KuraResponsePayload> implements DeviceResponseMessage<KuraResponseChannel, KuraResponsePayload>
{
    public KuraResponseMessage(KuraResponseChannel channel, Date timestamp, KuraResponsePayload payload)
    {
        super(channel, timestamp, payload);
    }
}
