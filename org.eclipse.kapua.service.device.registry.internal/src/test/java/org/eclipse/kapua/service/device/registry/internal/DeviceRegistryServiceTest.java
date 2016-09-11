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
    public static void beforeClass() throws KapuaException {
        enableH2Connection();
        scriptSession(DEFAULT_PATH, DEFAULT_FILTER);
    }

    @AfterClass
    public static void tearDown()
    {
        scriptSession(DEFAULT_PATH, DROP_FILTER);
    }

    @Test
    public void testCreate() throws Exception {
        Device device = KapuaSecurityUtils.doPriviledge(() -> {
            DeviceCreator deviceCreator = new TestDeviceCreator(new KapuaEid(BigInteger.ONE));
            deviceCreator.setClientId("foo");
//            return deviceRegistryService.create(deviceCreator);
            return null;
        });
    }

    class TestDeviceCreator extends DeviceCreatorImpl {

        protected TestDeviceCreator(KapuaId scopeId) {
            super(scopeId);
        }

    }

}
