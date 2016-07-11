package org.eclipse.kapua.client.mqtt;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.KapuaClientPool;
import org.eclipse.kapua.client.message.mqtt.MqttDestination;
import org.eclipse.kapua.client.message.mqtt.MqttPayload;
import org.eclipse.kapua.client.mqtt.setting.MqttClientSetting;
import org.eclipse.kapua.client.mqtt.setting.MqttClientSettingKeys;

public class MqttClientPool extends GenericObjectPool<MqttClient> implements KapuaClientPool<MqttDestination, MqttPayload, MqttClientCallback, MqttClient>
{
    // private static MqttClientPool mqttClientPoolInstance;

    // static {
    // mqttClientPoolInstance = new MqttClientPool(new MqttClientPoolFactory());
    // }

    public MqttClientPool(MqttClientPoolFactory factory)
    {
        super(factory);

        MqttClientSetting config = MqttClientSetting.getInstance();
        GenericObjectPoolConfig clientPoolConfig = new GenericObjectPoolConfig();
        clientPoolConfig.setMinIdle(config.getInt(MqttClientSettingKeys.CLIENT_POOL_SIZE_MINIDLE));
        clientPoolConfig.setMaxIdle(config.getInt(MqttClientSettingKeys.CLIENT_POOL_SIZE_MAXIDLE));
        clientPoolConfig.setMaxTotal(config.getInt(MqttClientSettingKeys.CLIENT_POOL_SIZE_MAXTOTAL));

        clientPoolConfig.setTestOnReturn(config.getBoolean(MqttClientSettingKeys.CLIENT_POOL_ON_RETURN_TEST));
        clientPoolConfig.setTestOnBorrow(config.getBoolean(MqttClientSettingKeys.CLIENT_POOL_ON_BORROW_TEST));

        clientPoolConfig.setTestWhileIdle(config.getBoolean(MqttClientSettingKeys.CLIENT_POOL_WHEN_IDLE_TEST));
        clientPoolConfig.setBlockWhenExhausted(config.getBoolean(MqttClientSettingKeys.CLIENT_POOL_WHEN_EXAUSTED_BLOCK));

        clientPoolConfig.setTimeBetweenEvictionRunsMillis(config.getLong(MqttClientSettingKeys.CLIENT_POOL_EVICTION_INTERVAL));

        setConfig(clientPoolConfig);
    }

    // public static MqttClientPool getInstance()
    // {
    // return kapuaClientPoolInstance;
    // }

    @Override
    public void returnObject(MqttClient mqttClient)
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
