package org.org.eclipse.kapua.client.pool;

import org.eclipse.kapua.client.KapuaClient;
import org.eclipse.kapua.client.KapuaClientCallback;
import org.eclipse.kapua.client.message.KapuaDestination;
import org.eclipse.kapua.client.message.KapuaPayload;
import org.eclipse.kapua.model.KapuaEntityFactory;

public interface KapuaClientPoolFactory<D extends KapuaDestination, P extends KapuaPayload, CB extends KapuaClientCallback, C extends KapuaClient<D, P, CB>> extends KapuaEntityFactory
{
    public KapuaClientPool<D, P, CB, C> newInstance();
}
