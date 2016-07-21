package org.eclipse.kapua.service.device.call.message.app.response.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;

public class KuraResponsePayload extends KuraPayload implements DeviceResponsePayload
{
    @SuppressWarnings("unchecked")
    @Override
    public KuraResponseCode getResponseCode()
    {
        return KuraResponseCode.valueOf((String) metrics().get("response.code"));
    }

    @Override
    public String getExceptionMessage()
    {
        return (String) metrics().get("response.exception.message");
    }

    @Override
    public String getExceptionStack()
    {
        return (String) metrics().get("response.exception.stack");
    }

    @Override
    public byte[] getResponseBody()
    {
        return getBody();
    }

    @Override
    public byte[] toByteArray()
    {
        return super.toByteArray();
    }

    @Override
    public void readFromByteArray(byte[] rawPayload)
        throws KapuaException
    {
        super.readFromByteArray(rawPayload);
    }
}