package org.org.eclipse.kapua.service.device.management.bundle;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;

public interface DeviceBundleManagementService extends KapuaService
{
    public DeviceBundleListResult get(KapuaId scopeId, KapuaId deviceId)
        throws KapuaException;

    public void start(KapuaId scopeId, KapuaId deviceId, String bundleId)
        throws KapuaException;

    public void stop(KapuaId scopeId, KapuaId deviceId, String bundleId)
        throws KapuaException;
}
