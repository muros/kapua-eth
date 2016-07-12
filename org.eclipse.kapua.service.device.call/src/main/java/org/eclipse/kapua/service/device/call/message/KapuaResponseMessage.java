package org.eclipse.kapua.service.device.call.message;

import org.eclipse.kapua.client.message.KapuaMessage;

public interface KapuaResponseMessage<D extends KapuaResponseDestination, P extends KapuaResponsePayload> extends KapuaMessage<D, P>
{
    public P getResponsePayload();
}
