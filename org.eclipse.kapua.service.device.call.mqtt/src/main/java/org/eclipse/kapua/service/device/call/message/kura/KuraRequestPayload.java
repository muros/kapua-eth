package org.eclipse.kapua.service.device.call.message.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.KapuaRequestPayload;

public class KuraRequestPayload extends AbstractKuraPayload implements KapuaRequestPayload
{
    @Override
    public void setRequestId(String requestId)
    {
        addMetric("resquest.id", requestId);
    }

    @Override
    public void setRequesterClientId(String requesterClientId)
    {
        addMetric("resquester.client.id", requesterClientId);
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
