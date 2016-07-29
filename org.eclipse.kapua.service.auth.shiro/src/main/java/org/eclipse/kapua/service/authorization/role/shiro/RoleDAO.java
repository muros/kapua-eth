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
package org.eclipse.kapua.service.authorization.role.shiro;

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RoleListResult;

public class RoleDAO extends ServiceDAO
{
    public static Role create(EntityManager em, RoleCreator creator)
        throws KapuaException
    {
        Role role = new RoleImpl(creator.getScopeId());

        role.setName(creator.getName());
        role.setPermissions(creator.getPermissions());

        return ServiceDAO.create(em, role);
    }

    public static Role find(EntityManager em, KapuaId roleId)
    {
        return em.find(RoleImpl.class, roleId);
    }

    public static void delete(EntityManager em, KapuaId roleId)
    {
        ServiceDAO.delete(em, RoleImpl.class, roleId);
    }

    public static RoleListResult query(EntityManager em, KapuaQuery<Role> roleQuery)
        throws KapuaException
    {
        return ServiceDAO.query(em, Role.class, RoleImpl.class, new RoleListResultImpl(), roleQuery);
    }

    public static long count(EntityManager em, KapuaQuery<Role> roleQuery)
        throws KapuaException
    {
        return ServiceDAO.count(em, Role.class, RoleImpl.class, roleQuery);
    }

}
