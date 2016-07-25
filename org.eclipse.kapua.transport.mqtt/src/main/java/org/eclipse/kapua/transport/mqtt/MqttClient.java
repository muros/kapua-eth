package org.eclipse.kapua.transport.mqtt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.transport.TransportClientConnectOptions;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttClient
{
    private org.eclipse.paho.client.mqttv3.MqttClient pahoMqttClient   = null;
    private List<MqttTopic>                           subscribedTopics = new ArrayList<>();

    //
    // Connection management
    //
    public void connectClient(TransportClientConnectOptions options)
        throws KapuaException
    {
        try {
            if (pahoMqttClient != null) {
                throw new MqttClientException(MqttClientErrorCodes.CLIENT_ALREADY_CONNECTED,
                                              null,
                                              (Object[]) null);

            }

            pahoMqttClient = new org.eclipse.paho.client.mqttv3.MqttClient(options.getEndpointURI().toString(),
                                                                           options.getClientId(),
                                                                           new MemoryPersistence());

            MqttConnectOptions pahoConnectOptions = new MqttConnectOptions();
            pahoConnectOptions.setUserName(options.getUsername());
            pahoConnectOptions.setPassword(options.getPassword());
            pahoConnectOptions.setCleanSession(true);
            // FIXME: Set other connect options!

            pahoMqttClient.connect(pahoConnectOptions);
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_CONNECT_ERROR,
                                          e,
                                          new Object[] { options.getEndpointURI().toString(),
                                                         options.getClientId(),
                                                         options.getUsername() });
        }
    }

    public void disconnectClient()
        throws KapuaException
    {
        try {
            unsubscribeAll();

            if (getPahoClient() != null) {
                getPahoClient().disconnect();
            }
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_DISCONNECT_ERROR,
                                          e,
                                          (Object[]) null);
        }

    }

    public void terminateClient()
        throws KapuaException
    {
        try {
            if (getPahoClient() != null) {
                if (getPahoClient().isConnected()) {
                    disconnectClient();
                }

                getPahoClient().close();
                pahoMqttClient = null;
            }
        }
        catch (MqttException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_TERMINATE_ERROR,
                                          e,
                                          (Object[]) null);
        }
    }

    public boolean isConnected()
    {
        try {
            return getPahoClient().isConnected();
        }
        catch (KapuaException e) {
            // FIXME: add log
            return false;
        }
    }

    //
    // Message management
    //

    public void publish(MqttMessage mqttMessage)
        throws KapuaException
    {
        MqttTopic mqttTopic = mqttMessage.getRequestTopic();
        MqttPayload mqttPayload = mqttMessage.getPayload();
        try {

            getPahoClient().publish(mqttTopic.getTopic(),
                                    mqttPayload.getBody(),
                                    0,
                                    false);
        }
        catch (MqttException | KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_SUBSCRIBE_ERROR,
                                          e,
                                          new Object[] { mqttTopic.toString() });
        }
    }

    public void subscribe(MqttTopic mqttTopic)
        throws KapuaException
    {
        try {
            getPahoClient().subscribe(mqttTopic.getTopic());
        }
        catch (MqttException | KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_SUBSCRIBE_ERROR,
                                          e,
                                          new Object[] { mqttTopic.toString() });
        }
    }

    public void unsubscribe(MqttTopic mqttTopic)
        throws KapuaException
    {
        try {
            getPahoClient().unsubscribe(mqttTopic.getTopic());
        }
        catch (MqttException | KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_UNSUBSCRIBE_ERROR,
                                          e,
                                          new Object[] { mqttTopic.toString() });
        }

        subscribedTopics.remove(mqttTopic);
    }

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

    public void setCallback(MqttClientCallback mqttClientCallback)
        throws KapuaException
    {
        try {
            getPahoClient().setCallback(mqttClientCallback);
        }
        catch (KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_CALLBACK_ERROR,
                                          e,
                                          (Object[]) null);
        }

    }

    public void clean()
        throws KapuaException
    {
        try {
            getPahoClient().setCallback(null);
            unsubscribeAll();
        }
        catch (KapuaException e) {
            terminateClient();
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_CLEAN_ERROR,
                                          e,
                                          (Object[]) null);
        }

    }

    //
    // Utilty
    //
    public String getClientId()
    {
        try {
            return getPahoClient().getClientId();
        }
        catch (KapuaException e) {
            return null;
        }
    }

    private synchronized org.eclipse.paho.client.mqttv3.MqttClient getPahoClient()
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
