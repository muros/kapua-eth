package org.eclipse.kapua.message;

import org.eclipse.kapua.KapuaException;

public interface KapuaPayload
{
    public byte[] toByteArray();

    public void readFromByteArray(byte[] rawPayload)
        throws KapuaException;

    public String toDisplayString();
}
