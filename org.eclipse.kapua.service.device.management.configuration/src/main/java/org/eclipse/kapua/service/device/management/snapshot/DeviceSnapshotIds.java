package org.eclipse.kapua.service.device.management.snapshot;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "snapshotIds")
public interface DeviceSnapshotIds
{
    public List<Long> getSnapshotsIds();
}
