package org.org.eclipse.kapua.client.pool;

import org.eclipse.kapua.client.KapuaClient;
import org.eclipse.kapua.model.KapuaObjectFactory;

@SuppressWarnings("rawtypes")
public interface KapuaClientPoolFactory<C extends KapuaClient> extends KapuaObjectFactory
{
    public KapuaClientPool<C> newClientPool(PooledKapuaClientFactory<C> pooledKapuaClientFactory);
}
