package org.eclipse.kapua.service.device.management.configuration.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;

@XmlRootElement(name = "configurations")
public class DeviceConfigurationImpl implements DeviceConfiguration
{
    @XmlElement(name = "configuration")
    private List<DeviceComponentConfigurationImpl> configurations;

    public DeviceConfigurationImpl()
    {
        configurations = new ArrayList<>();
    }

    @Override
    public List<DeviceComponentConfigurationImpl> getComponentConfigurations()
    {
        return configurations;
    }

}
