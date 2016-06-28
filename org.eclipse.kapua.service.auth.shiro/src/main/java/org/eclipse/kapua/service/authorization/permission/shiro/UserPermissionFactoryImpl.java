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
package org.eclipse.kapua.service.authorization.permission.shiro;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.permission.UserPermissionCreator;
import org.eclipse.kapua.service.authorization.permission.UserPermissionFactory;
import org.eclipse.kapua.service.authorization.permission.UserPermissionQuery;

public class UserPermissionFactoryImpl implements UserPermissionFactory
{

    @Override
    public UserPermissionCreator newCreator(KapuaId scopeId)
    {
        return new UserPermissionCreatorImpl(scopeId);
    }

    @Override
    public UserPermissionQuery newQuery(KapuaId scopeId)
    {
        return new UserPermissionQueryImpl(scopeId);
    }

}
