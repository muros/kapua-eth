package org.eclipse.kapua.service.device.management.response;

import org.eclipse.kapua.message.KapuaMessage;

public interface KapuaResponseMessage<C extends KapuaResponseChannel, P extends KapuaResponsePayload> extends KapuaMessage<C, P>
{
    public KapuaResponseCode getResponseCode();

    public void setResponseCode(KapuaResponseCode responseCode);
}
