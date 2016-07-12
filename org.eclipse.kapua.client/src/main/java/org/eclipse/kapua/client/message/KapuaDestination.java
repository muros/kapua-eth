package org.eclipse.kapua.client.message;

public interface KapuaDestination
{
    public String toClientDestination();

    public void fromClientDestination(String destination);
}
