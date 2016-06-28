package org.org.eclipse.kapua.service.device.management.deploy;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;

public interface DeviceDeployManagementService extends KapuaService
{
    public DeviceDeplomentPackageListResult get(KapuaId scopeId, KapuaId deviceId)
        throws KapuaException;

    public void install(KapuaId scopeId, KapuaId deviceId, String name, byte[] deviceDeploymentPackage)
        throws KapuaException;

    public void install(KapuaId scopeId, KapuaId deviceId, String deviceDeploymentPackageUrl)
        throws KapuaException;

    public void uninstall(KapuaId scopeId, KapuaId deviceId, String deviceDeploymentPackageId)
        throws KapuaException;
}
