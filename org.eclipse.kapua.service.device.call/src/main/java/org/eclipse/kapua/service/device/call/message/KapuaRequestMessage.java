package org.eclipse.kapua.service.device.call.message;

import org.eclipse.kapua.message.KapuaMessage;

public interface KapuaRequestMessage<D extends KapuaRequestDestination, P extends KapuaRequestPayload> extends KapuaMessage<D, P>
{

}
