package org.eclipse.kapua.service.device.management.configuration;

import java.util.Map;

import org.eclipse.kapua.model.config.metatype.Tocd;

public interface DeviceComponentConfiguration
{
    public String getComponentId();

    public void setComponentId(String componentId);

    public String getComponentName();

    public void setComponentName(String unescapedComponentName);

    public Tocd getDefinition();

    public Map<String, Object> getProperties();

    public void setProperties(Map<String, Object> properties);

}
