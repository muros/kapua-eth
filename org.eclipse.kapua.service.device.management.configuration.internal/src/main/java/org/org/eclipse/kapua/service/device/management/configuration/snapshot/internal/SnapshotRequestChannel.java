package org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal;

import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.commons.message.response.KapuaAppChannelImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestChannel;

public class SnapshotRequestChannel extends KapuaAppChannelImpl implements KapuaRequestChannel
{
    private KapuaMethod method;
    private String      snapshotId;

    @Override
    public KapuaMethod getMethod()
    {
        return method;
    }

    @Override
    public void setMethod(KapuaMethod method)
    {
        this.method = method;
    }

    public String getSnapshotId()
    {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId)
    {
        this.snapshotId = snapshotId;
    }
}
