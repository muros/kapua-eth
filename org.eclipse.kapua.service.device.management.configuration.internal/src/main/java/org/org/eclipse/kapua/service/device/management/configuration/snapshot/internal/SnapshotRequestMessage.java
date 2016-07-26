package org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;

public class SnapshotRequestMessage extends KapuaMessageImpl<SnapshotRequestChannel, SnapshotRequestPayload>implements KapuaRequestMessage<SnapshotRequestChannel, SnapshotRequestPayload>
{
    @SuppressWarnings("unchecked")
    @Override
    public Class<SnapshotRequestMessage> getRequestClass()
    {
        return SnapshotRequestMessage.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<SnapshotResponseMessage> getResponseClass()
    {
        return SnapshotResponseMessage.class;
    }

}
