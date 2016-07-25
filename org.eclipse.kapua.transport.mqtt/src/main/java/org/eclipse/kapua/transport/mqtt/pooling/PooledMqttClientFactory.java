package org.eclipse.kapua.transport.mqtt.pooling;

import java.net.URI;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.SystemUtils;
import org.eclipse.kapua.transport.mqtt.MqttClient;
import org.eclipse.kapua.transport.mqtt.MqttClientConnectionOptions;
import org.eclipse.kapua.transport.mqtt.pooling.setting.MqttClientPoolSetting;
import org.eclipse.kapua.transport.mqtt.pooling.setting.MqttClientPoolSettingKeys;
import org.eclipse.kapua.transport.utils.ClientIdGenerator;

public class PooledMqttClientFactory extends BasePooledObjectFactory<MqttClient>
{
    @Override
    public MqttClient create()
        throws Exception
    {
        //
        // User pwd generation
        MqttClientPoolSetting mqttClientPoolSettings = MqttClientPoolSetting.getInstance();

        // FIXME: remove these credentials!
        String username = "edcguest";
        char[] password = "Welcome1".toCharArray();
        String clientId = ClientIdGenerator.next(mqttClientPoolSettings.getString(MqttClientPoolSettingKeys.CLIENT_POOL_CLIENT_ID_PREFIX));
        URI brokerURI = SystemUtils.getBrokerURI();

        //
        // Get new client and connection options
        MqttClientConnectionOptions connectionOptions = new MqttClientConnectionOptions();
        connectionOptions.setClientId(clientId);
        connectionOptions.setUsername(username);
        connectionOptions.setPassword(password);
        connectionOptions.setEndpointURI(brokerURI);

        //
        // Connect client
        MqttClient kapuaClient = new MqttClient();
        try {
            kapuaClient.connectClient(connectionOptions);
        }
        catch (KapuaException ke) {
            kapuaClient.terminateClient();
            throw ke;
        }

        return kapuaClient;
    }

    @Override
    public PooledObject<MqttClient> wrap(MqttClient mqttClient)
    {
        return new DefaultPooledObject<>(mqttClient);
    }

    @Override
    public boolean validateObject(PooledObject<MqttClient> pooledKapuaClient)
    {
        MqttClient mqttClient = pooledKapuaClient.getObject();
        return (mqttClient != null && mqttClient.isConnected());
    }

    @Override
    public void destroyObject(PooledObject<MqttClient> pooledKapuaClient)
        throws Exception
    {
        MqttClient mqttClient = pooledKapuaClient.getObject();
        if (mqttClient != null) {
            if (mqttClient.isConnected()) {
                mqttClient.disconnectClient();
            }
            mqttClient.terminateClient();
        }
        super.destroyObject(pooledKapuaClient);
    }

}
