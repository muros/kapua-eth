package org.org.eclipse.kapua.service.device.management.bundle.message.internal;

import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;

public class BundleRequestMessage extends KapuaMessageImpl<BundleRequestChannel, BundleRequestPayload>
                                         implements KapuaRequestMessage<BundleRequestChannel, BundleRequestPayload>
{
    @SuppressWarnings("unchecked")
    @Override
    public Class<BundleRequestMessage> getRequestClass()
    {
        return BundleRequestMessage.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<BundleResponseMessage> getResponseClass()
    {
        return BundleResponseMessage.class;
    }

}
