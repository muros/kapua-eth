package org.org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaTopic;

public interface KapuaDeviceCall
{
    public KapuaMessage sendRequest(KapuaTopic requestTopic,
                                    KapuaTopic responseTopic,
                                    KapuaPayload reqPayload,
                                    long timeout)
        throws KapuaException;

}
