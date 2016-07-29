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
package org.eclipse.kapua.service.authorization;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.permission.Permission;

/**
 * AuthenticationService exposes APIs to manage User object under an Account.
 * It includes APIs to create, update, find, list and delete Users.
 * Instances of the UserService can be acquired through the ServiceLocator.
 */
public interface AuthorizationService extends KapuaService
{
    public boolean isPermitted(Permission permission)
        throws KapuaException;

    public void checkPermission(Permission permission)
        throws KapuaException;
}
