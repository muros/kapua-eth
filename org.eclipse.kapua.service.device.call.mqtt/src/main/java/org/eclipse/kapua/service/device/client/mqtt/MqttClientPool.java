package org.eclipse.kapua.service.device.client.mqtt;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.client.KapuaClientCallback;
import org.eclipse.kapua.service.device.client.KapuaClientPool;
import org.eclipse.kapua.service.device.client.mqtt.config.MqttClientConfig;
import org.eclipse.kapua.service.device.client.mqtt.config.MqttClientConfigKeys;

public class MqttClientPool extends GenericObjectPool<MqttClient> implements KapuaClientPool<MqttClient>
{
    // private static MqttClientPool mqttClientPoolInstance;

    // static {
    // mqttClientPoolInstance = new MqttClientPool(new MqttClientPoolFactory());
    // }

    public MqttClientPool(MqttClientPoolFactory factory)
    {
        super(factory);

        MqttClientConfig config = MqttClientConfig.getInstance();
        GenericObjectPoolConfig clientPoolConfig = new GenericObjectPoolConfig();
        clientPoolConfig.setMinIdle(config.getInt(MqttClientConfigKeys.CLIENT_POOL_SIZE_MINIDLE));
        clientPoolConfig.setMaxIdle(config.getInt(MqttClientConfigKeys.CLIENT_POOL_SIZE_MAXIDLE));
        clientPoolConfig.setMaxTotal(config.getInt(MqttClientConfigKeys.CLIENT_POOL_SIZE_MAXTOTAL));

        clientPoolConfig.setTestOnReturn(config.getBoolean(MqttClientConfigKeys.CLIENT_POOL_ON_RETURN_TEST));
        clientPoolConfig.setTestOnBorrow(config.getBoolean(MqttClientConfigKeys.CLIENT_POOL_ON_BORROW_TEST));

        clientPoolConfig.setTestWhileIdle(config.getBoolean(MqttClientConfigKeys.CLIENT_POOL_WHEN_IDLE_TEST));
        clientPoolConfig.setBlockWhenExhausted(config.getBoolean(MqttClientConfigKeys.CLIENT_POOL_WHEN_EXAUSTED_BLOCK));

        clientPoolConfig.setTimeBetweenEvictionRunsMillis(config.getLong(MqttClientConfigKeys.CLIENT_POOL_EVICTION_INTERVAL));

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
            mqttClient.setCallback((KapuaClientCallback) null);
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
