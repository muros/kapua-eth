package org.eclipse.kapua.service.device.call.message;

import org.eclipse.kapua.message.Channel;

public interface DeviceChannel extends Channel
{
    public String getMessageClassification();

    public void setMessageClassification(String messageClassification);

    public String getScope();

    public void setScope(String scope);

    public String getClientId();

    public void setClientId(String clientId);
}
