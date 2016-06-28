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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.Permission;

public class AuthorizationServiceImpl implements AuthorizationService
{

    @Override
    public boolean isPermitted(Permission permission)
        throws KapuaException
    {
        Subject subject = SecurityUtils.getSubject();
        return subject.isPermitted(permission.toString());
    }

    @Override
    public void checkPermission(Permission permission)
        throws KapuaException
    {
        Subject subject = SecurityUtils.getSubject();
        subject.checkPermission(permission.toString());
    }
}
