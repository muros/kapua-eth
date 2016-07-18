package org.eclipse.kapua.message.device.app.request;

import org.eclipse.kapua.message.device.app.KapuaAppChannel;

public interface KapuaRequestChannel extends KapuaAppChannel
{
    public String getMethod();

    public void setMethod(String method);

    public String[] getResources();

    public void setResources(String[] resources);
}
