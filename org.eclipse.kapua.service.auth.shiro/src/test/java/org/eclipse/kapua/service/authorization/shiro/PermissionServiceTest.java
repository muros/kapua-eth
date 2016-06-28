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
package org.eclipse.kapua.service.authorization.shiro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.util.KapuaEidGenerator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authorization.permission.UserPermission;
import org.eclipse.kapua.service.authorization.permission.UserPermissionCreator;
import org.eclipse.kapua.service.authorization.permission.UserPermissionFactory;
import org.eclipse.kapua.service.authorization.permission.UserPermissionService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionServiceTest
{
    @SuppressWarnings("unused")
    private static final Logger s_logger = LoggerFactory.getLogger(PermissionServiceTest.class);

    @Test
    public void testCreate()
        throws Exception
    {
        KapuaEid scopeId = KapuaEidGenerator.generate();
        KapuaEid targetScopeId = KapuaEidGenerator.generate();
        KapuaEid userId = KapuaEidGenerator.generate();

        KapuaLocator locator = KapuaLocator.getInstance();
        UserPermissionFactory permissionFactory = locator.getFactory(UserPermissionFactory.class);

        UserPermissionCreator permissionCreator = permissionFactory.newCreator(scopeId);
        permissionCreator.setUserId(userId);
        permissionCreator.setDomain("A domain");
        permissionCreator.setAction("A action");
        permissionCreator.setTargetScopeId(targetScopeId);

        // create the Permission
        UserPermissionService permissionService = locator.getService(UserPermissionService.class);
        UserPermission permission = permissionService.create(permissionCreator);

        //
        // Permission asserts
        assertNotNull(permission.getId());
        assertNotNull(permission.getId().getId());
        assertEquals(permissionCreator.getScopeId(), permission.getScopeId());

        assertNotNull(permission.getCreatedOn());
        assertNotNull(permission.getCreatedBy());

        assertEquals(permissionCreator.getUserId(), permission.getUserId());
        assertEquals(permissionCreator.getDomain(), permission.getDomain());
        assertEquals(permissionCreator.getAction(), permission.getAction());
        assertEquals(permissionCreator.getTargetScopeId(), permission.getTargetScopeId());
    }
}
