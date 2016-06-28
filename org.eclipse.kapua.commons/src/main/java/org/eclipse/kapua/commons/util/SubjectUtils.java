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
package org.eclipse.kapua.commons.util;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.AuthenticationService;

public class SubjectUtils
{
    public static String MDC_USERNAME = "username";

    public static KapuaId getCurrentUserId()
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
        return authenticationService.getCurrentUserId();
    }
}
