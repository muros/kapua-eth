package org.eclipse.kapua.transport.pooling.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.transport.TransportClient;
import org.eclipse.kapua.transport.pooling.PooledTransportClientFactory;
import org.eclipse.kapua.transport.pooling.TransportClientPool;
import org.eclipse.kapua.transport.pooling.TransportClientPoolFactory;
import org.eclipse.kapua.transport.pooling.setting.internal.TransportClientPoolSetting;
import org.eclipse.kapua.transport.pooling.setting.internal.TransportClientPoolSettingKeys;
import org.junit.Assert;
import org.junit.Test;

public class TransportClientPoolImplTest extends Assert
{
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testPoolBorrow()
        throws Exception
    {
        //
        // Get pool
        KapuaLocator locator = KapuaLocator.getInstance();
        TransportClientPoolFactory transportPoolFactory = locator.getFactory(TransportClientPoolFactory.class);
        PooledTransportClientFactory pooledTransportObject = locator.getFactory(PooledTransportClientFactory.class);
        TransportClientPool<TransportClient> transportPool = transportPoolFactory.newClientPool(pooledTransportObject);

        //
        // Borrow client
        TransportClient transportClient = null;
        try {
            transportClient = transportPool.borrowObject();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        //
        // Verify client
        assertNotNull("transportClient", transportClient);
        assertTrue("transportClient.isConnected", transportClient.isConnected());

        //
        // Return client
        try {
            transportPool.returnObject(transportClient);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testPoolBorrowMax()
        throws Exception
    {
        //
        // Get pool
        KapuaLocator locator = KapuaLocator.getInstance();
        TransportClientPoolFactory transportPoolFactory = locator.getFactory(TransportClientPoolFactory.class);
        PooledTransportClientFactory pooledTransportObject = locator.getFactory(PooledTransportClientFactory.class);
        TransportClientPool<TransportClient> transportPool = transportPoolFactory.newClientPool(pooledTransportObject);

        //
        // Test max borrow clients
        TransportClientPoolSetting setting = TransportClientPoolSetting.getInstance();
        long maxClients = setting.getLong(TransportClientPoolSettingKeys.CLIENT_POOL_SIZE_MAXTOTAL);
        List<TransportClient> transportClients = new ArrayList<>();
        for (int i = 0; i < maxClients; i++) {
            transportClients.add(transportPool.borrowObject());
        }

        //
        // Verify borrowed clients
        for (TransportClient t : transportClients) {
            assertNotNull("transportClient", t);
            assertTrue("transportClient.isConnected", t.isConnected());
        }
        assertEquals("numActiveClients", maxClients, transportPool.getNumActive());

        //
        // Ask one more transport client
        try {
            transportClients.add(transportPool.borrowObject());
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
        Iterator<TransportClient> transportClientIterator = transportClients.iterator();
        while (transportClientIterator.hasNext()) {
            transportPool.returnObject(transportClientIterator.next());
        }
        assertEquals("numActiveClients", 0, transportPool.getNumActive());
    }

}
