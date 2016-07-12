package org.eclipse.kapua.service.device.management.configuration;

import org.eclipse.kapua.model.KapuaObjectFactory;

public interface DeviceConfigurationFactory extends KapuaObjectFactory
{
    public DeviceComponentConfiguration newComponentConfigurationInstance(String componentConfigurationId);

    public DeviceConfiguration newConfigurationInstance();
}
