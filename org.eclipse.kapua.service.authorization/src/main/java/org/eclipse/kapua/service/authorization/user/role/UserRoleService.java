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
package org.eclipse.kapua.service.authorization.user.role;

import java.util.Set;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;

public interface UserRoleService extends KapuaEntityService<UserRole, UserRoleCreator>
{
    public UserRole create(UserRoleCreator creator)
        throws KapuaException;

    public UserRole find(KapuaId accountId, KapuaId entityId)
        throws KapuaException;

    public UserRoleListResult query(KapuaQuery<UserRole> query)
        throws KapuaException;

    public long count(KapuaQuery<UserRole> query)
        throws KapuaException;

    public void delete(UserRole entity)
        throws KapuaException;

    public UserRoleListResult merge(Set<UserRoleCreator> newPermissions)
        throws KapuaException;
}
