package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.message.request.KapuaRequestDestination;
import org.eclipse.kapua.service.device.message.request.KapuaRequestMessage;
import org.eclipse.kapua.service.device.message.response.KapuaResponseDestination;
import org.eclipse.kapua.service.device.message.response.KapuaResponseMessage;

public interface KapuaDeviceCall<RQM extends KapuaRequestMessage, RSM extends KapuaResponseMessage, RQD extends KapuaRequestDestination, RSD extends KapuaResponseDestination, H extends KapuaDeviceCallHandler<RSM>>
{
    public RSM send()
        throws KapuaException;

    public RSD sendAsync()
        throws KapuaException;

    public RSD sendAsync(H kapuaDeviceCallHandler)
        throws KapuaException;
}
