package org.eclipse.kapua.transport;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.transport.message.TransportMessage;

@SuppressWarnings("rawtypes")
public interface TransportCallback<M extends TransportMessage>
{
    public void clientConnectionLost(Throwable cause)
        throws KapuaException;

    public void messageArrived(M messageArrived)
        throws KapuaException;
}
