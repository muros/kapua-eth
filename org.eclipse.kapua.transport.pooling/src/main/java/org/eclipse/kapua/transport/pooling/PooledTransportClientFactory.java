package org.eclipse.kapua.transport.pooling;

import org.apache.commons.pool2.PooledObjectFactory;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.transport.TransportClient;

@SuppressWarnings("rawtypes")
public interface PooledTransportClientFactory<C extends TransportClient> extends PooledObjectFactory<C>, KapuaObjectFactory
{

}
