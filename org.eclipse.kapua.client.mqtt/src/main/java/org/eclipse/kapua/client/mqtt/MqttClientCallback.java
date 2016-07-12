package org.eclipse.kapua.client.mqtt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.KapuaClientCallback;
import org.eclipse.kapua.client.message.mqtt.MqttMessage;
import org.eclipse.kapua.client.message.mqtt.MqttPayload;
import org.eclipse.kapua.client.message.mqtt.MqttTopic;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public class MqttClientCallback implements MqttCallback, KapuaClientCallback<MqttTopic, MqttPayload, MqttMessage>
{
    List<MqttMessage> responses;

    public MqttClientCallback(List<MqttMessage> responses)
    {
        this.responses = responses;
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
        }
    }

    @Override
    public void messageArrived(String stringTopic, org.eclipse.paho.client.mqttv3.MqttMessage message)
        throws Exception
    {
        //
        // Parse the paho mqtt message
        MqttTopic topic = new MqttTopic();
        topic.fromClientDestination(stringTopic);

        MqttPayload mqttPayload = new MqttPayload();
        mqttPayload.readFromByteArray(message.getPayload());

        //
        // Call the KapuaClientCallback
        messageArrived(new MqttMessage(topic, new Date(), mqttPayload));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token)
    {
        // TODO Auto-generated method stub
        // ?????
    }

    @Override
    public void clientConnectionLost(Throwable cause)
        throws KapuaException
    {
        throw new MqttClientException(MqttClientErrorCodes.CLIENT_CONNECTION_LOST, cause, (Object[]) null);
    }

    @Override
    public void messageArrived(MqttMessage messageArrived)
    {
        //
        // Add to the received responses
        if (responses == null) {
            responses = new ArrayList<MqttMessage>();
        }

        responses.add(messageArrived);

    }

}
