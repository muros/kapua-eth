/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat
 *
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.common;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.internal.DeviceCreatorImpl;
import org.junit.Test;

import static java.math.BigInteger.ONE;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class DeviceValidationTest {

    DeviceValidation deviceValidation = new DeviceValidation(mock(PermissionFactory.class), mock(AuthorizationService.class));

    @Test
    public void shouldValidateNullCreator() throws KapuaException {
        try {
            deviceValidation.validateCreatePreconditions(null);
        } catch (KapuaIllegalNullArgumentException e) {
            if(e.getMessage().contains("deviceCreator")) {
                return;
            }
        }
        fail();
    }

    @Test
    public void shouldValidateNullScopeId() throws KapuaException {
        DeviceCreator deviceCreator = new TestDeviceCreator(null);
        try {
            deviceValidation.validateCreatePreconditions(deviceCreator);
        } catch (KapuaIllegalNullArgumentException e) {
            if(e.getMessage().contains("scopeId")) {
                return;
            }
        }
        fail();
    }

    @Test
    public void shouldValidateNullClientId() throws KapuaException {
        DeviceCreator deviceCreator = new TestDeviceCreator(new KapuaEid(ONE));
        try {
            deviceValidation.validateCreatePreconditions(deviceCreator);
        } catch (KapuaIllegalNullArgumentException e) {
            if(e.getMessage().contains("clientId")) {
                return;
            }
        }
        fail();
    }

    @Test
    public void shouldPassValidation() throws KapuaException {
        DeviceCreator deviceCreator = new TestDeviceCreator(new KapuaEid(ONE));
        deviceCreator.setClientId("foo");

        deviceValidation.validateCreatePreconditions(deviceCreator);
    }

    static class TestDeviceCreator extends DeviceCreatorImpl{
        protected TestDeviceCreator(KapuaId scopeId) {
            super(scopeId);
        }
    }

}
