package org.eclipse.kapua.client.mqtt;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.eclipse.kapua.client.KapuaClientFactory;
import org.eclipse.kapua.client.message.mqtt.MqttDestination;
import org.eclipse.kapua.client.message.mqtt.MqttPayload;
import org.eclipse.kapua.client.mqtt.setting.MqttClientSetting;
import org.eclipse.kapua.client.mqtt.setting.MqttClientSettingKeys;
import org.eclipse.kapua.client.utils.KapuaClientIdGenerator;
import org.eclipse.kapua.commons.util.KapuaEnvironmentUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class MqttClientPoolFactory extends BasePooledObjectFactory<MqttClient> implements KapuaClientFactory<MqttDestination, MqttPayload, MqttClientCallback, MqttClient>
{

    @Override
    public MqttClient create()
        throws Exception
    {
        MqttClientSetting deviceCallConfig = MqttClientSetting.getInstance();

        String username = "kapua-sys";
        char[] password = "We!come12345".toCharArray();
        String clientId = KapuaClientIdGenerator.next(deviceCallConfig.getString(MqttClientSettingKeys.CLIENT_POOL_CLIENT_PREFIX));

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
