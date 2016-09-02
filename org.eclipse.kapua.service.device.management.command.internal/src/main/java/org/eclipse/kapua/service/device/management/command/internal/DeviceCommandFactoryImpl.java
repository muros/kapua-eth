package org.eclipse.kapua.service.device.management.command.internal;

import org.eclipse.kapua.service.device.management.command.DeviceCommandFactory;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;

public class DeviceCommandFactoryImpl implements DeviceCommandFactory
{

    @Override
    public DeviceCommandInput newCommandInput()
    {
        return new DeviceCommandInputImpl();
    }

    @Override
    public DeviceCommandOutput newCommandOutput()
    {
        return new DeviceCommandOutputImpl();
    }

}
