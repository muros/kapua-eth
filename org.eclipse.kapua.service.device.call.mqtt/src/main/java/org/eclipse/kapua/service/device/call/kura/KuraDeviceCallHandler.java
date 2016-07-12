package org.eclipse.kapua.service.device.call.kura;

import java.util.List;

import org.eclipse.kapua.service.device.call.KapuaDeviceCallHandler;
import org.eclipse.kapua.service.device.call.message.kura.KuraResponseMessage;

public class KuraDeviceCallHandler implements KapuaDeviceCallHandler<KuraResponseMessage>
{
    List<KuraResponseMessage> responsesContainer;

    public KuraDeviceCallHandler(List<KuraResponseMessage> responseContainer)
    {
        this.responsesContainer = responseContainer;
    }

    @Override
    public void responseReceived(KuraResponseMessage response)
    {
        synchronized (this) {
            responsesContainer.add(response);
            notifyAll();
        }
    }

    @Override
    public void timedOut()
    {
        synchronized (this) {
            notifyAll();
        }
    }
}
