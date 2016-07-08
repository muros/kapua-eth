package org.eclipse.kapua.client;

import org.apache.commons.pool2.ObjectPool;
import org.eclipse.kapua.client.message.KapuaDestination;
import org.eclipse.kapua.client.message.KapuaPayload;
import org.eclipse.kapua.locator.KapuaLocator;

public interface KapuaClientPool<D extends KapuaDestination, P extends KapuaPayload, CB extends KapuaClientCallback, C extends KapuaClient<D, P, CB>> extends ObjectPool<C>
{
    @SuppressWarnings("rawtypes")
    public static KapuaClientPool clientPoolInstance = getInstance();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <D extends KapuaDestination, P extends KapuaPayload, CB extends KapuaClientCallback, C extends KapuaClient<D, P, CB>> KapuaClientPool<D, P, CB, C> getInstance()
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        KapuaClientPoolFactory clientPoolFactory = locator.getFactory(KapuaClientPoolFactory.class);
        return clientPoolFactory.newInstance();
    }
}
