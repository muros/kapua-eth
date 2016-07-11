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
package org.eclipse.kapua.service.authorization.permission;

import java.util.Set;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;

public interface UserPermissionService extends KapuaEntityService<UserPermission, UserPermissionCreator>
{
    public UserPermission create(UserPermissionCreator creator)
        throws KapuaException;

    public UserPermission find(KapuaId accountId, KapuaId entityId)
        throws KapuaException;

    public UserPermissionListResult query(KapuaQuery<UserPermission> query)
        throws KapuaException;

    public long count(KapuaQuery<UserPermission> query)
        throws KapuaException;

    public void delete(UserPermission entity)
        throws KapuaException;

    public UserPermissionListResult merge(Set<UserPermissionCreator> newPermissions)
        throws KapuaException;
}
