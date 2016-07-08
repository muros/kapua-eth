package org.eclipse.kapua.service.device.call.kura;

import java.util.List;

import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.service.device.call.KapuaDeviceCallHandler;

public class KuraDeviceCallHandlerImpl implements KapuaDeviceCallHandler
{
    List<KapuaMessage> responsesContainer;

    public KuraDeviceCallHandlerImpl(List<KapuaMessage> responseContainer)
    {
        this.responsesContainer = responseContainer;
    }

    @Override
    public void responseReceived(KapuaMessage response)
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
