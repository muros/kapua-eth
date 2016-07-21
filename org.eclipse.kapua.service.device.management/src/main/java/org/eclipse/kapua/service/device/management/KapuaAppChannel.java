package org.eclipse.kapua.service.device.management;

public interface KapuaAppChannel extends KapuaControlChannel
{
    public String getApp();

    public void setApp(String app);

    public String getVersion();

    public void setVersion(String version);
}