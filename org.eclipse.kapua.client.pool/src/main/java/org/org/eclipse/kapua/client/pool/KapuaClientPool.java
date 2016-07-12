package org.org.eclipse.kapua.client.pool;

import org.apache.commons.pool2.ObjectPool;
import org.eclipse.kapua.client.KapuaClient;
import org.eclipse.kapua.locator.KapuaLocator;

@SuppressWarnings("rawtypes")
public interface KapuaClientPool<C extends KapuaClient> extends ObjectPool<C>
{
    public static KapuaClientPool clientPoolInstance = getInstance();

    @SuppressWarnings("unchecked")
    public static <C extends KapuaClient> KapuaClientPool getInstance()
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        KapuaClientPoolFactory kapuaClientPoolFactory = locator.getFactory(KapuaClientPoolFactory.class);
        PooledKapuaClientFactory pooledKapuaClientFactory = locator.getFactory(PooledKapuaClientFactory.class);

        return kapuaClientPoolFactory.newClientPool(pooledKapuaClientFactory);
    }
}
