package org.eclipse.kapua.service.device.management.snapshots;

import org.eclipse.kapua.model.KapuaObjectFactory;

public interface DeviceSnapshotFactory extends KapuaObjectFactory
{
    public DeviceSnapshot newDeviceSnapshot(Long snapshotId);

    public DeviceSnapshotListResult newDeviceSnapshotListResult();
}
