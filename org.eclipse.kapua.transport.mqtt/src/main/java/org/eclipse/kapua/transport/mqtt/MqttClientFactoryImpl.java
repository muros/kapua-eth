package org.eclipse.kapua.transport.mqtt;

import org.eclipse.kapua.transport.KapuaClientFactory;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

public class MqttClientFactoryImpl implements KapuaClientFactory<MqttTopic, MqttPayload, MqttMessage, MqttMessage, MqttClient, MqttClientConnectionOptions>
{
    @Override
    public MqttClient newInstance()
    {
        return new MqttClient();
    }

    @Override
    public MqttClientConnectionOptions newConnectOptions()
    {
        return new MqttClientConnectionOptions();
    }

}
