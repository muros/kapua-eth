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
package org.eclipse.kapua.service.authorization.user.role.shiro;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.user.role.UserRoleCreator;
import org.eclipse.kapua.service.authorization.user.role.UserRoleFactory;
import org.eclipse.kapua.service.authorization.user.role.UserRoleQuery;

public class UserRoleFactoryImpl implements UserRoleFactory
{

    @Override
    public UserRoleCreator newCreator(KapuaId scopeId)
    {
        return new UserRoleCreatorImpl(scopeId);
    }

    @Override
    public UserRoleQuery newQuery(KapuaId scopeId)
    {
        return new UserRoleQueryImpl(scopeId);
    }

}
