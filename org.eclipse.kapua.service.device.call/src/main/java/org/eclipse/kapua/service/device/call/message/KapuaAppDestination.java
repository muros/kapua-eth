package org.eclipse.kapua.service.device.call.message;

import org.eclipse.kapua.client.message.KapuaDestination;

public interface KapuaAppDestination extends KapuaDestination
{
    public String getAppId();

    public void setAppId(String appId);
}
