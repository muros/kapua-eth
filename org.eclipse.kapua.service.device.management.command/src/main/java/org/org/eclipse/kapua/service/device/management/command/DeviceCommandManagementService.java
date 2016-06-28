package org.org.eclipse.kapua.service.device.management.command;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;

public interface DeviceCommandManagementService extends KapuaService
{
    public DeviceCommandOutput exec(KapuaId scopeId, KapuaId deviceId, DeviceCommandInput commandInput)
        throws KapuaException;
}
