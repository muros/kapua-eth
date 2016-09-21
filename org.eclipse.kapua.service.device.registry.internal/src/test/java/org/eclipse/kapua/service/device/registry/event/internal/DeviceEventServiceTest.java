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
package org.eclipse.kapua.service.device.registry.event.internal;

import org.assertj.core.api.Assertions;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceEventType;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.device.registry.internal.DeviceEntityManagerFactory;
import org.eclipse.kapua.test.KapuaTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import static java.math.BigInteger.ONE;
import static org.eclipse.kapua.commons.security.KapuaSecurityUtils.doPriviledge;

public class DeviceEventServiceTest extends KapuaTest {

    DeviceEventService deviceEventService = KapuaLocator.getInstance().getService(DeviceEventService.class);

    public static String DEFAULT_FILTER = "dvc_*.sql";
    public static String DROP_FILTER = "dvc_*_drop.sql";

    // Data fixtures

    KapuaEid scope = new KapuaEid(BigInteger.valueOf(random.nextLong()));

    DeviceEventCreator deviceEventCreator;

    @Before
    public void before() {
        deviceEventCreator = new DeviceEventFactoryImpl().newCreator(scope, new KapuaEid(ONE));
    }

    // Database fixtures

    @BeforeClass
    public static void beforeClass() throws KapuaException {
        enableH2Connection();
        scriptSession(DeviceEntityManagerFactory.instance(), DEFAULT_FILTER);
    }

    @AfterClass
    public static void afterClass() throws KapuaException {
        scriptSession(DeviceEntityManagerFactory.instance(), DROP_FILTER);
    }

    // Tests

    @Test
    public void shouldAssignIdAfterCreation() throws Exception {
        doPriviledge(() -> {
            deviceEventCreator.setReceivedOn(new Date());
            deviceEventCreator.setResource("resource");
            DeviceEvent deviceEvent = deviceEventService.create(deviceEventCreator);
            Assertions.assertThat(deviceEvent.getId()).isNotNull();
            return null;
        });
    }

}