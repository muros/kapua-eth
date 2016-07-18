package org.eclipse.kapua.message.device.app;

import org.eclipse.kapua.message.KapuaChannel;

public interface KapuaAppChannel extends KapuaChannel
{
    public String getApp();

    public void setApp(String app);
}
