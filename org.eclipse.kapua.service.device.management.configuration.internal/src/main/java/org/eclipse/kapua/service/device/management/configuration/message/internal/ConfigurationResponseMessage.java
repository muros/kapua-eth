package org.eclipse.kapua.service.device.management.configuration.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.management.response.KapuaResponseMessage;

public class ConfigurationResponseMessage extends KapuaMessageImpl<ConfigurationResponseChannel, ConfigurationResponsePayload>
                                          implements KapuaResponseMessage<ConfigurationResponseChannel, ConfigurationResponsePayload>
{
    private KapuaResponseCode responseCode;

    @Override
    public KapuaResponseCode getResponseCode()
    {
        return responseCode;
    }

    @Override
    public void setResponseCode(KapuaResponseCode responseCode)
    {
        this.responseCode = responseCode;
    }
}
