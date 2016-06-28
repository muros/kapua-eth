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
package org.eclipse.kapua.app.console.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.kapua.app.console.server.util.EdcExceptionHandler;
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtUser;
import org.eclipse.kapua.app.console.shared.model.GwtUserCreator;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.shared.util.KapuaGwtConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.permission.UserPermissionCreator;
import org.eclipse.kapua.service.authorization.permission.UserPermissionFactory;
import org.eclipse.kapua.service.authorization.permission.UserPermissionService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserStatus;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;

/**
 * The server side implementation of the RPC service.
 */
public class GwtUserServiceImpl extends KapuaRemoteServiceServlet implements GwtUserService
{
    private static final long serialVersionUID = 7430961652373364113L;

    public GwtUser create(GwtXSRFToken xsrfToken, GwtUserCreator gwtUserCreator)
        throws GwtEdcException
    {
        checkXSRFToken(xsrfToken);

        GwtUser gwtUser = null;
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            UserFactory userFactory = locator.getFactory(UserFactory.class);

            KapuaId scopeId = KapuaEid.parseShortId(gwtUserCreator.getScopeId());
            UserCreator userCreator = userFactory.newCreator(scopeId,
                                                             gwtUserCreator.getUsername());
            userCreator.setPassword(gwtUserCreator.getPassword());
            userCreator.setDisplayName(gwtUserCreator.getDisplayName());
            userCreator.setEmail(gwtUserCreator.getEmail());
            userCreator.setPhoneNumber(gwtUserCreator.getPhoneNumber());

            // create the User
            UserService userService = locator.getService(UserService.class);
            User user = userService.create(userCreator);

            // set permissions
            Set<String> permissions = new HashSet<String>();
            if (gwtUserCreator.getPermissions() != null) {
                // build the set of permissions
                permissions.addAll(Arrays.asList(gwtUserCreator.getPermissions().split(",")));
            }

            UserPermissionService userPermissionService = locator.getService(UserPermissionService.class);
            UserPermissionFactory userPermissionFactory = locator.getFactory(UserPermissionFactory.class);

            for (String p : permissions) {
                UserPermissionCreator userPermissionCreator = userPermissionFactory.newCreator(user.getScopeId());
                userPermissionCreator.setUserId(scopeId);

                String[] tokens = p.split(":");
                if (tokens.length == 1) {
                    userPermissionCreator.setDomain(tokens[0]);
                }
                if (tokens.length == 2) {
                    userPermissionCreator.setAction(tokens[1]);
                }
                if (tokens.length == 3) {
                    userPermissionCreator.setTargetScopeId(KapuaEid.parseShortId(tokens[2]));
                }

                userPermissionService.create(userPermissionCreator);
            }

            // convert to GwtAccount and return
            // reload the user as we want to load all its permissions
            gwtUser = KapuaGwtConverter.convert(userService.find(user.getScopeId(), user.getId()));
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }

        return gwtUser;
    }

    public GwtUser update(GwtXSRFToken xsrfToken, GwtUser gwtUser)
        throws GwtEdcException
    {
        checkXSRFToken(xsrfToken);

        GwtUser gwtUserUpdated = null;
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            UserService userService = locator.getService(UserService.class);

            KapuaId scopeId = KapuaEid.parseShortId(gwtUser.getScopeId());
            KapuaId userId = KapuaEid.parseShortId(gwtUser.getId());

            User user = userService.find(scopeId, userId);

            if (user != null) {

                user.setName(gwtUser.getUnescapedUsername());
                user.setDisplayName(gwtUser.getUnescapedDisplayName());
                user.setEmail(gwtUser.getUnescapedEmail());
                user.setPhoneNumber(gwtUser.getUnescapedPhoneNumber());
                if (gwtUser.getPassword() != null) {
                    // do not reset the password unless a new value is supplied
                    user.setRawPassword(gwtUser.getUnescapedPassword());
                }

                // status
                user.setStatus(UserStatus.valueOf(gwtUser.getStatus()));

                // set permissions
                Set<String> newPermissions = new HashSet<String>();
                if (gwtUser.getPermissions() != null) {
                    // build the set of permissions
                    newPermissions.addAll(Arrays.asList(gwtUser.getPermissions().split(",")));
                }

                UserPermissionService userPermissionService = locator.getService(UserPermissionService.class);
                UserPermissionFactory userPermissionFactory = locator.getFactory(UserPermissionFactory.class);

                Set<UserPermissionCreator> newUserPermissions = new HashSet<UserPermissionCreator>();
                for (String p : newPermissions) {
                    String[] tokens = p.split(":");

                    UserPermissionCreator userPermissionCreator = userPermissionFactory.newCreator(user.getScopeId());
                    userPermissionCreator.setUserId(scopeId);

                    if (tokens.length == 1) {
                        userPermissionCreator.setDomain(tokens[0]);
                    }
                    if (tokens.length == 2) {
                        userPermissionCreator.setAction(tokens[1]);
                    }
                    if (tokens.length == 3) {
                        userPermissionCreator.setTargetScopeId(KapuaEid.parseShortId(tokens[2]));
                    }

                    newUserPermissions.add(userPermissionCreator);
                }

                userPermissionService.merge(newUserPermissions);

                // optlock
                user.setOptlock(gwtUser.getOptlock());

                // update the user
                userService.update(user);

                //
                // convert to GwtAccount and return
                // reload the user as we want to load all its permissions
                gwtUserUpdated = KapuaGwtConverter.convert(userService.find(user.getScopeId(), user.getId()));
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return gwtUserUpdated;
    }

    public void delete(GwtXSRFToken xsrfToken, String accountId, GwtUser gwtUser)
        throws GwtEdcException
    {
        checkXSRFToken(xsrfToken);

        KapuaId scopeId = KapuaEid.parseShortId(accountId);

        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            UserService userService = locator.getService(UserService.class);
            User user = userService.find(scopeId, KapuaEid.parseShortId(gwtUser.getId()));
            if (user != null) {
                userService.delete(user);
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }

    public GwtUser find(String accountId, String userIdString)
        throws GwtEdcException
    {
        KapuaId scopeId = KapuaEid.parseShortId(accountId);
        KapuaId userId = KapuaEid.parseShortId(userIdString);

        GwtUser gwtUser = null;
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            UserService userService = locator.getService(UserService.class);
            User user = userService.find(scopeId, userId);
            if (user != null) {
                gwtUser = KapuaGwtConverter.convert(user);
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }

        return gwtUser;
    }

    public ListLoadResult<GwtUser> findAll(String scopeIdString)
        throws GwtEdcException
    {
        KapuaId scopeId = KapuaEid.parseShortId(scopeIdString);
        List<GwtUser> gwtUserList = new ArrayList<GwtUser>();
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            UserService userService = locator.getService(UserService.class);
            UserFactory userFactory = locator.getFactory(UserFactory.class);
            UserQuery query = userFactory.newQuery(scopeId);
            UserListResult list = userService.query(query);

            for (User user : list) {
                gwtUserList.add(KapuaGwtConverter.convert(user));
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }

        return new BaseListLoadResult<GwtUser>(gwtUserList);
    }
}
