package org.org.eclipse.kapua.transport.pooling;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.transport.TransportClient;
import org.org.eclipse.kapua.transport.pooling.TransportClientPool;
import org.org.eclipse.kapua.transport.pooling.PooledTransportClientFactory;

@SuppressWarnings("rawtypes")
public interface TransportClientPoolFactory<C extends TransportClient> extends KapuaObjectFactory
{
    public TransportClientPool<C> newClientPool(PooledTransportClientFactory<C> pooledKapuaClientFactory);
}
