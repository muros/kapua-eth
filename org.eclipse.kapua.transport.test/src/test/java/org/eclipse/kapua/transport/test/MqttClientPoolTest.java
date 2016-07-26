package org.eclipse.kapua.transport.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.kapua.transport.mqtt.MqttClient;
import org.eclipse.kapua.transport.mqtt.pooling.MqttClientPool;
import org.eclipse.kapua.transport.mqtt.pooling.PooledMqttClientFactory;
import org.eclipse.kapua.transport.mqtt.pooling.setting.MqttClientPoolSetting;
import org.eclipse.kapua.transport.mqtt.pooling.setting.MqttClientPoolSettingKeys;
import org.junit.Assert;
import org.junit.Test;

public class MqttClientPoolTest extends Assert
{
    @Test
    public void testPoolBorrow()
        throws Exception
    {
        //
        // Get pool
        MqttClientPool transportPool = new MqttClientPool(new PooledMqttClientFactory());

        //
        // Borrow client
        MqttClient mqttClient = null;
        try {
            mqttClient = transportPool.borrowObject();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        //
        // Verify client
        assertNotNull("mqttClient", mqttClient);
        assertTrue("mqttClient.isConnected", mqttClient.isConnected());

        //
        // Return client
        try {
            transportPool.returnObject(mqttClient);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPoolBorrowMax()
        throws Exception
    {
        //
        // Get pool
        MqttClientPool transportPool = new MqttClientPool(new PooledMqttClientFactory());

        //
        // Test max borrow clients
        MqttClientPoolSetting setting = MqttClientPoolSetting.getInstance();
        long maxClients = setting.getLong(MqttClientPoolSettingKeys.CLIENT_POOL_SIZE_MAXTOTAL);
        List<MqttClient> mqttClients = new ArrayList<>();
        for (int i = 0; i < maxClients; i++) {
            mqttClients.add(transportPool.borrowObject());
        }

        //
        // Verify borrowed clients
        for (MqttClient t : mqttClients) {
            assertNotNull("mqttClient", t);
            assertTrue("mqttClient.isConnected", t.isConnected());
        }
        assertEquals("numActiveClients", maxClients, transportPool.getNumActive());

        //
        // Ask one more transport client
        try {
            mqttClients.add(transportPool.borrowObject());
            fail("Should have thrown " + NoSuchElementException.class.getName() + " exception");
        }
        catch (NoSuchElementException nsee) {
            // All ok
        }
        catch (Exception e) {
            assertEquals("Should have thrown " + NoSuchElementException.class.getName() + " exception", NoSuchElementException.class, e.getClass());
        }

        //
        // Return clients
        Iterator<MqttClient> transportClientIterator = mqttClients.iterator();
        while (transportClientIterator.hasNext()) {
            transportPool.returnObject(transportClientIterator.next());
        }
        assertEquals("numActiveClients", 0, transportPool.getNumActive());
    }

}
