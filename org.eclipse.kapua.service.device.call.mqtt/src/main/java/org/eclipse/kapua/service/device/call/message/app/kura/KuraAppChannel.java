package org.eclipse.kapua.service.device.call.message.app.kura;

import org.eclipse.kapua.service.device.call.message.app.DeviceAppChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;

public abstract class KuraAppChannel extends KuraChannel implements DeviceAppChannel
{
    protected String appId;

    public KuraAppChannel()
    {
        super();
    }

    public KuraAppChannel(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public KuraAppChannel(String controlDestinationPrefix, String scopeNamespace, String clientId)
    {
        super(controlDestinationPrefix, scopeNamespace, clientId);
    }

    @Override
    public String getAppId()
    {
        return appId;
    }

    @Override
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
}
