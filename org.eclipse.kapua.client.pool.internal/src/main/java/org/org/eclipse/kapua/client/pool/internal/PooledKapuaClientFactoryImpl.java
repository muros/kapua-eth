package org.org.eclipse.kapua.client.pool.internal;

import java.net.URI;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.KapuaClient;
import org.eclipse.kapua.client.KapuaClientConnectOptions;
import org.eclipse.kapua.client.KapuaClientFactory;
import org.eclipse.kapua.client.utils.KapuaClientIdGenerator;
import org.eclipse.kapua.commons.util.KapuaEnvironmentUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.org.eclipse.kapua.client.pool.PooledKapuaClientFactory;
import org.org.eclipse.kapua.client.pool.setting.internal.KapuaClientPoolSetting;
import org.org.eclipse.kapua.client.pool.setting.internal.KapuaClientPoolSettingKeys;

@SuppressWarnings("rawtypes")
public class PooledKapuaClientFactoryImpl<C extends KapuaClient, CO extends KapuaClientConnectOptions> extends BasePooledObjectFactory<C> implements PooledKapuaClientFactory<C>
{
    @SuppressWarnings("unchecked")
    @Override
    public C create()
        throws Exception
    {
        //
        // User pwd generation
        KapuaClientPoolSetting clientPoolSettings = KapuaClientPoolSetting.getInstance();

        // FIXME: remove these credentials!
        String username = "kapua-sys";
        char[] password = "We!come12345".toCharArray();
        String clientId = KapuaClientIdGenerator.next(clientPoolSettings.getString(KapuaClientPoolSettingKeys.CLIENT_POOL_CLIENT_ID_PREFIX));
        URI brokerURI = new URI(KapuaEnvironmentUtils.getBrokerURI());

        //
        // Get client factory
        KapuaLocator locator = KapuaLocator.getInstance();
        KapuaClientFactory clientFactory = locator.getFactory(KapuaClientFactory.class);

        //
        // Get new client and connection options
        KapuaClientConnectOptions connectOptions = clientFactory.newConnectOptions();
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
