package org.eclipse.kapua.message.device.lifecycle;

import java.util.Date;

import org.eclipse.kapua.message.KapuaMessage;

public interface KapuaBirthMessage extends KapuaMessage<KapuaBirthChannel, KapuaBirthPayload>
{
    public Date getReceivedOn();

    public Date getSentOn();
}
