package org.eclipse.kapua.transport.pooling.internal;

import java.net.URI;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.SystemUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.transport.TransportClient;
import org.eclipse.kapua.transport.TransportClientConnectOptions;
import org.eclipse.kapua.transport.TransportClientFactory;
import org.eclipse.kapua.transport.pooling.PooledTransportClientFactory;
import org.eclipse.kapua.transport.pooling.setting.internal.TransportClientPoolSetting;
import org.eclipse.kapua.transport.pooling.setting.internal.TransportClientPoolSettingKeys;
import org.eclipse.kapua.transport.utils.ClientIdGenerator;

@SuppressWarnings("rawtypes")
public class PooledTransportClientFactoryImpl<C extends TransportClient, CO extends TransportClientConnectOptions> extends BasePooledObjectFactory<C> implements PooledTransportClientFactory<C>
{
    @SuppressWarnings("unchecked")
    @Override
    public C create()
        throws Exception
    {
        //
        // User pwd generation
        TransportClientPoolSetting clientPoolSettings = TransportClientPoolSetting.getInstance();

        // FIXME: remove these credentials!
        String username = "edcguest";
        char[] password = "Welcome1".toCharArray();
        String clientId = ClientIdGenerator.next(clientPoolSettings.getString(TransportClientPoolSettingKeys.CLIENT_POOL_CLIENT_ID_PREFIX));
        URI brokerURI = SystemUtils.getBrokerURI();

        //
        // Get client factory
        KapuaLocator locator = KapuaLocator.getInstance();
        TransportClientFactory clientFactory = locator.getFactory(TransportClientFactory.class);

        //
        // Get new client and connection options
        TransportClientConnectOptions connectOptions = clientFactory.newConnectOptions();
        connectOptions.setClientId(clientId);
        connectOptions.setUsername(username);
        connectOptions.setPassword(password);
        connectOptions.setEndpointURI(brokerURI);

        //
        // Connect client
        C kapuaClient = (C) clientFactory.newInstance();
        try {
            kapuaClient.connectClient(connectOptions);
        }
        catch (KapuaException ke) {
            kapuaClient.terminateClient();
            throw ke;
        }

        return kapuaClient;
    }

    @Override
    public PooledObject<C> wrap(C kapuaClient)
    {
        return new DefaultPooledObject<C>(kapuaClient);
    }

    @Override
    public boolean validateObject(PooledObject<C> pooledKapuaClient)
    {
        C kapuaClient = pooledKapuaClient.getObject();
        return (kapuaClient != null && kapuaClient.isConnected());
    }

    @Override
    public void destroyObject(PooledObject<C> pooledKapuaClient)
        throws Exception
    {
        C kapuaClient = pooledKapuaClient.getObject();
        if (kapuaClient != null) {
            if (kapuaClient.isConnected()) {
                kapuaClient.disconnectClient();
            }
            kapuaClient.terminateClient();
        }
        super.destroyObject(pooledKapuaClient);
    }

}
