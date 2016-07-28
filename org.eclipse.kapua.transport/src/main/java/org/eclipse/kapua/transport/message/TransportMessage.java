package org.eclipse.kapua.transport.message;

import org.eclipse.kapua.message.Message;

@SuppressWarnings("rawtypes")
public interface TransportMessage<D extends TransportChannel, P extends TransportPayload> extends Message
{

}
