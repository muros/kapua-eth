package org.eclipse.kapua.message.device.app.response;

import org.eclipse.kapua.message.device.app.KapuaAppChannel;

public interface KapuaResponseChannel extends KapuaAppChannel
{
    public String getReplyPart();

    public void setReplyPart(String replyPart);

    public String getRequestId();

    public void setRequestId(String requestId);
}
