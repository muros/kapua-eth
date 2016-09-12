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
package org.eclipse.kapua.service.device.registry.internal;

import org.assertj.core.api.Assertions;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.jpa.SimpleSqlScriptExecutor;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.shiro.AuthenticationEntityManagerFactory;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.test.KapuaTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;

public class DeviceRegistryServiceTest extends KapuaTest {

    DeviceRegistryService deviceRegistryService = KapuaLocator.getInstance().getService(DeviceRegistryService.class);

    public static String DEFAULT_FILTER = "dvc_*.sql";
    public static String DROP_FILTER = "dvc_*_drop.sql";

    public static void scriptSession(String fileFilter)
    {
        EntityManager em = null;
        try {

            em = AuthenticationEntityManagerFactory.getEntityManager();
            em.beginTransaction();

            SimpleSqlScriptExecutor sqlScriptExecutor = new SimpleSqlScriptExecutor();
            sqlScriptExecutor.scanScripts(fileFilter);
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
    public static void beforeClass() throws KapuaException {
        enableH2Connection();
        scriptSession(DEFAULT_FILTER);
    }

    @AfterClass
    public static void tearDown()
    {
        scriptSession(DROP_FILTER);
    }

    // Tests

    @Test
    public void shouldAssignIdAfterCreation() throws Exception {
        Device device = KapuaSecurityUtils.doPriviledge(() -> {
            DeviceCreator deviceCreator = new TestDeviceCreator(new KapuaEid(BigInteger.ONE));
            deviceCreator.setClientId("foo");
            return deviceRegistryService.create(deviceCreator);
        });
        Assertions.assertThat(device.getId()).isNotNull();
    }

    @Test
    public void shouldFindDeviceByClientID() throws Exception {
        KapuaSecurityUtils.doPriviledge(() -> {
            DeviceCreator deviceCreator = new TestDeviceCreator(new KapuaEid(BigInteger.ONE));
            deviceCreator.setClientId("bar");
            deviceRegistryService.create(deviceCreator);
            Device deviceFound = deviceRegistryService.findByClientId(new KapuaEid(BigInteger.ONE), "bar");
            Assertions.assertThat(deviceFound).isNotNull();
            return null;
        });
    }

    // Test classes

    class TestDeviceCreator extends DeviceCreatorImpl {

        protected TestDeviceCreator(KapuaId scopeId) {
            super(scopeId);
        }

    }

}
