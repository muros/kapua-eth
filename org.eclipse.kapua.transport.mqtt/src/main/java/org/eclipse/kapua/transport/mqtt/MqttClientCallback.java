package org.eclipse.kapua.transport.mqtt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.transport.TransportCallback;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public class MqttClientCallback implements MqttCallback, TransportCallback<MqttMessage>
{
    private List<MqttMessage> responses;
    private int               expectedResponses;

    public MqttClientCallback(List<MqttMessage> responses)
    {
        this(responses, 1);
    }

    public MqttClientCallback(List<MqttMessage> responses, int expectedResponses)
    {
        this.responses = responses;
        this.expectedResponses = expectedResponses;
    }

    @Override
    /**
     * Override from {@link org.eclipse.paho.client.mqttv3.MqttCallback}
     */
    public void messageArrived(String stringTopic, org.eclipse.paho.client.mqttv3.MqttMessage message)
        throws Exception
    {
        MqttTopic mqttTopic = new MqttTopic(stringTopic);
        MqttPayload mqttPayload = new MqttPayload(message.getPayload());
        MqttMessage mqttMessage = new MqttMessage(mqttTopic,
                                                  new Date(),
                                                  mqttPayload);

        //
        // Call the KapuaClientCallback
        messageArrived(mqttMessage);
    }

    @Override
    /**
     * Override from {@link org.eclipse.kapua.transport.TransportCallback<MqttMessage>}
     */
    public void messageArrived(MqttMessage messageArrived)
        throws KapuaException
    {
        //
        // Add to the received responses
        if (responses == null) {
            responses = new ArrayList<MqttMessage>();
        }

        //
        // Convert MqttMessage to the given device-levelMessage
        responses.add(messageArrived);

        //
        // notify if all expected responses arrived
        if (expectedResponses == responses.size()) {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token)
    {
        // TODO Auto-generated method stub
        // ?????
    }

    @Override
    public void connectionLost(Throwable cause)
    {
        //
        // Call the KapuaClientCallback
        try {
            clientConnectionLost(cause);
        }
        catch (KapuaException e) {
            // TODO Auto-generated catch block
            // FIXME: WTF to do here??
            e.printStackTrace();

            notifyAll();
        }
    }

    @Override
    public void clientConnectionLost(Throwable cause)
        throws KapuaException
    {
        throw new MqttClientException(MqttClientErrorCodes.CLIENT_CONNECTION_LOST, cause, (Object[]) null);
    }

}
