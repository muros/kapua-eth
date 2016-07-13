package org.eclipse.kapua.client;

import org.eclipse.kapua.message.KapuaDestination;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.model.KapuaObjectFactory;

public interface KapuaClientFactory<D extends KapuaDestination, P extends KapuaPayload, CB extends KapuaClientCallback<D, P, KapuaMessage<D, P>>, C extends KapuaClient<D, P, CB>, CO extends KapuaClientConnectOptions>
                                   extends KapuaObjectFactory
{
    public C newInstance();

    public CO newConnectOptions();

}
