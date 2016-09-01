package org.eclipse.kapua.service.device.management.snapshot;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;

public interface DeviceSnapshotManagementService extends KapuaService
{
    public DeviceSnapshotIds get(KapuaId scopeId, KapuaId deviceid, Long timeout)
        throws KapuaException;

    public void rollback(KapuaId scopeId, KapuaId deviceid, String snapshotId, Long timeout)
        throws KapuaException;
}
