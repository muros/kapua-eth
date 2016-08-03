package org.eclipse.kapua.transport.test;

import java.util.Date;

import org.eclipse.kapua.commons.util.SystemUtils;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.kapua.transport.mqtt.MqttClient;
import org.eclipse.kapua.transport.mqtt.MqttClientConnectionOptions;
import org.eclipse.kapua.transport.utils.ClientIdGenerator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class MqttClientTest extends Assert
{
    // private static String username = "kapua-sys";
    // private static String password = "We!come12345";

    private static String username = "edcguest";
    private static String password = "Welcome1";

    /**
     * Ignoring this test for a while. We should fix the build in the first place and then use embedded ActiveMQ
     * broker for tests.
     */
    @Ignore
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

    /**
     * Ignoring this test for a while. We should fix the build in the first place and then use embedded ActiveMQ
     * broker for tests.
     */
    @Ignore
    @Test
    public void testMqttClientPublish()
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

        try {
            mqttClient.publish(mqttMessage);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

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
