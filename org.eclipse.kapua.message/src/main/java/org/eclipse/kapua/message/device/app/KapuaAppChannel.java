package org.eclipse.kapua.message.device.app;

public interface KapuaAppChannel extends KapuaControlChannel
{
    public String getApp();

    public void setApp(String app);
}
