package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.service.device.call.message.DeviceMessage;

@SuppressWarnings("rawtypes")
public interface DeviceCallback<RSM extends DeviceMessage>
{
    public void responseReceived(RSM response);

    public void timedOut();
}
