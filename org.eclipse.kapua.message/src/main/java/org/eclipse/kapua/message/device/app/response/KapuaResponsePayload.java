package org.eclipse.kapua.message.device.app.response;

import org.eclipse.kapua.message.KapuaPayload;

public interface KapuaResponsePayload extends KapuaPayload
{
    public KapuaResponseCode getResponseCode();

    public String getExceptionMessage();

    public String getExceptionStack();

    public byte[] getResponseBody();
}
