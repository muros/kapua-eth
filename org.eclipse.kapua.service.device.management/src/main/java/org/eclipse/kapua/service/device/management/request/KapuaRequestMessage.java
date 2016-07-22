package org.eclipse.kapua.service.device.management.request;

import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.service.device.management.response.KapuaResponseChannel;
import org.eclipse.kapua.service.device.management.response.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.response.KapuaResponsePayload;

public interface KapuaRequestMessage<C extends KapuaRequestChannel, P extends KapuaRequestPayload> extends KapuaMessage<C, P>
{
    public <M extends KapuaRequestMessage<C, P>> Class<M> getRequestClass();

    public <RSC extends KapuaResponseChannel, RSP extends KapuaResponsePayload, M extends KapuaResponseMessage<RSC, RSP>> Class<M> getResponseClass();
}
