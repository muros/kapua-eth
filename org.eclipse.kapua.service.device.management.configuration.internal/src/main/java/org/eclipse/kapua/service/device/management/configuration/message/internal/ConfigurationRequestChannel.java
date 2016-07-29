package org.eclipse.kapua.service.device.management.configuration.message.internal;

import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.commons.message.response.KapuaAppChannelImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestChannel;

public class ConfigurationRequestChannel extends KapuaAppChannelImpl implements KapuaRequestChannel
{
    private KapuaMethod method;
    private String      componentId;

    @Override
    public KapuaMethod getMethod()
    {
        return method;
    }

    @Override
    public void setMethod(KapuaMethod method)
    {
        this.method = method;
    }

    public String getComponentId()
    {
        return componentId;
    }

    public void setComponentId(String componentId)
    {
        this.componentId = componentId;
    }
}
