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

import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.JpaUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.permission.UserPermission;
import org.eclipse.kapua.service.authorization.permission.UserPermissionCreator;
import org.eclipse.kapua.service.authorization.permission.UserPermissionListResult;
import org.eclipse.kapua.service.authorization.permission.UserPermissionService;

public class UserPermissionServiceImpl implements UserPermissionService
{

    @Override
    public UserPermission create(UserPermissionCreator userPermissionCreator)
        throws KapuaException
    {
        ArgumentValidator.notNull(userPermissionCreator, "userPermissionCreator");
        ArgumentValidator.notNull(userPermissionCreator.getDomain(), "userPermissionCreator.domain");

        // TODO: check access ??

        UserPermission permission = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            JpaUtils.beginTransaction(em);

            permission = UserPermissionDAO.create(em, userPermissionCreator);

            JpaUtils.commit(em);
        }
        catch (Exception e) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return permission;
    }

    @Override
    public void delete(UserPermission permission)
        throws KapuaException
    {
        ArgumentValidator.notNull(permission, "permission");

        // TODO: check access ??

        EntityManager em = JpaUtils.getEntityManager();
        try {
            KapuaId permissionId = permission.getId();

            if (UserPermissionDAO.find(em, permissionId) == null) {
                throw new KapuaEntityNotFoundException(UserPermission.TYPE, permissionId);
            }

            JpaUtils.beginTransaction(em);
            UserPermissionDAO.delete(em, permissionId);
            JpaUtils.commit(em);
        }
        catch (KapuaException e) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }
    }

    @Override
    public UserPermission find(KapuaId accountId, KapuaId permissionId)
        throws KapuaException
    {
        ArgumentValidator.notNull(accountId, "accountId");
        ArgumentValidator.notNull(permissionId, "permissionId");

        // TODO: check access ??

        UserPermission permission = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            permission = UserPermissionDAO.find(em, permissionId);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }
        return permission;
    }

    @Override
    public UserPermissionListResult query(KapuaQuery<UserPermission> query)
        throws KapuaException
    {
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        // TODO: check access ??

        UserPermissionListResult result = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            result = UserPermissionDAO.query(em, query);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return result;
    }

    @Override
    public long count(KapuaQuery<UserPermission> query)
        throws KapuaException
    {
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        // TODO: check access ??

        long count = 0;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            count = UserPermissionDAO.count(em, query);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return count;
    }

    @Override
    public UserPermissionListResult merge(Set<UserPermissionCreator> newPermissions)
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
