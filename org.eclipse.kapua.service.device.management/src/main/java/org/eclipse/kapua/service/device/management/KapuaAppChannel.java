package org.eclipse.kapua.service.device.management;

public interface KapuaAppChannel extends KapuaControlChannel
{
    public KapuaAppProperties getApp();

    public void setApp(KapuaAppProperties app);

    public KapuaAppProperties getVersion();

    public void setVersion(KapuaAppProperties version);
}