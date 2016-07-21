package org.eclipse.kapua.service.device.management.request;

import org.eclipse.kapua.service.device.management.KapuaAppChannel;
import org.eclipse.kapua.service.device.management.KapuaMethod;

public interface KapuaRequestChannel extends KapuaAppChannel
{
    public KapuaMethod getMethod();

    public void setMethod(KapuaMethod method);

    public String[] getResources();

    public void setResources(String[] resources);
}
