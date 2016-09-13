package org.eclipse.kapua.message.internal.device.data;

import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;
import org.eclipse.kapua.message.internal.KapuaMessageImpl;

public class KapuaDataMessageImpl extends KapuaMessageImpl<KapuaDataChannel, KapuaDataPayload> implements KapuaDataMessage
{

    private String clientId;

    @Override
    public String getClientId()
    {
        return clientId;
    }

    @Override
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

}