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
package org.eclipse.kapua.service.authorization.role;

import java.util.Set;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;

public interface RoleService extends KapuaEntityService<Role, RoleCreator>
{
    public Role create(RoleCreator creator)
        throws KapuaException;

    public Role find(KapuaId accountId, KapuaId entityId)
        throws KapuaException;

    public RoleListResult query(KapuaQuery<Role> query)
        throws KapuaException;

    public long count(KapuaQuery<Role> query)
        throws KapuaException;

    public void delete(Role entity)
        throws KapuaException;

    public RoleListResult merge(Set<RoleCreator> newPermissions)
        throws KapuaException;
}
