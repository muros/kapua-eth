package org.eclipse.kapua.service.device.call.message.app.response;

import org.eclipse.kapua.service.device.call.message.app.DeviceAppChannel;

public interface DeviceResponseChannel extends DeviceAppChannel
{
    public String getReplyPart();

    public void setReplyPart(String replyPart);

    public String getRequestId();

    public void setRequestId(String requestId);
}
