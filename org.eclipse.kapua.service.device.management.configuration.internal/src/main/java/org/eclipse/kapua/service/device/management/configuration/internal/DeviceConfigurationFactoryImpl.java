package org.eclipse.kapua.service.device.management.configuration.internal;

import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;

public class DeviceConfigurationFactoryImpl implements DeviceConfigurationFactory
{

    @Override
    public DeviceComponentConfiguration newComponentConfigurationInstance(String componentConfigurationId)
    {
        return new DeviceComponentConfigurationImpl(componentConfigurationId);
    }

    @Override
    public DeviceConfiguration newConfigurationInstance()
    {
        return new DeviceConfigurationImpl();
    }

}
