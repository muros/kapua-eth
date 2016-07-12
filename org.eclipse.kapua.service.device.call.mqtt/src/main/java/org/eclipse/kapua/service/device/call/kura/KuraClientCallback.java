package org.eclipse.kapua.service.device.call.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.KapuaClientCallback;
import org.eclipse.kapua.service.device.call.message.kura.KuraResponseDestination;
import org.eclipse.kapua.service.device.call.message.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraResponsePayload;

public class KuraClientCallback extends KuraDeviceCallHandler implements KapuaClientCallback<KuraResponseDestination, KuraResponsePayload, KuraResponseMessage>
{
    public KuraClientCallback(KuraDeviceCallHandler responseContainer)
    {
        super(responseContainer.responsesContainer);
    }

    @Override
    public void clientConnectionLost(Throwable cause)
        throws KapuaException
    {
        notifyAll();
    }

    @Override
    public void messageArrived(KuraResponseMessage messageArrived)
    {
        super.responseReceived(messageArrived);
    }

}
