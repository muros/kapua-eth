package org.eclipse.kapua.service.device.client.mqtt;

import org.eclipse.kapua.service.device.call.KapuaDeviceCallHandler;
import org.eclipse.kapua.service.device.client.KapuaClientCallback;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttClientCallback implements MqttCallback, KapuaClientCallback
{

    public MqttClientCallback(KapuaDeviceCallHandler kuraDeviceCallHandler)
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
