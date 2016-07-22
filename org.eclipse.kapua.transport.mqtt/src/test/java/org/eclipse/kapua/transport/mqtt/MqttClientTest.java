package org.eclipse.kapua.transport.mqtt;

import java.util.Date;

import org.eclipse.kapua.commons.util.SystemUtils;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.kapua.transport.utils.ClientIdGenerator;
import org.junit.Assert;
import org.junit.Test;

public class MqttClientTest extends Assert
{
    // private static String username = "kapua-sys";
    // private static String password = "We!come12345";

    private static String username = "edcguest";
    private static String password = "Welcome1";

    @Test
    public void testMqttClientConnectDisconnect()
        throws Exception
    {
        MqttClientConnectionOptions clientConnectOptions = new MqttClientConnectionOptions();
        clientConnectOptions.setClientId(ClientIdGenerator.next(MqttClientTest.class.getSimpleName()));
        clientConnectOptions.setUsername(username);
        clientConnectOptions.setPassword(password.toCharArray());
        clientConnectOptions.setEndpointURI(SystemUtils.getBrokerURI());

        //
        // Connect
        MqttClient mqttClient = new MqttClient();
        try {
            mqttClient.connectClient(clientConnectOptions);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        //
        // Verify
        assertTrue("client.connected", mqttClient.isConnected());
        assertEquals("client.clientId", clientConnectOptions.getClientId(), mqttClient.getClientId());

        //
        // Disconnect
        try {
            mqttClient.disconnectClient();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        assertFalse("client.connected", mqttClient.isConnected());
    }

    @Test
    public void testMqttClientSend()
        throws Exception
    {
        MqttClientConnectionOptions clientConnectOptions = new MqttClientConnectionOptions();
        clientConnectOptions.setClientId(ClientIdGenerator.next(MqttClientTest.class.getSimpleName()));
        clientConnectOptions.setUsername(username);
        clientConnectOptions.setPassword(password.toCharArray());
        clientConnectOptions.setEndpointURI(SystemUtils.getBrokerURI());

        //
        // Connect
        MqttClient mqttClient = new MqttClient();
        try {
            mqttClient.connectClient(clientConnectOptions);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        assertTrue("client.connected", mqttClient.isConnected());

        //
        // Send
        String sendTopic = "$EDC/edcguest/" + mqttClient.getClientId() + "/" + MqttClientTest.class.getSimpleName() + "/testMqttClientSendTopic";

        MqttTopic mqttTopic = new MqttTopic(sendTopic);
        MqttPayload mqttPayload = new MqttPayload("testMqttClientSendPayload".getBytes());

        MqttMessage mqttMessage = new MqttMessage(mqttTopic,
                                                  new Date(),
                                                  mqttPayload);

        MqttMessage responseMessage = null;
        try {
            responseMessage = mqttClient.send(mqttMessage, null);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        //
        // Verify
        assertNull("responseMessage", responseMessage);

        //
        // Disconnect
        try {
            mqttClient.disconnectClient();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        assertFalse("client.connected", mqttClient.isConnected());
    }

    @Test
    public void testMqttClientSendReceiveSameClient()
        throws Exception
    {
        MqttClientConnectionOptions clientConnectOptions = new MqttClientConnectionOptions();
        clientConnectOptions.setClientId(ClientIdGenerator.next(MqttClientTest.class.getSimpleName()));
        clientConnectOptions.setUsername(username);
        clientConnectOptions.setPassword(password.toCharArray());
        clientConnectOptions.setEndpointURI(SystemUtils.getBrokerURI());

        //
        // Connect
        MqttClient mqttClient = new MqttClient();
        try {
            mqttClient.connectClient(clientConnectOptions);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        assertTrue("client.connected", mqttClient.isConnected());

        //
        // Send
        String sendTopic = "$EDC/edcguest/" + mqttClient.getClientId() + "/" + MqttClientTest.class.getSimpleName() + "/testMqttClientSendTopic";

        String testPayload = "testMqttClientSendPayload";
        MqttTopic mqttTopic = new MqttTopic(sendTopic + "/request");
        MqttTopic mqttResponseTopic = new MqttTopic(sendTopic + "/#");
        MqttPayload mqttPayload = new MqttPayload(testPayload.getBytes());

        MqttMessage mqttMessage = new MqttMessage(mqttTopic,
                                                  new Date(),
                                                  mqttPayload);
        mqttMessage.setResponseTopic(mqttResponseTopic);

        MqttMessage responseMessage = null;
        try {
            responseMessage = mqttClient.send(mqttMessage, 3000000L);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        //
        // Verify
        assertNotNull("responseMessage", responseMessage);
        assertEquals("responseMessage.topic", responseMessage.getRequestTopic().getTopic(), mqttMessage.getRequestTopic().getTopic());
        assertEquals("responseMessage.payload", responseMessage.getPayload().getBody(), responseMessage.getPayload().getBody());
        assertEquals("responseMessage.payload", testPayload, new String(responseMessage.getPayload().getBody()));

        //
        // Disconnect
        try {
            mqttClient.disconnectClient();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        assertFalse("client.connected", mqttClient.isConnected());
    }
}
