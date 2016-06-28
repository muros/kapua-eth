package org.org.eclipse.kapua.service.device.management.command;

import org.eclipse.kapua.model.KapuaEntityFactory;

public interface DeviceCommandFactory extends KapuaEntityFactory
{
    public DeviceCommandInput newInstance();
}
