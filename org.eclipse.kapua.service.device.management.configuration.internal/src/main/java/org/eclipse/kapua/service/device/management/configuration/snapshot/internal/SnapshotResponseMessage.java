package org.eclipse.kapua.service.device.management.configuration.snapshot.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.management.response.KapuaResponseMessage;

public class SnapshotResponseMessage extends KapuaMessageImpl<SnapshotResponseChannel, SnapshotResponsePayload>implements KapuaResponseMessage<SnapshotResponseChannel, SnapshotResponsePayload>
{
    private KapuaResponseCode responseCode;

    @Override
    public KapuaResponseCode getResponseCode()
    {
        return responseCode;
    }

    @Override
    public void setResponseCode(KapuaResponseCode responseCode)
    {
        this.responseCode = responseCode;
    }
}
