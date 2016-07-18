package org.org.eclipse.kapua.transport.pooling;

import org.apache.commons.pool2.ObjectPool;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.transport.TransportClient;
import org.org.eclipse.kapua.transport.pooling.TransportClientPool;
import org.org.eclipse.kapua.transport.pooling.TransportClientPoolFactory;
import org.org.eclipse.kapua.transport.pooling.PooledTransportClientFactory;

@SuppressWarnings("rawtypes")
public interface TransportClientPool<C extends TransportClient> extends ObjectPool<C>
{
    public static TransportClientPool clientPoolInstance = getInstance();

    @SuppressWarnings("unchecked")
    public static <C extends TransportClient> TransportClientPool getInstance()
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        TransportClientPoolFactory kapuaClientPoolFactory = locator.getFactory(TransportClientPoolFactory.class);
        PooledTransportClientFactory pooledKapuaClientFactory = locator.getFactory(PooledTransportClientFactory.class);

        return kapuaClientPoolFactory.newClientPool(pooledKapuaClientFactory);
    }
}
