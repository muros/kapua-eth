package org.eclipse.kapua.client.message;

public interface KapuaMessage<D extends KapuaDestination, P extends KapuaPayload>
{
    public D getDestination();

    public void setDestination(D destination);

    public P getPayload();

    public void setPayload(P payload);

}
