package org.eclipse.kapua.service.device.management.request;

import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;

public interface KapuaRequestMessage<C extends KapuaRequestChannel, P extends KapuaPayload> extends KapuaMessage<C, P>
{

}
