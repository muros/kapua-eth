package org.eclipse.kapua.service.device.call.message;

public interface KapuaResponseDestination extends KapuaAppDestination
{
    public String getRequestId();

    public void setRequestId(String requestId);
}
