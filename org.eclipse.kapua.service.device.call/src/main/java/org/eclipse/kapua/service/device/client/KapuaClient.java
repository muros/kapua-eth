package org.eclipse.kapua.service.device.client;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.message.KapuaDestination;
import org.eclipse.kapua.service.device.message.KapuaPayload;

public interface KapuaClient
{
    public void publish(KapuaDestination destination, KapuaPayload payload)
        throws KapuaException;

    public void subscribe(KapuaDestination destination)
        throws KapuaException;

    public void unsubscribe(KapuaDestination desination)
        throws KapuaException;

    public void setCallback(KapuaClientCallback clientCallback)
        throws KapuaException;

    public String getClientId();
}
