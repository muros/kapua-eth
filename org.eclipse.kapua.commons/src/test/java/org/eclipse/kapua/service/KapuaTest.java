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
package org.eclipse.kapua.service;

import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class KapuaTest
{
    protected static Random       random  = new Random();
    protected static KapuaLocator locator = KapuaLocator.getInstance();

    protected static KapuaId      adminUserId;

    @BeforeClass
    public static void setUp()
    {
        try {
            //
            // Login
            String username = "kapua-sys";
            String password = "We!come12345";

            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
            UsernamePasswordTokenFactory credentialsFactory = locator.getFactory(UsernamePasswordTokenFactory.class);
            authenticationService.login(credentialsFactory.newInstance(username, password.toCharArray()));

            //
            // Get current user Id
            adminUserId = KapuaSecurityUtils.getSession().getUserId();

        }
        catch (KapuaException exc) {
            exc.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown()
    {
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);

            authenticationService.logout();
        }
        catch (KapuaException exc) {
            exc.printStackTrace();
        }
    }

    //
    // Test utility methods
    //

    protected static String generateRandomString()
    {
        return generateRandomString(10, true, true);
    }

    protected static String generateRandomString(int chars, boolean letters, boolean numbers)
    {
        return RandomStringUtils.random(chars, 0, 0, letters, numbers, null, random);
    }
}
