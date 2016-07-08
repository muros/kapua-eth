package org.eclipse.kapua.service.device.management.commons.message;

import org.eclipse.kapua.message.KapuaMessage;

public class KapuaResponseMessage extends KapuaMessage
{
    public KapuaResponsePayload getResponsePayload()
    {
        return new KapuaResponsePayload(getKapuaPayload());
    }
}
