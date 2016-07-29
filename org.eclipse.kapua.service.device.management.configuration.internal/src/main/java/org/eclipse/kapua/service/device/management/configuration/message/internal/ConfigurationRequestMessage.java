package org.eclipse.kapua.service.device.management.configuration.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;

public class ConfigurationRequestMessage extends KapuaMessageImpl<ConfigurationRequestChannel, ConfigurationRequestPayload>
                                         implements KapuaRequestMessage<ConfigurationRequestChannel, ConfigurationRequestPayload>
{
    @SuppressWarnings("unchecked")
    @Override
    public Class<ConfigurationRequestMessage> getRequestClass()
    {
        return ConfigurationRequestMessage.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<ConfigurationResponseMessage> getResponseClass()
    {
        return ConfigurationResponseMessage.class;
    }

}
