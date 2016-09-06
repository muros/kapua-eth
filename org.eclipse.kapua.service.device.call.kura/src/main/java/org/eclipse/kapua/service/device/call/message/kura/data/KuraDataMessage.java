package org.eclipse.kapua.service.device.call.message.kura.data;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.DeviceMessage;

public class KuraDataMessage implements DeviceMessage<KuraDataChannel, KuraDataPayload>
{
    protected KuraDataChannel  channel;
    protected Date             timestamp;
    protected KuraDataPayload  payload;

    public KuraDataMessage()
    {
        super();
    }

    public KuraDataMessage(KuraDataChannel channel, Date timestamp, KuraDataPayload payload)
    {
        this();
        this.channel = channel;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    @Override
    public KuraDataChannel getChannel()
    {
        return channel;
    }

    @Override
    public KuraDataPayload getPayload()
    {
        return payload;
    }

    @Override
    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }
}
