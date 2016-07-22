package org.eclipse.kapua.transport.pooling.internal;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.transport.TransportClient;
import org.eclipse.kapua.transport.pooling.PooledTransportClientFactory;
import org.eclipse.kapua.transport.pooling.TransportClientPool;
import org.eclipse.kapua.transport.pooling.setting.internal.TransportClientPoolSetting;
import org.eclipse.kapua.transport.pooling.setting.internal.TransportClientPoolSettingKeys;

@SuppressWarnings("rawtypes")
public class TransportClientPoolImpl<C extends TransportClient> extends GenericObjectPool<C> implements TransportClientPool<C>
{
    // private static MqttClientPool mqttClientPoolInstance;

    // static {
    // mqttClientPoolInstance = new MqttClientPool(new MqttClientPoolFactory());
    // }

    public TransportClientPoolImpl(PooledTransportClientFactory<C> factory)
    {
        super(factory);

        TransportClientPoolSetting config = TransportClientPoolSetting.getInstance();
        GenericObjectPoolConfig clientPoolConfig = new GenericObjectPoolConfig();
        clientPoolConfig.setMinIdle(config.getInt(TransportClientPoolSettingKeys.CLIENT_POOL_SIZE_MINIDLE));
        clientPoolConfig.setMaxIdle(config.getInt(TransportClientPoolSettingKeys.CLIENT_POOL_SIZE_MAXIDLE));
        clientPoolConfig.setMaxTotal(config.getInt(TransportClientPoolSettingKeys.CLIENT_POOL_SIZE_MAXTOTAL));

        clientPoolConfig.setTestOnReturn(config.getBoolean(TransportClientPoolSettingKeys.CLIENT_POOL_ON_RETURN_TEST));
        clientPoolConfig.setTestOnBorrow(config.getBoolean(TransportClientPoolSettingKeys.CLIENT_POOL_ON_BORROW_TEST));

        clientPoolConfig.setTestWhileIdle(config.getBoolean(TransportClientPoolSettingKeys.CLIENT_POOL_WHEN_IDLE_TEST));
        clientPoolConfig.setBlockWhenExhausted(config.getBoolean(TransportClientPoolSettingKeys.CLIENT_POOL_WHEN_EXAUSTED_BLOCK));

        clientPoolConfig.setTimeBetweenEvictionRunsMillis(config.getLong(TransportClientPoolSettingKeys.CLIENT_POOL_EVICTION_INTERVAL));

        setConfig(clientPoolConfig);
    }

    @Override
    public void returnObject(C kapuaClient)
    {
        //
        // Clean up callback
        try {
            kapuaClient.clean();
        }
        catch (KapuaException e) {
            try {
                kapuaClient.terminateClient();
            }
            catch (KapuaException e1) {
                // FIXME: Manage exception
            }
            // FIXME: Manage exception
            e.printStackTrace();
        }

        //
        // Return object to pool
        super.returnObject(kapuaClient);
    }
}
