package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.service.device.call.message.KapuaResponseMessage;

public interface KapuaDeviceCallHandler<RSM extends KapuaResponseMessage>
{
    public void responseReceived(RSM response);

    public void timedOut();
}
