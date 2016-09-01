package org.eclipse.kapua.service.device.management.snapshot;

import org.eclipse.kapua.model.KapuaObjectFactory;

public interface DeviceSnapshotFactory extends KapuaObjectFactory
{
    public DeviceSnapshotIds newDeviceSnapshotIds();
}
