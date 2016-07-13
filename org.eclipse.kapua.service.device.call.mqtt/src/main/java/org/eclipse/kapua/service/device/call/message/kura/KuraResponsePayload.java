package org.eclipse.kapua.service.device.call.message.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.KapuaResponseCode;
import org.eclipse.kapua.service.device.call.message.KapuaResponsePayload;

public class KuraResponsePayload extends AbstractKuraPayload implements KapuaResponsePayload
{
    @Override
    public KapuaResponseCode getResponseCode()
    {
        return KapuaResponseCode.valueOf((String) getMetric("response.code"));
    }

    @Override
    public String getExceptionMessage()
    {
        return (String) getMetric("response.exception.message");
    }

    @Override
    public String getExceptionStack()
    {
        return (String) getMetric("response.exception.stack");
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
