package org.eclipse.kapua.service.device.call.message;

import java.util.Date;

import org.eclipse.kapua.message.Message;

public interface DeviceMessage<C extends DeviceChannel, P extends DevicePayload> extends Message
{
    public C getChannel();

    public P getPayload();

    public Date getTimestamp();
}
