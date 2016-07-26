package org.eclipse.kapua.service.device.management.snapshot.internal;

import org.eclipse.kapua.service.device.management.KapuaAppProperties;

public enum SnapshotAppProperties implements KapuaAppProperties
{
	APP_NAME("SNAPSHOT"),
    APP_VERSION("1.0.0"),
    ;

    private String value;

    SnapshotAppProperties(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }
	
}
