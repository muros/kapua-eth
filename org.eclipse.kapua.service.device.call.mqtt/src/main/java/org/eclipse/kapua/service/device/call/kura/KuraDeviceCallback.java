package org.eclipse.kapua.service.device.call.kura;

import org.eclipse.kapua.service.device.call.DeviceCallback;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;

public class KuraDeviceCallback implements DeviceCallback<KuraResponseMessage>
{
    private KuraDeviceResponseContainer responseContainer;

    public KuraDeviceCallback(KuraDeviceResponseContainer responseContainer)
    {
        this.responseContainer = responseContainer;
    }

    @Override
    public void responseReceived(KuraResponseMessage response)
    {
        responseContainer.add(response);
    }

    @Override
    public void timedOut()
    {
        notifyAll();
    }
}
