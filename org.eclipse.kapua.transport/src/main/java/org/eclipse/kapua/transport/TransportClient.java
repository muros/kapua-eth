package org.eclipse.kapua.transport;

import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.eclipse.kapua.transport.message.TransportPayload;

public interface TransportClient<C extends TransportChannel, P extends TransportPayload, M extends TransportMessage<C, P>, TB extends TransportCallback<M>>
{
    //
    // Connection management
    //
    public void connectClient(KapuaClientConnectOptions options)
        throws KapuaException;

    public void disconnectClient()
        throws KapuaException;

    public void terminateClient()
        throws KapuaException;

    public boolean isConnected();

    //
    // Message management
    //
    public void publish(M message)
        throws KapuaException;

    public void subscribe(C channel)
        throws KapuaException;

    public void unsubscribe(C channel)
        throws KapuaException;

    void unsubscribeAll()
        throws KapuaException;

    //
    // Client callback management
    //
    public void newCallback(List<Message> responseContainer, Class<Message> clazz)
        throws KapuaException;

    public void clearCallback()
        throws KapuaException;

    //
    // Utilities
    //
    public String getClientId();

    public Class<M> getMessageClass();
}
