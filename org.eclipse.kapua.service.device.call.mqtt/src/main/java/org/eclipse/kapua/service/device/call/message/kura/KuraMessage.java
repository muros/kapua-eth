package org.eclipse.kapua.service.device.call.message.kura;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.DeviceMessage;

public class KuraMessage<C extends KuraChannel, P extends KuraPayload> implements DeviceMessage<C, P>
{
    protected C    channel;
    protected Date timestamp;
    protected P    payload;

    public KuraMessage()
    {
        super();
    }

    public KuraMessage(C channel, Date timestamp, P payload)
    {
        this();
        this.channel = channel;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    @Override
    public C getChannel()
    {
        return channel;
    }

    @Override
    public P getPayload()
    {
        return payload;
    }

    @Override
    public Date timestamp()
    {
        return timestamp;
    }
}
