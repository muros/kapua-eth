package org.org.eclipse.kapua.service.device.management.bundle.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.management.response.KapuaResponseMessage;

public class BundleResponseMessage extends KapuaMessageImpl<BundleResponseChannel, BundleResponsePayload>
                                          implements KapuaResponseMessage<BundleResponseChannel, BundleResponsePayload>
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
