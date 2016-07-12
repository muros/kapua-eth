package org.eclipse.kapua.client;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.message.KapuaDestination;
import org.eclipse.kapua.client.message.KapuaMessage;
import org.eclipse.kapua.client.message.KapuaPayload;

public interface KapuaClientCallback<D extends KapuaDestination, P extends KapuaPayload, M extends KapuaMessage<D, P>>
{
    public void clientConnectionLost(Throwable cause)
        throws KapuaException;

    public void messageArrived(M messageArrived);
}
