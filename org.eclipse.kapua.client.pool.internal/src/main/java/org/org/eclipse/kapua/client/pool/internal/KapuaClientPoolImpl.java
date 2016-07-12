package org.org.eclipse.kapua.client.pool.internal;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.KapuaClient;
import org.eclipse.kapua.client.KapuaClientPool;
import org.eclipse.kapua.client.message.mqtt.MqttDestination;
import org.eclipse.kapua.client.message.mqtt.MqttPayload;
import org.eclipse.kapua.client.mqtt.setting.MqttClientSetting;
import org.eclipse.kapua.client.mqtt.setting.MqttClientSettingKeys;
import org.org.eclipse.kapua.client.pool.KapuaClientPoolFactory;
import org.org.eclipse.kapua.client.pool.setting.internal.KapuaClientPoolSetting;
import org.org.eclipse.kapua.client.pool.setting.internal.KapuaClientPoolSettingKeys;

public class KapuaClientPoolImpl extends GenericObjectPool<K extends KapuaClient> implements KapuaClientPool<KapuaDestination, KapuaPayload, MqttClientCallback, MqttClient>
{
    // private static MqttClientPool mqttClientPoolInstance;

    // static {
    // mqttClientPoolInstance = new MqttClientPool(new MqttClientPoolFactory());
    // }

    public KapuaClientPoolImpl(KapuaClientPoolFactory<KapuaDestination, KapuaPayload, KapuaClientCallback, KapuaClient<D,P,CB>> factory)
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

    @Override
    public void returnObject(KapuaClient mqttClient)
    {
        //
        // Clean up callback
        try {
            mqttClient.setCallback((MqttClientCallback) null);
        }
        catch (KapuaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //
        // Clean up subscription
        // FIXME: clean up any subscription

        //
        // Return object to pool
        super.returnObject(mqttClient);
    }
}
