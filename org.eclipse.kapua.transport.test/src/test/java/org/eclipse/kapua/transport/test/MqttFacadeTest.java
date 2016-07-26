package org.eclipse.kapua.transport.test;

import java.util.Date;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.transport.TransportClientFactory;
import org.eclipse.kapua.transport.TransportFacade;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.junit.Assert;
import org.junit.Test;

public class MqttFacadeTest extends Assert
{
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testMqttClientSend()
        throws Exception
    {
        //
        // Get facade
        KapuaLocator locator = KapuaLocator.getInstance();
        TransportClientFactory transportFacadeFactory = locator.getFactory(TransportClientFactory.class);
        TransportFacade transportFacade = transportFacadeFactory.getFacade();

        assertNotNull("client.clientId", transportFacade.getClientId());

        //
        // Send
        String sendTopic = "$EDC/edcguest/" + transportFacade.getClientId() + "/" + MqttClientTest.class.getSimpleName() + "/testTransportFacadeSend";

        MqttTopic mqttTopic = new MqttTopic(sendTopic);
        MqttPayload mqttPayload = new MqttPayload("testTransportFacadeSendPayload".getBytes());

        MqttMessage mqttMessage = new MqttMessage(mqttTopic,
                                                  new Date(),
                                                  mqttPayload);

        TransportMessage responseMessage = null;
        try {
            responseMessage = transportFacade.sendSync(mqttMessage, null);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        //
        // Verify
        assertNull("responseMessage", responseMessage);

        //
        // Clean
        try {
            transportFacade.clean();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
