package org.eclipse.kapua.service.device.management.configuration;

import org.eclipse.kapua.model.KapuaEntityFactory;

public interface DeviceConfigurationFactory extends KapuaEntityFactory
{
    public DeviceComponentConfiguration newComponentConfigurationInstance(String componentConfigurationId);

    public DeviceConfiguration newConfigurationInstance();
}
