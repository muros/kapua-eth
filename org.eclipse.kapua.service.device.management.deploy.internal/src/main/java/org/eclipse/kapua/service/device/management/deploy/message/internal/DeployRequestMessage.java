package org.eclipse.kapua.service.device.management.deploy.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;

public class DeployRequestMessage extends KapuaMessageImpl<DeployRequestChannel, DeployRequestPayload>
                                         implements KapuaRequestMessage<DeployRequestChannel, DeployRequestPayload>
{
    @SuppressWarnings("unchecked")
    @Override
    public Class<DeployRequestMessage> getRequestClass()
    {
        return DeployRequestMessage.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<DeployResponseMessage> getResponseClass()
    {
        return DeployResponseMessage.class;
    }

}
