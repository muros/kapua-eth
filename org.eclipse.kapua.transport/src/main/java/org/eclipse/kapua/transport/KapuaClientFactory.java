package org.eclipse.kapua.transport;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.eclipse.kapua.transport.message.TransportPayload;

public interface KapuaClientFactory<D extends TransportChannel, P extends TransportPayload, MQ extends TransportMessage<D, P>, MS extends TransportMessage<D, P>, C extends TransportClient<D, P, MQ, MS>, CO extends KapuaClientConnectOptions>
                                   extends KapuaObjectFactory
{
    public C newInstance();

    public CO newConnectOptions();
}
