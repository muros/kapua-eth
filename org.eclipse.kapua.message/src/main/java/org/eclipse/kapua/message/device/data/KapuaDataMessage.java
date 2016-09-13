package org.eclipse.kapua.message.device.data;

import org.eclipse.kapua.message.KapuaMessage;

public interface KapuaDataMessage extends KapuaMessage<KapuaDataChannel, KapuaDataPayload>
{
    public String getClientId();

    public void setClientId(String clientId);
}
