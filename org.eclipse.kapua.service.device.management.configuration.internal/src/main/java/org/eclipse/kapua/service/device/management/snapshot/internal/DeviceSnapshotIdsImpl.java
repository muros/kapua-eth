package org.eclipse.kapua.service.device.management.snapshot.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshotIds;

@XmlRootElement(name = "snapshotIds")
public class DeviceSnapshotIdsImpl implements DeviceSnapshotIds
{
    @XmlElement(name = "snapshotId")
    List<Long> snapshotIds;

    public DeviceSnapshotIdsImpl()
    {
        super();
    }

    @Override
    public List<Long> getSnapshotsIds()
    {
        if (snapshotIds == null) {
            snapshotIds = new ArrayList<>();
        }

        return snapshotIds;
    }
}
