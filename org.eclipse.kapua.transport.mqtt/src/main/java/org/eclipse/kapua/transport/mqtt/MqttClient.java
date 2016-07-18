package org.eclipse.kapua.transport.mqtt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.transport.KapuaClientConnectOptions;
import org.eclipse.kapua.transport.TransportClient;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttClient implements TransportClient<MqttTopic, MqttPayload, MqttMessage, MqttClientCallback>
{
    org.eclipse.paho.client.mqttv3.MqttClient pahoMqttClient   = null;

    List<MqttTopic>                           subscribedTopics = new ArrayList<MqttTopic>();

    //
    // Connection management
    //
    @Override
    public void connectClient(KapuaClientConnectOptions options)
        throws KapuaException
    {
        try {
            if (getClient() != null) {
                throw new MqttClientException(MqttClientErrorCodes.CLIENT_ALREADY_CONNECTED,
                                              null,
                                              (Object[]) null);

            }

            pahoMqttClient = new org.eclipse.paho.client.mqttv3.MqttClient(options.getEndpointURI().toString(),
                                                                           options.getClientId(),
                                                                           new MemoryPersistence());

            MqttConnectOptions pahoConnectOptions = new MqttConnectOptions();
            options.setUsername(options.getUsername());
            options.setPassword(options.getPassword());
            // FIXME: hot to add MQTT protocol version??
            pahoMqttClient.connect(pahoConnectOptions);
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_CONNECT_ERROR,
                                          e,
                                          new Object[] { options.getEndpointURI().toString(),
                                                         options.getClientId() });
        }
    }

    @Override
    public void disconnectClient()
        throws KapuaException
    {
        try {
            unsubscribeAll();

            if (getClient() != null) {
                getClient().disconnect();
            }
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_DISCONNECT_ERROR,
                                          e,
                                          (Object[]) null);
        }

    }

    @Override
    public void terminateClient()
        throws KapuaException
    {
        try {
            if (getClient() != null) {
                if (getClient().isConnected()) {
                    disconnectClient();
                }

                getClient().close();
                pahoMqttClient = null;
            }
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_TERMINATE_ERROR,
                                          e,
                                          (Object[]) null);
        }
    }

    @Override
    public boolean isConnected()
    {
        try {
            return getClient().isConnected();
        }
        catch (KapuaException e) {
            return false;
        }
    }

    //
    // Message management
    //
    @Override
    public void publish(MqttMessage mqttMessage)
        throws KapuaException
    {
        MqttTopic mqttTopic = mqttMessage.getTopic();
        MqttPayload mqttPayload = mqttMessage.getPayload();
        try {
            getClient().publish(mqttTopic.getTopic(),
                                mqttPayload.getBody(),
                                0,
                                false);
        }
        catch (MqttPersistenceException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_PUBLISH_ERROR,
                                          e,
                                          new Object[] { mqttTopic.getTopic(),
                                                         mqttPayload.getBody() });
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_PUBLISH_ERROR,
                                          e,
                                          new Object[] { mqttTopic.getTopic(),
                                                         mqttPayload.getBody() });
        }
        catch (KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_PUBLISH_ERROR,
                                          e,
                                          new Object[] { mqttTopic.getTopic(),
                                                         mqttPayload.getBody() });
        }
    }

    @Override
    public void subscribe(MqttTopic mqttTopic)
        throws KapuaException
    {
        try {
            getClient().subscribe(mqttTopic.getTopic());
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_SUBSCRIBE_ERROR,
                                          e,
                                          new Object[] { mqttTopic.toString() });
        }
        catch (KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_SUBSCRIBE_ERROR,
                                          e,
                                          new Object[] { mqttTopic.getTopic() });
        }
    }

    @Override
    public void unsubscribe(MqttTopic mqttTopic)
        throws KapuaException
    {
        try {
            getClient().unsubscribe(mqttTopic.getTopic());
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_UNSUBSCRIBE_ERROR,
                                          e,
                                          new Object[] { mqttTopic.toString() });
        }
        catch (KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_UNSUBSCRIBE_ERROR,
                                          e,
                                          new Object[] { mqttTopic.getTopic() });
        }

        subscribedTopics.remove(mqttTopic);
    }

    @Override
    public void unsubscribeAll()
        throws KapuaException
    {
        Iterator<MqttTopic> subscribptionIterator = subscribedTopics.iterator();

        while (subscribptionIterator.hasNext()) {
            MqttTopic mqttTopic = subscribptionIterator.next();
            unsubscribe(mqttTopic);
        }

        subscribedTopics.clear();
    }

    @Override
    public void newCallback(List<Message> messages, Class<Message> clazz)
        throws KapuaException
    {
        MqttClientCallback transportClientCalback;
        try {
            transportClientCalback = new MqttClientCallback(messages, clazz);

            getClient().setCallback(transportClientCalback);
        }
        catch (KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_CALLBACK_ERROR,
                                          e,
                                          (Object[]) null);
        }
    }

    @Override
    public void clearCallback()
        throws KapuaException
    {
        try {
            getClient().setCallback(null);
        }
        catch (KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_CALLBACK_ERROR,
                                          e,
                                          (Object[]) null);
        }

    }

    //
    // Utilty
    //
    @Override
    public String getClientId()
    {
        try {
            return getClient().getClientId();
        }
        catch (KapuaException e) {
            return null;
        }
    }

    @Override
    public Class<MqttMessage> getMessageClass()
    {
        return MqttMessage.class;
    }

    private org.eclipse.paho.client.mqttv3.MqttClient getClient()
        throws KapuaException
    {
        if (pahoMqttClient == null) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_NOT_CONNECTED,
                                          null,
                                          (Object[]) null);
        }

        return pahoMqttClient;
    }

}
