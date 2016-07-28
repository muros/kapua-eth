package org.eclipse.kapua.service.device.management.snapshots;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;

public interface DeviceSnapshotManagementService extends KapuaService
{
    public DeviceSnapshotListResult get(KapuaId scopeId, KapuaId deviceid, Long timeout)
        throws KapuaException;

    public DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId, String snapshotId, Long timeout)
        throws KapuaException;

    public void rollback(KapuaId scopeId, KapuaId deviceid, String snapshotId, Long timeout)
        throws KapuaException;
}
