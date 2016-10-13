/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordToken;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationServiceTest extends AbstractAuthenticationServiceTest
{
    @SuppressWarnings("unused")
    private static final Logger s_logger = LoggerFactory.getLogger(AuthenticationServiceTest.class);

    /**
     * We should ignore this test until schema loading feature is provided.
     */
    @Ignore
    @Test
    public void testLoginAndLogout()
        throws Exception
    {
        String username = "kapua-sys";
        char[] password = "kapua-password".toCharArray();

        KapuaLocator locator = KapuaLocator.getInstance();
        UsernamePasswordTokenFactory usernamePasswordTokenFactory = locator.getFactory(UsernamePasswordTokenFactory.class);
        UsernamePasswordToken credentialsToken = usernamePasswordTokenFactory.newInstance(username, password);

        AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
        authenticationService.login(credentialsToken);

        Subject currentSubject = SecurityUtils.getSubject();

        assertTrue(currentSubject.isAuthenticated());
        assertEquals(username, currentSubject.getPrincipal());

        authenticationService.logout();

        assertFalse(currentSubject.isAuthenticated());
        assertNull(currentSubject.getPrincipal());
    }
}
