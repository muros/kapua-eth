package org.eclipse.kapua.service.device.management.response;

import org.eclipse.kapua.message.KapuaChannel;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;

public interface KapuaResponseMessage<C extends KapuaChannel, P extends KapuaPayload> extends KapuaMessage<C, P>
{
    public KapuaResponseCode getResponseCode();

    public void setResponseCode(KapuaResponseCode responseCode);
}
