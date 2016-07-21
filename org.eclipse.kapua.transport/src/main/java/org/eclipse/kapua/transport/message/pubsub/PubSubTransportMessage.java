package org.eclipse.kapua.transport.message.pubsub;

import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.eclipse.kapua.transport.message.TransportPayload;

public interface PubSubTransportMessage<C extends TransportChannel, P extends TransportPayload> extends TransportMessage<C, P>
{
}
