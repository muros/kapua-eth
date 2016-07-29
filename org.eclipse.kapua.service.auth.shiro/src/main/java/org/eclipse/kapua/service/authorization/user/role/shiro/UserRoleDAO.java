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

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.user.role.UserRole;
import org.eclipse.kapua.service.authorization.user.role.UserRoleCreator;
import org.eclipse.kapua.service.authorization.user.role.UserRoleListResult;

public class UserRoleDAO extends ServiceDAO
{
    public static UserRole create(EntityManager em, UserRoleCreator creator)
        throws KapuaException
    {
        UserRole userRole = new UserRoleImpl(creator.getScopeId());

        userRole.setUserId(creator.getUserId());
        userRole.setRolesIds(creator.getRoles());

        return ServiceDAO.create(em, userRole);
    }

    public static UserRole find(EntityManager em, KapuaId userRoleId)
    {
        return em.find(UserRoleImpl.class, userRoleId);
    }

    public static void delete(EntityManager em, KapuaId userRoleId)
    {
        ServiceDAO.delete(em, UserRoleImpl.class, userRoleId);
    }

    public static UserRoleListResult query(EntityManager em, KapuaQuery<UserRole> userRoleQuery)
        throws KapuaException
    {
        return ServiceDAO.query(em, UserRole.class, UserRoleImpl.class, new UserRoleListResultImpl(), userRoleQuery);
    }

    public static long count(EntityManager em, KapuaQuery<UserRole> userRoleQuery)
        throws KapuaException
    {
        return ServiceDAO.count(em, UserRole.class, UserRoleImpl.class, userRoleQuery);
    }

}
