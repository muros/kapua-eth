package org.org.eclipse.kapua.client.pool.internal;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.KapuaClient;
import org.org.eclipse.kapua.client.pool.KapuaClientPool;
import org.org.eclipse.kapua.client.pool.PooledKapuaClientFactory;
import org.org.eclipse.kapua.client.pool.setting.internal.KapuaClientPoolSetting;
import org.org.eclipse.kapua.client.pool.setting.internal.KapuaClientPoolSettingKeys;

@SuppressWarnings("rawtypes")
public class KapuaClientPoolImpl<C extends KapuaClient> extends GenericObjectPool<C> implements KapuaClientPool<C>
{
    // private static MqttClientPool mqttClientPoolInstance;

    // static {
    // mqttClientPoolInstance = new MqttClientPool(new MqttClientPoolFactory());
    // }

    public KapuaClientPoolImpl(PooledKapuaClientFactory<C> factory)
    {
        super(factory);

        KapuaClientPoolSetting config = KapuaClientPoolSetting.getInstance();
        GenericObjectPoolConfig clientPoolConfig = new GenericObjectPoolConfig();
        clientPoolConfig.setMinIdle(config.getInt(KapuaClientPoolSettingKeys.CLIENT_POOL_SIZE_MINIDLE));
        clientPoolConfig.setMaxIdle(config.getInt(KapuaClientPoolSettingKeys.CLIENT_POOL_SIZE_MAXIDLE));
        clientPoolConfig.setMaxTotal(config.getInt(KapuaClientPoolSettingKeys.CLIENT_POOL_SIZE_MAXTOTAL));

        clientPoolConfig.setTestOnReturn(config.getBoolean(KapuaClientPoolSettingKeys.CLIENT_POOL_ON_RETURN_TEST));
        clientPoolConfig.setTestOnBorrow(config.getBoolean(KapuaClientPoolSettingKeys.CLIENT_POOL_ON_BORROW_TEST));

        clientPoolConfig.setTestWhileIdle(config.getBoolean(KapuaClientPoolSettingKeys.CLIENT_POOL_WHEN_IDLE_TEST));
        clientPoolConfig.setBlockWhenExhausted(config.getBoolean(KapuaClientPoolSettingKeys.CLIENT_POOL_WHEN_EXAUSTED_BLOCK));

        clientPoolConfig.setTimeBetweenEvictionRunsMillis(config.getLong(KapuaClientPoolSettingKeys.CLIENT_POOL_EVICTION_INTERVAL));

        setConfig(clientPoolConfig);
    }

    // public static MqttClientPool getInstance()
    // {
    // return kapuaClientPoolInstance;
    // }

    @SuppressWarnings("unchecked")
    @Override
    public void returnObject(C kapuaClient)
    {
        //
        // Clean up callback
        try {
            kapuaClient.setCallback(null);
        }
        catch (KapuaException e) {
            try {
                kapuaClient.terminateClient();
            }
            catch (KapuaException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            kapuaClient.unsubscribeAll();
        }
        catch (Exception e) {
            try {
                kapuaClient.terminateClient();
            }
            catch (KapuaException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            // TODO: handle exception
        }

        //
        // Return object to pool
        super.returnObject(kapuaClient);
    }
}
