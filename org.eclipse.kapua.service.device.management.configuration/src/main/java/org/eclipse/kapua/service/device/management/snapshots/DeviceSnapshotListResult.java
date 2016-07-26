package org.eclipse.kapua.service.device.management.snapshots;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="snapshot-ids")
public interface DeviceSnapshotListResult extends List<DeviceSnapshot>
{

}
