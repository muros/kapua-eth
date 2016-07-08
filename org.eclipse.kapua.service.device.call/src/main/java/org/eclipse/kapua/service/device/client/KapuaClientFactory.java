package org.eclipse.kapua.service.device.client;

import org.apache.commons.pool2.PooledObjectFactory;

public interface KapuaClientFactory<C extends KapuaClient> extends PooledObjectFactory<C>
{

}
