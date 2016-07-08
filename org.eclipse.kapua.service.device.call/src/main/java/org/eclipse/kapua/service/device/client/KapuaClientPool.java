package org.eclipse.kapua.service.device.client;

import org.apache.commons.pool2.ObjectPool;
import org.eclipse.kapua.locator.KapuaLocator;

public interface KapuaClientPool<C extends KapuaClient> extends ObjectPool<C>
{
    public static KapuaClientPool<KapuaClient> kapuaClientPoolInstance = getInstance();

    @SuppressWarnings("unchecked")
    public static <C extends KapuaClient, I extends KapuaClientPool<C>> I getInstance()
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        KapuaClientPoolFactory<C> kapuaClientPoolFactory = locator.getFactory(KapuaClientPoolFactory.class);
        return kapuaClientPoolFactory.newInstance();
    }
}
