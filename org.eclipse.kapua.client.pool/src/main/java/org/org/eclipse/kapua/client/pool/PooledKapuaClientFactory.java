package org.org.eclipse.kapua.client.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.eclipse.kapua.client.KapuaClient;
import org.eclipse.kapua.model.KapuaObjectFactory;

@SuppressWarnings("rawtypes")
public interface PooledKapuaClientFactory<C extends KapuaClient> extends PooledObjectFactory<C>, KapuaObjectFactory
{

}
