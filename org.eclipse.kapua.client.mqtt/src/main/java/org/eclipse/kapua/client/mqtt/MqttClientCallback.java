package org.eclipse.kapua.client.mqtt;

import org.eclipse.kapua.client.KapuaClientCallback;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttClientCallback implements MqttCallback, KapuaClientCallback
{

    public MqttClientCallback()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void connectionLost(Throwable cause)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void messageArrived(String topic, MqttMessage message)
        throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token)
    {
        // TODO Auto-generated method stub

    }

}
