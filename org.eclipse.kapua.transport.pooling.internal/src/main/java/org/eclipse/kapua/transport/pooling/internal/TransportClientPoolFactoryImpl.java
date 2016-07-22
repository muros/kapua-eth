package org.eclipse.kapua.transport.pooling.internal;

import org.eclipse.kapua.transport.TransportClient;
import org.eclipse.kapua.transport.pooling.PooledTransportClientFactory;
import org.eclipse.kapua.transport.pooling.TransportClientPool;
import org.eclipse.kapua.transport.pooling.TransportClientPoolFactory;

@SuppressWarnings("rawtypes")
public class TransportClientPoolFactoryImpl<C extends TransportClient> implements TransportClientPoolFactory<C>
{

    @Override
    public TransportClientPool<C> newClientPool(PooledTransportClientFactory<C> pooledKapuaClientFactory)
    {
        return new TransportClientPoolImpl<>(pooledKapuaClientFactory);
    }

}
