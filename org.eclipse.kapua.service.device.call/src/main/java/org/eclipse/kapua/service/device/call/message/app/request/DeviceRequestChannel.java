package org.eclipse.kapua.service.device.call.message.app.request;

import org.eclipse.kapua.service.device.call.message.app.DeviceAppChannel;

public interface DeviceRequestChannel extends DeviceAppChannel
{
    public String getMethod();

    public void setMethod(String method);

    public String[] getResources();

    public void setResources(String[] resources);

}
