package org.org.eclipse.kapua.service.device.management.command.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.management.response.KapuaResponseMessage;

public class CommandResponseMessage extends KapuaMessageImpl<CommandResponseChannel, CommandResponsePayload> implements KapuaResponseMessage<CommandResponseChannel, CommandResponsePayload>
{
    private KapuaResponseCode responseCode;

    @Override
    public KapuaResponseCode getResponseCode()
    {
        return responseCode;
    }

    @Override
    public void setResponseCode(KapuaResponseCode responseCode)
    {
        this.responseCode = responseCode;
    }
}
