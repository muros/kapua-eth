package org.eclipse.kapua.service.device.call.message.app;

import org.eclipse.kapua.service.device.call.message.DeviceChannel;

public interface DeviceAppChannel extends DeviceChannel
{
    public String getAppId();

    public void setAppId(String appId);
}
