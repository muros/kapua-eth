package org.eclipse.kapua.client;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.message.KapuaDestination;
import org.eclipse.kapua.client.message.KapuaPayload;

public interface KapuaClient<D extends KapuaDestination, P extends KapuaPayload, C extends KapuaClientCallback>
{
    public void publish(D kapuaDestination, P kapuaPayload)
        throws KapuaException;

    public void subscribe(D destination)
        throws KapuaException;

    public void unsubscribe(D desination)
        throws KapuaException;

    void unsubscribeAll()
        throws KapuaException;

    public void setCallback(C clientCallback)
        throws KapuaException;

    public String getClientId();
}
