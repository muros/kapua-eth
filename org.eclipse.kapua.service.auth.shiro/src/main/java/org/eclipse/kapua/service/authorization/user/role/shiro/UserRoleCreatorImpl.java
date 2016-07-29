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

import java.util.Set;

import org.eclipse.kapua.commons.model.AbstractKapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.user.role.UserRole;
import org.eclipse.kapua.service.authorization.user.role.UserRoleCreator;

public class UserRoleCreatorImpl extends AbstractKapuaEntityCreator<UserRole> implements UserRoleCreator
{
    private static final long serialVersionUID = 972154225756734130L;

    private KapuaId           userId;
    private Set<KapuaId>      roles;

    public UserRoleCreatorImpl(KapuaId scopeId)
    {
        super(scopeId);
    }

    @Override
    public void setUserId(KapuaId userId)
    {
        this.userId = userId;
    }

    @Override
    public KapuaId getUserId()
    {
        return userId;
    }

    @Override
    public void setRoles(Set<KapuaId> roles)
    {
        this.roles = roles;
    }

    @Override
    public Set<KapuaId> getRoles()
    {
        return roles;
    }

}
