package org.eclipse.kapua.message;

import org.eclipse.kapua.message.device.app.request.KapuaRequestChannel;
import org.eclipse.kapua.message.device.app.request.KapuaRequestMessage;
import org.eclipse.kapua.message.device.app.request.KapuaRequestPayload;
import org.eclipse.kapua.model.KapuaObjectFactory;

public interface KapuaMessageFactory extends KapuaObjectFactory
{
    @SuppressWarnings("rawtypes")
    public KapuaMessage newMessage();

    public KapuaRequestMessage newRequestMessage();

    public KapuaChannel newChannel();

    public KapuaRequestChannel newRequestChannel();

    public KapuaPayload newPayload();

    public KapuaRequestPayload newRequestPayload();

}
