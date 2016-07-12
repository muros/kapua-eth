package org.eclipse.kapua.client.mqtt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.KapuaClient;
import org.eclipse.kapua.client.message.mqtt.MqttPayload;
import org.eclipse.kapua.client.message.mqtt.MqttTopic;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttClient extends org.eclipse.paho.client.mqttv3.MqttClient implements KapuaClient<MqttTopic, MqttPayload, MqttClientCallback>
{
    List<MqttTopic> subscribedTopics = new ArrayList<MqttTopic>();

    public MqttClient(String brokerURI, String clientId) throws MqttException
    {
        super(brokerURI, clientId, new MemoryPersistence());
    }

    @Override
    public void publish(MqttTopic kapuaDestination, MqttPayload kapuaPayload)
        throws KapuaException
    {
        try {
            super.publish(kapuaDestination.toClientDestination(),
                          kapuaPayload.toByteArray(),
                          0,
                          false);
        }
        catch (MqttPersistenceException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_PUBLISH_ERROR, e, (Object[]) null);
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_PUBLISH_ERROR, e, (Object[]) null);
        }

    }

    @Override
    public void subscribe(MqttTopic destination)
        throws KapuaException
    {
        try {
            super.subscribe(destination.toClientDestination());
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_SUBSCRIBE_ERROR, e, (Object[]) null);
        }

        subscribedTopics.add(destination);
    }

    @Override
    public void unsubscribe(MqttTopic desination)
        throws KapuaException
    {
        try {
            super.unsubscribe(desination.toClientDestination());
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_UNSUBSCRIBE_ERROR, e, (Object[]) null);
        }

        subscribedTopics.remove(desination);
    }

    @Override
    public void unsubscribeAll()
        throws KapuaException
    {
        Iterator<MqttTopic> subscribptionIterator = subscribedTopics.iterator();

        while (subscribptionIterator.hasNext()) {
            MqttTopic topic = subscribptionIterator.next();
            unsubscribe(topic);
        }
    }

    @Override
    public void setCallback(MqttClientCallback clientCallback)
        throws KapuaException
    {
        super.setCallback(clientCallback);
    }
}
