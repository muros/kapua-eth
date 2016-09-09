/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.connection;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.service.authentication.shiro.AuthenticationEntityManagerFactory;
import org.eclipse.kapua.test.KapuaTest;
import org.eclipse.kapua.test.SimpleSqlScriptExecutor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DeviceConnectionTest extends KapuaTest {
    //
    // static DeviceConnectionService deviceConnectionService = locator.getService(DeviceConnectionService.class);
    // static DeviceConnectionFactory deviceConnectionFactory = locator.getFactory(DeviceConnectionFactory.class);

    public static String DEFAULT_PATH = "src/main/sql/H2";
    public static String DEFAULT_FILTER = "athz_*.sql";
    public static String DROP_FILTER = "athz_*_drop.sql";

    public static void scriptSession(String path, String fileFilter)
    {
        EntityManager em = null;
        try {

            em = AuthenticationEntityManagerFactory.getEntityManager();
            em.beginTransaction();

            SimpleSqlScriptExecutor sqlScriptExecutor = new SimpleSqlScriptExecutor();
            sqlScriptExecutor.scanScripts(path, fileFilter);
            sqlScriptExecutor.executeUpdate(em);

            em.commit();

        }
        catch (KapuaException e) {
            if (em != null)
                em.rollback();
        }
        finally {
            if (em != null)
                em.close();
        }

    }

    @BeforeClass
    public static void tearUp()
            throws KapuaException
    {
        enableH2Connection();
        scriptSession(DEFAULT_PATH, DEFAULT_FILTER);
    }

    @AfterClass
    public static void tearDown()
    {
        scriptSession(DEFAULT_PATH, DROP_FILTER);
    }

    @Test
    public void testCreate()
        throws Exception
    {
        // {
        // // Creator setup
        // DeviceConnectionCreator deviceConnectionCreator = deviceConnectionFactory.newCreator(KapuaEidGenerator.generate());
        // deviceConnectionCreator.setClientId("testClientId-" + generateRandomString());
        // deviceConnectionCreator.setClientIp("111.222.111.111");
        // deviceConnectionCreator.setProtocol("mqtt");
        // deviceConnectionCreator.setServerIp("222.111.222.111");
        // deviceConnectionCreator.setUserId(adminUserId);
        //
        // // Create
        // DeviceConnection deviceConnection = deviceConnectionService.create(deviceConnectionCreator);
        //
        // // Verify
        // assertNotNull(deviceConnection);
        // assertNotNull(deviceConnection.getId());
        // assertEquals(deviceConnectionCreator.getScopeId(), deviceConnection.getScopeId());
        // assertNotNull(deviceConnection.getCreatedOn());
        // assertEquals(adminUserId, deviceConnection.getCreatedBy());
        // assertNotNull(deviceConnection.getModifiedOn());
        // assertEquals(adminUserId, deviceConnection.getModifiedBy());
        // assertEquals(deviceConnectionCreator.getClientId(), deviceConnection.getClientId());
        // assertEquals(adminUserId, deviceConnection.getUserId());
        // assertEquals(deviceConnectionCreator.getProtocol(), deviceConnection.getProtocol());
        // assertEquals(deviceConnectionCreator.getClientIp(), deviceConnection.getClientIp());
        // assertEquals(deviceConnectionCreator.getServerIp(), deviceConnection.getServerIp());
        //
        // assertEquals(new Properties(), deviceConnection.getEntityAttributes());
        // assertEquals(new Properties(), deviceConnection.getEntityProperties());
        // assertEquals(DeviceConnectionStatus.CONNECTED, deviceConnection.getStatus());
        // }
        //
        // @Test
        // public void testUpdate()
        // throws Exception
        // {
        // // Creator setup
        // DeviceConnectionCreator deviceConnectionCreator = deviceConnectionFactory.newCreator(KapuaEidGenerator.generate());
        // deviceConnectionCreator.setClientId("testClientId-" + generateRandomString());
        // deviceConnectionCreator.setClientIp("111.222.111.111");
        // deviceConnectionCreator.setServerIp("222.111.222.111");
        // deviceConnectionCreator.setProtocol("mqtt");
        // deviceConnectionCreator.setUserId(adminUserId);
        //
        // // Create
        // DeviceConnection deviceConnection = deviceConnectionService.create(deviceConnectionCreator);
        //
        // // Verify
        // assertNotNull(deviceConnection);
        // assertNotNull(deviceConnection.getId());
        // assertEquals(deviceConnectionCreator.getScopeId(), deviceConnection.getScopeId());
        // assertNotNull(deviceConnection.getCreatedOn());
        // assertEquals(adminUserId, deviceConnection.getCreatedBy());
        // assertNotNull(deviceConnection.getModifiedOn());
        // assertEquals(adminUserId, deviceConnection.getModifiedBy());
        // assertEquals(deviceConnectionCreator.getClientId(), deviceConnection.getClientId());
        // assertEquals(adminUserId, deviceConnection.getUserId());
        // assertEquals(deviceConnectionCreator.getProtocol(), deviceConnection.getProtocol());
        // assertEquals(deviceConnectionCreator.getClientIp(), deviceConnection.getClientIp());
        // assertEquals(deviceConnectionCreator.getServerIp(), deviceConnection.getServerIp());
        //
        // assertEquals(new Properties(), deviceConnection.getEntityAttributes());
        // assertEquals(new Properties(), deviceConnection.getEntityProperties());
        // assertEquals(DeviceConnectionStatus.CONNECTED, deviceConnection.getStatus());
        //
        // // Change informations
        // Properties deviceConnectionProperties = new Properties();
        // deviceConnectionProperties.setProperty("deviceConnectionProperties", "properties");
        //
        // Properties deviceConnectionAttributes = new Properties();
        // deviceConnectionAttributes.setProperty("deviceConnectionAttributes", "attributes");
        //
        // deviceConnection.setClientId("notUpdatableField");
        // deviceConnection.setClientIp("222.111.222.111");
        // deviceConnection.setServerIp("111.222.111.222");
        // deviceConnection.setEntityProperties(deviceConnectionProperties);
        // deviceConnection.setEntityAttributes(deviceConnectionAttributes);
        // deviceConnection.setProtocol("mqtts");
        // deviceConnection.setStatus(DeviceConnectionStatus.DISCONNECTED);
        //
        // // Update
        // Thread.sleep(10);
        // DeviceConnection deviceConnectionUpdated = deviceConnectionService.update(deviceConnection);
        //
        // // Verify
        // assertNotNull(deviceConnectionUpdated);
        // assertEquals(deviceConnection.getId(), deviceConnectionUpdated.getId());
        // assertEquals(deviceConnection.getScopeId(), deviceConnectionUpdated.getScopeId());
        // assertEquals(deviceConnection.getCreatedOn(), deviceConnectionUpdated.getCreatedOn());
        // assertEquals(deviceConnection.getCreatedBy(), deviceConnectionUpdated.getCreatedBy());
        //
        // assertEquals(deviceConnection.getCreatedOn(), deviceConnection.getModifiedOn());
        // assertTrue(deviceConnection.getModifiedOn().getTime() < deviceConnectionUpdated.getModifiedOn().getTime());
        // assertTrue(deviceConnectionUpdated.getCreatedOn().getTime() < deviceConnectionUpdated.getModifiedOn().getTime());
        // assertEquals(deviceConnection.getModifiedBy(), deviceConnectionUpdated.getModifiedBy());
        // assertEquals(deviceConnection.getModifiedBy(), deviceConnectionUpdated.getModifiedBy());
        //
        // assertNotEquals(deviceConnection.getClientId(), deviceConnectionUpdated.getClientId());
        // assertEquals(deviceConnectionCreator.getClientId(), deviceConnectionUpdated.getClientId());
        //
        // assertEquals(deviceConnection.getClientIp(), deviceConnectionUpdated.getClientIp());
        // assertEquals(deviceConnection.getServerIp(), deviceConnectionUpdated.getServerIp());
        // assertEquals(deviceConnectionProperties, deviceConnectionUpdated.getEntityProperties());
        // assertEquals(deviceConnectionAttributes, deviceConnectionUpdated.getEntityAttributes());
        // assertEquals(deviceConnection.getProtocol(), deviceConnectionUpdated.getProtocol());
        // assertEquals(DeviceConnectionStatus.DISCONNECTED, deviceConnectionUpdated.getStatus());
        //
        // // Test optimistic lock
        // try {
        // deviceConnectionService.update(deviceConnection);
        // }
        // catch (KapuaOptimisticLockingException e) {
        // assertEquals(KapuaOptimisticLockingException.class, e.getClass());
        // }
        // }
        //
        // @Test
        // public void testFind()
        // throws Exception
        // {
        // // Creator setup
        // DeviceConnectionCreator deviceConnectionCreator = deviceConnectionFactory.newCreator(KapuaEidGenerator.generate());
        // deviceConnectionCreator.setClientId("testClientId-" + generateRandomString());
        // deviceConnectionCreator.setClientIp("111.222.111.111");
        // deviceConnectionCreator.setServerIp("222.111.222.111");
        // deviceConnectionCreator.setProtocol("mqtt");
        // deviceConnectionCreator.setUserId(adminUserId);
        //
        // // Create
        // DeviceConnection deviceConnection = deviceConnectionService.create(deviceConnectionCreator);
        //
        // // Verify
        // assertNotNull(deviceConnection);
        // assertNotNull(deviceConnection.getId());
        // assertEquals(deviceConnectionCreator.getScopeId(), deviceConnection.getScopeId());
        // assertNotNull(deviceConnection.getCreatedOn());
        // assertEquals(adminUserId, deviceConnection.getCreatedBy());
        // assertNotNull(deviceConnection.getModifiedOn());
        // assertEquals(adminUserId, deviceConnection.getModifiedBy());
        // assertEquals(deviceConnectionCreator.getClientId(), deviceConnection.getClientId());
        // assertEquals(adminUserId, deviceConnection.getUserId());
        // assertEquals(deviceConnectionCreator.getProtocol(), deviceConnection.getProtocol());
        // assertEquals(deviceConnectionCreator.getClientIp(), deviceConnection.getClientIp());
        // assertEquals(deviceConnectionCreator.getServerIp(), deviceConnection.getServerIp());
        //
        // assertEquals(new Properties(), deviceConnection.getEntityAttributes());
        // assertEquals(new Properties(), deviceConnection.getEntityProperties());
        // assertEquals(DeviceConnectionStatus.CONNECTED, deviceConnection.getStatus());
        //
        // // Find
        // DeviceConnection foundDeviceConnection = deviceConnectionService.find(deviceConnection.getScopeId(), deviceConnection.getId());
        //
        // // Verify
        // assertNotNull(foundDeviceConnection);
        // assertEquals(deviceConnection.getId(), foundDeviceConnection.getId());
        // assertEquals(deviceConnection.getScopeId(), foundDeviceConnection.getScopeId());
        // assertEquals(deviceConnection.getCreatedOn(), foundDeviceConnection.getCreatedOn());
        // assertEquals(deviceConnection.getCreatedBy(), foundDeviceConnection.getCreatedBy());
        // assertEquals(deviceConnection.getModifiedOn(), foundDeviceConnection.getModifiedOn());
        // assertEquals(deviceConnection.getModifiedBy(), foundDeviceConnection.getModifiedBy());
        // assertEquals(deviceConnection.getClientId(), foundDeviceConnection.getClientId());
        // assertEquals(deviceConnection.getUserId(), foundDeviceConnection.getUserId());
        // assertEquals(deviceConnection.getProtocol(), foundDeviceConnection.getProtocol());
        // assertEquals(deviceConnection.getClientIp(), foundDeviceConnection.getClientIp());
        // assertEquals(deviceConnection.getServerIp(), foundDeviceConnection.getServerIp());
        //
        // assertEquals(deviceConnection.getEntityAttributes(), foundDeviceConnection.getEntityAttributes());
        // assertEquals(deviceConnection.getEntityProperties(), foundDeviceConnection.getEntityProperties());
        // assertEquals(deviceConnection.getStatus(), foundDeviceConnection.getStatus());
        // }
        //
        // @Test
        // public void testDelete()
        // throws Exception
        // {
        // // Creator setup
        // DeviceConnectionCreator deviceConnectionCreator = deviceConnectionFactory.newCreator(KapuaEidGenerator.generate());
        // deviceConnectionCreator.setClientId("testClientId-" + generateRandomString());
        // deviceConnectionCreator.setClientIp("111.222.111.111");
        // deviceConnectionCreator.setServerIp("222.111.222.111");
        // deviceConnectionCreator.setProtocol("mqtt");
        // deviceConnectionCreator.setUserId(adminUserId);
        //
        // // Create
        // DeviceConnection deviceConnection = deviceConnectionService.create(deviceConnectionCreator);
        //
        // // Find
        // DeviceConnection foundDeviceConnection = deviceConnectionService.find(deviceConnection.getScopeId(), deviceConnection.getId());
        //
        // // Verify
        // assertNotNull(foundDeviceConnection);
        //
        // // Delete
        // deviceConnectionService.delete(deviceConnection);
        //
        // // Find and verify
        // foundDeviceConnection = deviceConnectionService.find(deviceConnection.getScopeId(), deviceConnection.getId());
        //
        // // Verify
        // assertNull(foundDeviceConnection);
    }

}
