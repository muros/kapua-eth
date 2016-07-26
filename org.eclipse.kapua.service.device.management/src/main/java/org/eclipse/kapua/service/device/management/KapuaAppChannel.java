package org.eclipse.kapua.service.device.management;

public interface KapuaAppChannel extends KapuaControlChannel
{
    public KapuaAppProperties getAppName();

    public void setAppName(KapuaAppProperties app);

    public KapuaAppProperties getVersion();

    public void setVersion(KapuaAppProperties version);
}