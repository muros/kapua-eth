package org.eclipse.kapua.service.device.management.response;

import org.eclipse.kapua.message.KapuaPayload;

public interface KapuaResponsePayload extends KapuaPayload
{
    public String getExceptionMessage();

    public void setExceptionMessage(String setExecptionMessage);

    public String getExceptionStack();

    public void setExceptionStack(String setExecptionStack);
}
