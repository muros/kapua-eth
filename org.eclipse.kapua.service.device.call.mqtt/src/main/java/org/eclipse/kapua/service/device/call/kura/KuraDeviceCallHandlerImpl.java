package org.eclipse.kapua.service.device.call.kura;

import java.util.List;

import org.eclipse.kapua.service.device.call.KapuaDeviceCallHandler;
import org.eclipse.kapua.service.device.message.response.KapuaResponseMessage;

public class KuraDeviceCallHandlerImpl implements KapuaDeviceCallHandler<KapuaResponseMessage>
{
    List<KapuaResponseMessage> responsesContainer;

    public KuraDeviceCallHandlerImpl(List<KapuaResponseMessage> responseContainer)
    {
        this.responsesContainer = responseContainer;
    }

    @Override
    public void responseReceived(KapuaResponseMessage response)
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
