/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
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
