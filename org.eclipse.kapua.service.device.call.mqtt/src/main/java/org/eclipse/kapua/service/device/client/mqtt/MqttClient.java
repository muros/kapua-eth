package org.eclipse.kapua.service.device.client.mqtt;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.kura.exception.KapuaDeviceCallException;
import org.eclipse.kapua.service.device.client.KapuaClient;
import org.eclipse.kapua.service.device.client.KapuaClientCallback;
import org.eclipse.kapua.service.device.message.request.KapuaRequestPayload;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MqttClient extends org.eclipse.paho.client.mqttv3.MqttClient implements KapuaClient
{

    public MqttClient(String serverURI, String clientId)
        throws MqttException
    {
        super(serverURI, clientId);
    }

    public void publish(String requestTopic, KapuaRequestPayload requestPayload)
        throws KapuaException
    {
        org.eclipse.paho.client.mqttv3.MqttMessage message = new org.eclipse.paho.client.mqttv3.MqttMessage(requestPayload.toByteArray());

        try {
            super.publish(requestTopic, message);
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

    public void subscribe(String requestTopic, Object o)
        throws KapuaException
    {
        try {
            super.subscribe(requestTopic);
        }
        catch (MqttException e) {
            throw new KapuaDeviceCallException(null, e, (Object[]) null);
        }
    }

    public void setCallback(KapuaClientCallback clientCallback)
        throws KapuaException
    {
        setCallback(clientCallback);
    }

}
