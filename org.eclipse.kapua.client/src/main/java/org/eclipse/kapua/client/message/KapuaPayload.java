package org.eclipse.kapua.client.message;

import org.eclipse.kapua.KapuaException;

public interface KapuaPayload
{
    public byte[] toByteArray();

    public void readFromByteArray(byte[] rawPayload)
        throws KapuaException;
}
