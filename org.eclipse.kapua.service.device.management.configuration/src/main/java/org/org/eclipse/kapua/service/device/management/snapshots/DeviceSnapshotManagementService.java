package org.org.eclipse.kapua.service.device.management.snapshots;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;

public interface DeviceSnapshotManagementService extends KapuaService
{
    public DeviceSnapshotListResult get(KapuaId scopeId, KapuaId deviceid)
        throws KapuaException;

    public void exec(KapuaId scopeId, KapuaId deviceid, DeviceConfiguration snapshotId)
        throws KapuaException;

    public void rollback(KapuaId scopeId, KapuaId deviceid, String snapshotId)
        throws KapuaException;
}
