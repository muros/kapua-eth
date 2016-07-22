package org.org.eclipse.kapua.service.device.management.command.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;

public class CommandRequestMessage extends KapuaMessageImpl<CommandRequestChannel, CommandRequestPayload> implements KapuaRequestMessage<CommandRequestChannel, CommandRequestPayload>
{
    @SuppressWarnings("unchecked")
    @Override
    public Class<CommandRequestMessage> getRequestClass()
    {
        return CommandRequestMessage.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<CommandResponseMessage> getResponseClass()
    {
        return CommandResponseMessage.class;
    }

}
