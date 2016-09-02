package org.eclipse.kapua.service.device.management.snapshot.internal;

import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshotFactory;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshotIds;

public class DeviceSnapshotFactoryImpl implements DeviceSnapshotFactory
{

    @Override
    public DeviceSnapshotIds newDeviceSnapshotIds()
    {
        return new DeviceSnapshotIdsImpl();
    }

}
