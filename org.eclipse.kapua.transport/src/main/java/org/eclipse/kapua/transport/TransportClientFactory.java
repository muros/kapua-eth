package org.eclipse.kapua.transport;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.eclipse.kapua.transport.message.TransportPayload;

public interface TransportClientFactory<D extends TransportChannel, P extends TransportPayload, MQ extends TransportMessage<D, P>, MS extends TransportMessage<D, P>, C extends TransportFacade<D, P, MQ, MS>, CO extends TransportClientConnectOptions>
                                       extends KapuaObjectFactory
{
    public C getFacade()
        throws Exception;

    public CO newConnectOptions();
}
