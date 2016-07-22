package org.eclipse.kapua.transport;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.eclipse.kapua.transport.message.TransportPayload;

public interface TransportClient<C extends TransportChannel, P extends TransportPayload, MQ extends TransportMessage<C, P>, MS extends TransportMessage<C, P>>
{
    //
    // Connection management
    //
    public void connectClient(TransportClientConnectOptions options)
        throws KapuaException;

    public void disconnectClient()
        throws KapuaException;

    public void terminateClient()
        throws KapuaException;

    public boolean isConnected();

    //
    // Message management
    //
    public void sendAndForget(MQ message)
        throws KapuaException;

    public MS send(MQ message, Long timeout)
        throws KapuaException;

    public void clean()
        throws KapuaException;

    //
    // Utilities
    //
    public String getClientId();

    public Class<MQ> getMessageClass();
}
