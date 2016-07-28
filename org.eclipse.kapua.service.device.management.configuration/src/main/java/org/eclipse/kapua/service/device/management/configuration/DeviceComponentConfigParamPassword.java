package org.eclipse.kapua.service.device.management.configuration;

public class DeviceComponentConfigParamPassword
{
    private String password;

    public DeviceComponentConfigParamPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    @Override
    public String toString()
    {
        return password;
    }
}
