package org.eclipse.kapua.service.device.call.kura;

import java.util.ArrayList;

import org.eclipse.kapua.service.device.call.DeviceCallback;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;

public class KuraDeviceResponseContainer extends ArrayList<KuraResponseMessage> implements DeviceCallback<KuraResponseMessage>
{
    private static final long serialVersionUID = -6909761350290400843L;

    public KuraDeviceResponseContainer()
    {
        super();
    }

    @Override
    public void responseReceived(KuraResponseMessage response)
    {
        synchronized (this) {
            add(response);
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
