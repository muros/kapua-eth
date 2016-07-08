package org.eclipse.kapua.service.device.client.mqtt;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.eclipse.kapua.commons.util.KapuaEnvironmentUtils;
import org.eclipse.kapua.service.device.client.KapuaClientFactory;
import org.eclipse.kapua.service.device.client.mqtt.config.MqttClientConfig;
import org.eclipse.kapua.service.device.client.mqtt.config.MqttClientConfigKeys;
import org.eclipse.kapua.service.device.client.mqtt.utils.MqttClientIdGenerator;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class MqttClientPoolFactory extends BasePooledObjectFactory<MqttClient> implements KapuaClientFactory<MqttClient>
{

    @Override
    public MqttClient create()
        throws Exception
    {
        MqttClientConfig deviceCallConfig = MqttClientConfig.getInstance();

        String username = "kapua-sys";
        char[] password = "We!come12345".toCharArray();
        String clientId = MqttClientIdGenerator.next(deviceCallConfig.getString(MqttClientConfigKeys.CLIENT_POOL_CLIENT_PREFIX));

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setUserName(username);
        connectOptions.setPassword(password);
        connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1); // FIXME: get version from config file

        MqttClient mqttClient = new MqttClient(KapuaEnvironmentUtils.getBrokerURI(), clientId);
        try {
            mqttClient.connect(connectOptions);
        }
        catch (MqttSecurityException mse) {
            mqttClient.close();
            throw mse;
        }
        catch (MqttException me) {
            mqttClient.close();
            throw me;
        }

        return mqttClient;
    }

    @Override
    public PooledObject<MqttClient> wrap(MqttClient mqttClient)
    {
        return new DefaultPooledObject<MqttClient>(mqttClient);
    }

    @Override
    public boolean validateObject(PooledObject<MqttClient> mqttClientPool)
    {
        MqttClient mqttClient = mqttClientPool.getObject();
        return (mqttClient != null && mqttClient.isConnected());
    }

    @Override
    public void destroyObject(PooledObject<MqttClient> mqttClientPool)
        throws Exception
    {
        MqttClient mqttClient = mqttClientPool.getObject();
        if (mqttClient != null) {

            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }

            mqttClient.close();
        }
        super.destroyObject(mqttClientPool);
    }

}
