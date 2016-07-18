package org.eclipse.kapua.service.device.call.message.app.request.kura;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;

public class KuraRequestMessage extends KuraMessage
{
    public KuraRequestMessage(KuraChannel channel, Date timestamp, KuraPayload payload)
    {
        super(channel, timestamp, payload);
    }
}
