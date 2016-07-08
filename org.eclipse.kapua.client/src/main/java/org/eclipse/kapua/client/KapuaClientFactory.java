package org.eclipse.kapua.client;

import org.apache.commons.pool2.PooledObjectFactory;
import org.eclipse.kapua.client.message.KapuaDestination;
import org.eclipse.kapua.client.message.KapuaPayload;

public interface KapuaClientFactory<D extends KapuaDestination, P extends KapuaPayload, CB extends KapuaClientCallback, C extends KapuaClient<D, P, CB>> extends PooledObjectFactory<C>
{

}
