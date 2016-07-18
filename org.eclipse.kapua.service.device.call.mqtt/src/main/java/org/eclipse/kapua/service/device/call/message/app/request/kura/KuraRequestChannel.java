package org.eclipse.kapua.service.device.call.message.app.request.kura;

import org.eclipse.kapua.service.device.call.message.app.kura.KuraAppChannel;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestChannel;

public class KuraRequestChannel extends KuraAppChannel implements DeviceRequestChannel
{
    private String   method;
    private String[] resources;

    public KuraRequestChannel()
    {
        super();
    }

    public KuraRequestChannel(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public KuraRequestChannel(String controlDestinationPrefix, String scopeNamespace, String clientId)
    {
        super(controlDestinationPrefix, scopeNamespace, clientId);
    }

    @Override
    public String getMethod()
    {
        return method;
    }

    @Override
    public void setMethod(String method)
    {
        this.method = method;
    }

    @Override
    public String[] getResources()
    {
        return resources;
    }

    @Override
    public void setResources(String[] resources)
    {
        this.resources = resources;
    }
}
