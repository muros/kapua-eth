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
package org.eclipse.kapua.service.user.internal;

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.JpaUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserService;

/**
 * UserServiceImpl implements the Kapua User Service.
 */
public class UserServiceImpl implements UserService
{
    @Override
    public User create(UserCreator userCreator)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(userCreator.getScopeId().getId(), "scopeId");
        ArgumentValidator.notEmptyOrNull(userCreator.getName(), "name");
//        ArgumentValidator.notEmptyOrNull(userCreator.getRawPassword(), "rawPassword");
        ArgumentValidator.match(userCreator.getName(), ArgumentValidator.NAME_REGEXP, "name");
//        ArgumentValidator.match(userCreator.getRawPassword(), ArgumentValidator.PASSWORD_REGEXP, "rawPassword");
        ArgumentValidator.match(userCreator.getEmail(), ArgumentValidator.EMAIL_REGEXP, "email");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(UserDomain.USER, Actions.write, userCreator.getScopeId()));

        //
        // Do create
        User user = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {

            JpaUtils.beginTransaction(em);

            user = UserDAO.create(em, userCreator);

            user = UserDAO.find(em, user.getId());
            JpaUtils.commit(em);

        }
        catch (Exception pe) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(pe);
        }
        finally {
            JpaUtils.close(em);
        }

        return user;
    }

    @Override
    public User update(User user)
        throws KapuaException
    {
        // Validation of the fields
        ArgumentValidator.notNull(user.getId().getId(), "id");
        ArgumentValidator.notNull(user.getScopeId().getId(), "accountId");
        ArgumentValidator.notEmptyOrNull(user.getName(), "name");
        ArgumentValidator.match(user.getName(), ArgumentValidator.NAME_REGEXP, "name");
        // ArgumentValidator.match(user.getRawPassword(), ArgumentValidator.PASSWORD_REGEXP, "rawPassword");
        ArgumentValidator.match(user.getEmail(), ArgumentValidator.EMAIL_REGEXP, "email");
        validateSystemUser(user.getName());

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(UserDomain.USER, Actions.write, user.getScopeId()));

        //
        // Do update
        User userUpdated = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {

            User currentUser = UserDAO.find(em, user.getId());
            if (currentUser == null) {
                throw new KapuaEntityNotFoundException(User.TYPE, user.getId());
            }

            JpaUtils.beginTransaction(em);
            UserDAO.update(em, user);
            JpaUtils.commit(em);

            userUpdated = UserDAO.find(em, user.getId());
        }
        catch (Exception pe) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(pe);
        }
        finally {
            JpaUtils.close(em);
        }

        return userUpdated;
    }

    @Override
    public void delete(User user)
        throws KapuaException
    {
        // Validation of the fields
        ArgumentValidator.notNull(user, "user");
        ArgumentValidator.notNull(user.getId().getId(), "id");
        ArgumentValidator.notNull(user.getScopeId().getId(), "accountId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(UserDomain.USER, Actions.write, user.getScopeId()));

        // Do the delete
        EntityManager em = JpaUtils.getEntityManager();
        try {
            KapuaId userId = user.getId();

            // Entity needs to be loaded in the context of the same EntityManger to be able to delete it afterwards
            User userx = UserDAO.find(em, userId);
            if (userx == null) {
                throw new KapuaEntityNotFoundException(User.TYPE, userId);
            }
            validateSystemUser(user.getName());

            // Ensure this is not the last admin for the account
            // FIXME-KAPUA: Ask the Authorization Service
            // UserDAO.checkForLastAccountAdministratorDelete(em, userx);

            JpaUtils.beginTransaction(em);
            UserDAO.delete(em, userId);
            JpaUtils.commit(em);
        }
        catch (Exception pe) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(pe);
        }
        finally {
            JpaUtils.close(em);
        }
    }

    @Override
    public User find(KapuaId accountId, KapuaId userId)
        throws KapuaException
    {
        // Validation of the fields
        ArgumentValidator.notNull(accountId.getId(), "accountId");
        ArgumentValidator.notNull(userId.getId(), "id");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(UserDomain.USER, Actions.read, accountId));

        // Do the find
        User user = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            user = UserDAO.find(em, userId);
        }
        catch (Exception pe) {
            throw JpaUtils.toKapuaException(pe);
        }
        finally {
            JpaUtils.close(em);
        }

        return user;
    }

    @Override
    public User findByName(String name)
        throws KapuaException
    {
        // Validation of the fields
        ArgumentValidator.notEmptyOrNull(name, "name");

        // Do the find
        User user = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            user = UserDAO.findByName(em, name);
        }
        catch (Exception pe) {
            throw JpaUtils.toKapuaException(pe);
        }
        finally {
            JpaUtils.close(em);
        }

        //
        // Check Access
        if (user != null) {
            KapuaLocator locator = KapuaLocator.getInstance();
            AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
            PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
            authorizationService.checkPermission(permissionFactory.newPermission(UserDomain.USER, Actions.read, user.getScopeId()));
        }

        return user;
    }

    @Override
    public UserListResult query(KapuaQuery<User> query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(UserDomain.USER, Actions.read, query.getScopeId()));

        //
        // Do count
        UserListResult result = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            result = UserDAO.query(em, query);
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
    public long count(KapuaQuery<User> query)
        throws KapuaException
    {
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(UserDomain.USER, Actions.read, query.getScopeId()));

        //
        // Do count
        long count = 0;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            count = UserDAO.count(em, query);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return count;
    }

    // -----------------------------------------------------------------------------------------
    //
    // Private Methods
    //
    // -----------------------------------------------------------------------------------------

    private void validateSystemUser(String name)
        throws KapuaException
    {
        // FIXME-KAPUA: AuthenticationService get system user name via config
        if ("kapua-sys".equals(name)) {
            throw new KapuaIllegalArgumentException("name", "kapua-sys");
        }
    }

}
