package org.eclipse.kapua.service.device.call.message;

public interface KapuaRequestDestination extends KapuaAppDestination
{
    public String getMethod();

    public void setMethod(String method);

    public String[] getResources();

    public void setResources(String[] resources);

}
