package org.eclipse.kapua.transport.mqtt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.transport.TransportClientConnectOptions;
import org.eclipse.kapua.transport.TransportClient;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttClient implements TransportClient<MqttTopic, MqttPayload, MqttMessage, MqttMessage>
{
    private org.eclipse.paho.client.mqttv3.MqttClient pahoMqttClient   = null;
    private List<MqttTopic>                           subscribedTopics = new ArrayList<>();
    private MqttCallback                              mqttCallback;

    //
    // Connection management
    //
    @Override
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

    @Override
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

    @Override
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

    @Override
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
    @Override
    public void sendAndForget(MqttMessage mqttMessage)
        throws KapuaException
    {
        send(mqttMessage, null);
    }

    @Override
    public MqttMessage send(MqttMessage mqttMessage, Long timeout)
        throws KapuaException
    {
        List<MqttMessage> responses = new ArrayList<>();

        if (timeout != null) {
            // synchronized (responses) {
            sendInternal(mqttMessage, responses, timeout);

            // try {
            // responses.wait(MqttClientSetting.getInstance().getLong(MqttClientSettingKeys.SEND_TIMEOUT_MAX));
            // }
            // catch (InterruptedException e) {
            // Thread.interrupted();
            // throw new MqttClientException(MqttClientErrorCodes.CLIENT_CALLBACK_ERROR,
            // e,
            // (Object[]) null);
            // }

            // }

            if (responses.isEmpty()) {
                throw new MqttClientException(MqttClientErrorCodes.CLIENT_TIMEOUT_EXCEPTION,
                                              null,
                                              new Object[] {
                                                             mqttMessage.getRequestTopic()
                                              });

            }
            return responses.get(0);
        }
        else {
            sendInternal(mqttMessage, responses, timeout);
            return null;
        }
    }

    private void sendInternal(MqttMessage mqttMessage, List<MqttMessage> responses, Long timeout)
        throws KapuaException
    {
        //
        // Subscribe if necessary
        if (mqttMessage.getResponseTopic() != null) {
            mqttCallback = new MqttClientCallback(responses);
            getPahoClient().setCallback(mqttCallback);
            subscribe(mqttMessage.getResponseTopic());
        }

        //
        // Publish message
        MqttTopic mqttTopic = mqttMessage.getRequestTopic();
        MqttPayload mqttPayload = mqttMessage.getPayload();
        try {
            getPahoClient().publish(mqttTopic.getTopic(),
                                    mqttPayload.getBody(),
                                    0,
                                    false);
        }
        catch (MqttException | KapuaException e) {
            throw new MqttClientException(MqttClientErrorCodes.CLIENT_PUBLISH_ERROR,
                                          e,
                                          new Object[] { mqttTopic.getTopic(),
                                                         mqttPayload.getBody() });
        }

        //
        // Wait if required
        if (timeout != null &&
            responses != null) {
            Timer timeoutTimer = new Timer("timeoutTimer", true);

            timeoutTimer.schedule(new TimerTask() {

                @Override
                public void run()
                {
                    if (responses != null) {
                        mqttCallback.notifyAll();
                    }
                }
            }, timeout);

            try {
                synchronized (mqttCallback) {
                    mqttCallback.wait();
                }
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            timeoutTimer.cancel();
        }
    }

    private void subscribe(MqttTopic mqttTopic)
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

    private void unsubscribe(MqttTopic mqttTopic)
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

    private void unsubscribeAll()
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
    public void clean()
        throws KapuaException
    {
        try {
            mqttCallback = null;
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
    @Override
    public String getClientId()
    {
        try {
            return getPahoClient().getClientId();
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
