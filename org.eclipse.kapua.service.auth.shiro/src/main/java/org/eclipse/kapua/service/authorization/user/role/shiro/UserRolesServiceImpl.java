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

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.JpaUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.shiro.RoleDomain;
import org.eclipse.kapua.service.authorization.user.role.UserRoles;
import org.eclipse.kapua.service.authorization.user.role.UserRolesCreator;
import org.eclipse.kapua.service.authorization.user.role.UserRolesListResult;
import org.eclipse.kapua.service.authorization.user.role.UserRolesService;

public class UserRolesServiceImpl implements UserRolesService
{

    @Override
    public UserRoles create(UserRolesCreator userRoleCreator)
        throws KapuaException
    {
        ArgumentValidator.notNull(userRoleCreator, "userRoleCreator");
        ArgumentValidator.notNull(userRoleCreator.getUserId(), "userRoleCreator.userId");
        ArgumentValidator.notEmptyOrNull(userRoleCreator.getRoles(), "userRoleCreator.roles");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(RoleDomain.ROLE, Actions.write, userRoleCreator.getScopeId()));

        //
        // Do create
        UserRoles userRole = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            JpaUtils.beginTransaction(em);

            userRole = UserRolesDAO.create(em, userRoleCreator);

            JpaUtils.commit(em);
        }
        catch (Exception e) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return userRole;
    }

    @Override
    public void delete(UserRoles userRole)
        throws KapuaException
    {
        ArgumentValidator.notNull(userRole, "userRole");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(RoleDomain.ROLE, Actions.delete, userRole.getScopeId()));

        //
        // Do delete
        EntityManager em = JpaUtils.getEntityManager();
        try {
            KapuaId userRoleId = userRole.getId();

            if (UserRolesDAO.find(em, userRoleId) == null) {
                throw new KapuaEntityNotFoundException(Role.TYPE, userRoleId);
            }

            JpaUtils.beginTransaction(em);
            UserRolesDAO.delete(em, userRoleId);
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
    public UserRoles find(KapuaId scopeId, KapuaId userRoleId)
        throws KapuaException
    {
        ArgumentValidator.notNull(scopeId, "accountId");
        ArgumentValidator.notNull(userRoleId, "userRoleId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(RoleDomain.ROLE, Actions.read, scopeId));

        //
        // Do find
        UserRoles userRole = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            userRole = UserRolesDAO.find(em, userRoleId);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }
        return userRole;
    }

    @Override
    public UserRolesListResult query(KapuaQuery<UserRoles> query)
        throws KapuaException
    {
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(RoleDomain.ROLE, Actions.read, query.getScopeId()));

        //
        // Do query
        UserRolesListResult result = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            result = UserRolesDAO.query(em, query);
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
    public long count(KapuaQuery<UserRoles> query)
        throws KapuaException
    {
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(RoleDomain.ROLE, Actions.read, query.getScopeId()));

        //
        // Do count
        long count = 0;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            count = UserRolesDAO.count(em, query);
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
    public UserRolesListResult merge(Set<UserRolesCreator> newPermissions)
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
