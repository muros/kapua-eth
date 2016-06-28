package org.org.eclipse.kapua.service.device.management.configuration;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;

public interface DeviceConfigurationManagementService extends KapuaService
{
    public DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId, String configurationComponentPid)
        throws KapuaException;

    public void put(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration configuration)
        throws KapuaException;

    public void apply(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration configuration)
        throws KapuaException;

    public void rollback(KapuaId scopeId, KapuaId deviceId, String configurationId)
        throws KapuaException;
}
