package org.eclipse.kapua.service.device.management.configuration.internal;

import org.eclipse.kapua.service.device.management.KapuaAppProperties;

public enum ConfigurationAppProperties implements KapuaAppProperties
{
    APP_NAME("CONFIGURATION"), 
    APP_VERSION("1.0.0"),
    ;

    private String value;

    ConfigurationAppProperties(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }

}
