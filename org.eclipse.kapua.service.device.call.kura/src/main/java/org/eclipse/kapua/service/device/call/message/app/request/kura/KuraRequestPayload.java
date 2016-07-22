package org.eclipse.kapua.service.device.call.message.app.request.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;

public class KuraRequestPayload extends KuraPayload implements DeviceRequestPayload
{
    @Override
    public void setRequestId(String requestId)
    {
        metrics().put("resquest.id", requestId);
    }

    @Override
    public void setRequesterClientId(String requesterClientId)
    {
        metrics().put("requester.client.id", requesterClientId);
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
