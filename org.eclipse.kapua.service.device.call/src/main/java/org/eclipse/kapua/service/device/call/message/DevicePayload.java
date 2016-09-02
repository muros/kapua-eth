package org.eclipse.kapua.service.device.call.message;

import java.util.Date;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.message.Payload;

public interface DevicePayload extends Payload
{
    public Date getTimestamp();

    public DevicePosition getPosition();

    public Map<String, Object> getMetrics();

    public byte[] getBody();

    //
    // Encode/Decode stuff
    //
    public byte[] toByteArray();

    public void readFromByteArray(byte[] rawPayload)
        throws KapuaException;
}
