package org.eclipse.kapua.service.device.call.message;

import org.eclipse.kapua.message.KapuaPayload;

public interface KapuaResponsePayload extends KapuaPayload
{
    public KapuaResponseCode getResponseCode();

    public String getExceptionMessage();

    public String getExceptionStack();

    public byte[] getResponseBody();
}
