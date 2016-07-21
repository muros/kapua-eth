package org.eclipse.kapua.message.internal;

import org.eclipse.kapua.message.KapuaChannel;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaMessageFactory;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;

public class KapuaMessageFactoryImpl implements KapuaMessageFactory
{

    @SuppressWarnings("rawtypes")
    @Override
    public KapuaMessage newMessage()
    {
        return new KapuaMessageImpl();
    }

    @Override
    public KapuaChannel newChannel()
    {
        return new KapuaChannelImpl();
    }

    @Override
    public KapuaPayload newPayload()
    {
        return new KapuaPayloadImpl();
    }

    @Override

    public KapuaPosition newPosition()
    {
        return new KapuaPositionImpl();
    }

}
