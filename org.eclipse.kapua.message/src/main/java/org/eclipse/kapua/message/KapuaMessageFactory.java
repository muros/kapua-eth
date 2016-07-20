package org.eclipse.kapua.message;

import org.eclipse.kapua.model.KapuaObjectFactory;

public interface KapuaMessageFactory extends KapuaObjectFactory
{
    @SuppressWarnings("rawtypes")
    public KapuaMessage newMessage();

    public KapuaChannel newChannel();

    public KapuaPayload newPayload();

    public KapuaPosition newPosition();

}
