package org.eclipse.kapua.service.device.call.message.app.response.kura;

import org.eclipse.kapua.service.device.call.message.app.kura.KuraAppChannel;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseChannel;

public class KuraResponseChannel extends KuraAppChannel implements DeviceResponseChannel
{
    private String replyPart;
    private String requestId;

    public KuraResponseChannel(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public KuraResponseChannel(String controlDestinationPrefix, String scopeNamespace, String clientId)
    {
        super(controlDestinationPrefix, scopeNamespace, clientId);
    }

    @Override
    public String getReplyPart()
    {
        return replyPart;
    }

    @Override
    public void setReplyPart(String replyPart)
    {
        this.replyPart = replyPart;
    }

    @Override
    public String getRequestId()
    {
        return requestId;
    }

    @Override
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }
}
