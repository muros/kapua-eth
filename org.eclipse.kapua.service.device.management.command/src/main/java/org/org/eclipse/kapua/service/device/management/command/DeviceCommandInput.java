package org.org.eclipse.kapua.service.device.management.command;

public interface DeviceCommandInput
{
    public void setCommand(String command);

    public void setPassword(String password);

    public void setArguments(String[] arguments);

    public void setTimeout(Integer timeout);

    public void setWorkingDir(String workingDir);

    public void setBytes(byte[] bytes);
}
