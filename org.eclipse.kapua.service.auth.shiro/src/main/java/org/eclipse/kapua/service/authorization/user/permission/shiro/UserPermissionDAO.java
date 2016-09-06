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
package org.eclipse.kapua.service.authorization.user.permission.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.user.permission.UserPermission;
import org.eclipse.kapua.service.authorization.user.permission.UserPermissionCreator;
import org.eclipse.kapua.service.authorization.user.permission.UserPermissionListResult;

public class UserPermissionDAO extends ServiceDAO
{
    public static UserPermission create(EntityManager em, UserPermissionCreator creator)
        throws KapuaException
    {
        UserPermission permission = new UserPermissionImpl(creator.getScopeId());

        permission.setUserId(creator.getUserId());
        permission.setPermission(creator.getPermission());

        return ServiceDAO.create(em, permission);
    }

    public static UserPermission find(EntityManager em, KapuaId permissionId)
    {
        return em.find(UserPermissionImpl.class, permissionId);
    }

    public static void delete(EntityManager em, KapuaId permissionId)
    {
        ServiceDAO.delete(em, UserPermissionImpl.class, permissionId);
    }

    public static UserPermissionListResult query(EntityManager em, KapuaQuery<UserPermission> userPermissionQuery)
        throws KapuaException
    {
        return ServiceDAO.query(em, UserPermission.class, UserPermissionImpl.class, new UserPermissionListResultImpl(), userPermissionQuery);
    }

    public static long count(EntityManager em, KapuaQuery<UserPermission> userPermissionQuery)
        throws KapuaException
    {
        return ServiceDAO.count(em, UserPermission.class, UserPermissionImpl.class, userPermissionQuery);
    }

}
