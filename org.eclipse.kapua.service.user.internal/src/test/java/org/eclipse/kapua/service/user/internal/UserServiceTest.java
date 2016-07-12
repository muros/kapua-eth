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
package org.eclipse.kapua.service.user.internal;

import org.eclipse.kapua.service.KapuaTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceTest extends KapuaTest
{
    @SuppressWarnings("unused")
    private static final Logger s_logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Test
    public void testCreate()
        throws Exception
    {
        //
        // // prepare the UserCreator
        // long now = (new Date()).getTime();
        // String username = MessageFormat.format("aaa_test_username_{0,number,#}", now);
        // String password = "We!come12345";
        // String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        // String displayName = MessageFormat.format("User Display Name {0}", now);
        //
        // // KapuaPeid accountPeid = KapuaEidGenerator.generate();//
        // KapuaId scopeId = KapuaEidGenerator.generate();
        //
        // KapuaLocator serviceLocator = KapuaLocator.getInstance();
        // UserFactory kapuaEntityCreatorFactory = serviceLocator.getFactory(UserFactory.class);
        // UserCreator userCreator = kapuaEntityCreatorFactory.newCreator(scopeId, username);
        //
        // userCreator.setRawPassword(password);
        // userCreator.setDisplayName(displayName);
        // userCreator.setEmail(userEmail);
        // userCreator.setPhoneNumber("+1 555 123 4567");
        //
        // // create the User
        // UserService userService = serviceLocator.getService(UserService.class);
        //
        // System.err.println("Subject: " + SubjectUtils.getCurrentUserId());
        // System.err.println("Account: " + SubjectUtils.getCurrentUserId());
        //
        // User user = userService.create(userCreator);
        //
        // //
        // // User asserts
        // assertNotNull(user.getId());
        // assertNotNull(user.getId().getId());
        // assertTrue(user.getOptlock() >= 0);
        // assertEquals(scopeId, user.getScopeId());
        // assertEquals(userCreator.getName(), user.getName());
        // assertNotNull(user.getCreatedOn());
        // assertNotNull(user.getCreatedBy());
        // assertNotNull(user.getModifiedOn());
        // assertNotNull(user.getModifiedBy());
        // assertEquals(userCreator.getDisplayName(), user.getDisplayName());
        // assertEquals(userCreator.getEmail(), user.getEmail());
        // assertEquals(userCreator.getPhoneNumber(), user.getPhoneNumber());
        // assertEquals(UserStatus.ENABLED, user.getStatus());
    }
}
