package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.KapuaResponseDestination;
import org.eclipse.kapua.service.device.call.message.KapuaResponseMessage;
import org.eclipse.kapua.service.device.call.message.KapuaResponsePayload;

public interface KapuaDeviceCall<RSD extends KapuaResponseDestination, RSP extends KapuaResponsePayload, RSM extends KapuaResponseMessage<RSD, RSP>, H extends KapuaDeviceCallHandler<RSM>>
{
    public RSM send()
        throws KapuaException;

    public RSD sendAsync()
        throws KapuaException;

    public RSD sendAsync(H kapuaDeviceCallHandler)
        throws KapuaException;
}
