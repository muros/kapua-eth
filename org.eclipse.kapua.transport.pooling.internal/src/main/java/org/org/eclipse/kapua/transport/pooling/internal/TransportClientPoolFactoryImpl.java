package org.org.eclipse.kapua.transport.pooling.internal;

import org.eclipse.kapua.transport.TransportClient;
import org.org.eclipse.kapua.transport.pooling.TransportClientPool;
import org.org.eclipse.kapua.transport.pooling.TransportClientPoolFactory;
import org.org.eclipse.kapua.transport.pooling.PooledTransportClientFactory;

@SuppressWarnings("rawtypes")
public class TransportClientPoolFactoryImpl<C extends TransportClient> implements TransportClientPoolFactory<C>
{

    @Override
    public TransportClientPool<C> newClientPool(PooledTransportClientFactory<C> pooledKapuaClientFactory)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
