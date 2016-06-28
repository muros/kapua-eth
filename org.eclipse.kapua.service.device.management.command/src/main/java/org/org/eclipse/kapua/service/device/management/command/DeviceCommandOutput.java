package org.org.eclipse.kapua.service.device.management.command;

public interface DeviceCommandOutput
{
    public String getStderr();

    public String getStdout();

    public String getExceptionMessage();
}
