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
package org.eclipse.kapua.service.authorization.user.role;

import java.util.Set;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;

public interface UserRolesService extends KapuaEntityService<UserRoles, UserRolesCreator>
{
    public UserRoles create(UserRolesCreator creator)
        throws KapuaException;

    public UserRoles find(KapuaId accountId, KapuaId entityId)
        throws KapuaException;

    public UserRolesListResult query(KapuaQuery<UserRoles> query)
        throws KapuaException;

    public long count(KapuaQuery<UserRoles> query)
        throws KapuaException;

    public void delete(UserRoles entity)
        throws KapuaException;

    public UserRolesListResult merge(Set<UserRolesCreator> newPermissions)
        throws KapuaException;
}
