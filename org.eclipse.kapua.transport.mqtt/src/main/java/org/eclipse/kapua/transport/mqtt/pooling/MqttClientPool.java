/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.transport.mqtt.pooling;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.transport.mqtt.MqttClient;
import org.eclipse.kapua.transport.mqtt.pooling.setting.MqttClientPoolSetting;
import org.eclipse.kapua.transport.mqtt.pooling.setting.MqttClientPoolSettingKeys;

public class MqttClientPool extends GenericObjectPool<MqttClient>
{
    private static MqttClientPool mqttClientPoolInstance;

    static {
        mqttClientPoolInstance = new MqttClientPool(new PooledMqttClientFactory());
    }

    public MqttClientPool(PooledMqttClientFactory factory)
    {
        super(factory);

        MqttClientPoolSetting config = MqttClientPoolSetting.getInstance();
        GenericObjectPoolConfig clientPoolConfig = new GenericObjectPoolConfig();
        clientPoolConfig.setMinIdle(config.getInt(MqttClientPoolSettingKeys.CLIENT_POOL_SIZE_MINIDLE));
        clientPoolConfig.setMaxIdle(config.getInt(MqttClientPoolSettingKeys.CLIENT_POOL_SIZE_MAXIDLE));
        clientPoolConfig.setMaxTotal(config.getInt(MqttClientPoolSettingKeys.CLIENT_POOL_SIZE_MAXTOTAL));

        clientPoolConfig.setMaxWaitMillis(config.getInt(MqttClientPoolSettingKeys.CLIENT_POOL_BORROW_WAIT_MAX));

        clientPoolConfig.setTestOnReturn(config.getBoolean(MqttClientPoolSettingKeys.CLIENT_POOL_ON_RETURN_TEST));
        clientPoolConfig.setTestOnBorrow(config.getBoolean(MqttClientPoolSettingKeys.CLIENT_POOL_ON_BORROW_TEST));

        clientPoolConfig.setTestWhileIdle(config.getBoolean(MqttClientPoolSettingKeys.CLIENT_POOL_WHEN_IDLE_TEST));
        clientPoolConfig.setBlockWhenExhausted(config.getBoolean(MqttClientPoolSettingKeys.CLIENT_POOL_WHEN_EXAUSTED_BLOCK));

        clientPoolConfig.setTimeBetweenEvictionRunsMillis(config.getLong(MqttClientPoolSettingKeys.CLIENT_POOL_EVICTION_INTERVAL));

        setConfig(clientPoolConfig);
    }

    public static MqttClientPool getInstance()
    {
        return mqttClientPoolInstance;
    }

    @Override
    public void returnObject(MqttClient kapuaClient)
    {
        //
        // Clean up callback
        try {
            kapuaClient.clean();

            //
            // Return object to pool
            super.returnObject(kapuaClient);
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
    }
}
