package org.org.eclipse.kapua.client.pool.internal;

import org.eclipse.kapua.client.KapuaClient;
import org.org.eclipse.kapua.client.pool.KapuaClientPool;
import org.org.eclipse.kapua.client.pool.KapuaClientPoolFactory;
import org.org.eclipse.kapua.client.pool.PooledKapuaClientFactory;

@SuppressWarnings("rawtypes")
public class KapuaClientPoolFactoryImpl<C extends KapuaClient> implements KapuaClientPoolFactory<C>
{

    @Override
    public KapuaClientPool<C> newClientPool(PooledKapuaClientFactory<C> pooledKapuaClientFactory)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
