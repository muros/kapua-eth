package org.eclipse.kapua.client.mqtt;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.KapuaClient;
import org.eclipse.kapua.client.message.mqtt.MqttDestination;
import org.eclipse.kapua.client.message.mqtt.MqttPayload;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttClient extends org.eclipse.paho.client.mqttv3.MqttClient implements KapuaClient<MqttDestination, MqttPayload, MqttClientCallback>
{

    public MqttClient(String brokerURI, String clientId) throws MqttException
    {
        super(brokerURI, clientId, new MemoryPersistence());
    }

    @Override
    public void publish(MqttDestination kapuaDestination, MqttPayload kapuaPayload)
        throws KapuaException
    {
        try {
            super.publish(kapuaDestination.toClientDestination(),
                          kapuaPayload.toByteArray(),
                          0,
                          false);
        }
        catch (MqttPersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void subscribe(MqttDestination destination)
        throws KapuaException
    {
        try {
            super.subscribe(destination.toClientDestination());
        }
        catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void unsubscribe(MqttDestination desination)
        throws KapuaException
    {
        try {
            super.unsubscribe(desination.toClientDestination());
        }
        catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void setCallback(MqttClientCallback clientCallback)
        throws KapuaException
    {
        super.setCallback(clientCallback);
    }
}
