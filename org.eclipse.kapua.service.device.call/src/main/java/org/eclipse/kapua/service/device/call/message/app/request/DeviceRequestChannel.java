package org.eclipse.kapua.service.device.call.message.app.request;

import org.eclipse.kapua.service.device.call.DeviceMethod;
import org.eclipse.kapua.service.device.call.message.app.DeviceAppChannel;

public interface DeviceRequestChannel extends DeviceAppChannel
{
    public DeviceMethod getMethod();

    public void setMethod(DeviceMethod method);

    public String[] getResources();

    public void setResources(String[] resources);

    public String getRequestId();

    public void setRequestId(String requestId);

    public String getRequesterClientId();

    public void setRequesterClientId(String requesterClientId);

}
