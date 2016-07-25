package org.eclipse.kapua.transport;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.eclipse.kapua.transport.message.TransportPayload;

public interface TransportFacade<C extends TransportChannel, P extends TransportPayload, MQ extends TransportMessage<C, P>, MS extends TransportMessage<C, P>>
{
    //
    // Message management
    //
    public void sendAsync(MQ message)
        throws KapuaException;

    public MS sendSync(MQ message, Long timeout)
        throws KapuaException;

    //
    // Utilities
    //
    public String getClientId();

    public void clean();

    public Class<MQ> getMessageClass();
}
