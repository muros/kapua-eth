package org.org.eclipse.kapua.service.device.management.bundle.internal;

import org.eclipse.kapua.service.device.management.KapuaAppProperties;

public enum BundleAppProperties implements KapuaAppProperties
{
	APP_NAME("BUNDLE"),
    APP_VERSION("1.0.0"),
    ;

    private String value;

    BundleAppProperties(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }
	
}
