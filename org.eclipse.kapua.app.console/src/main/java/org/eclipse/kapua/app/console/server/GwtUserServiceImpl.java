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
package org.eclipse.kapua.app.console.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.kapua.app.console.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.shared.GwtKapuaException;
import org.eclipse.kapua.app.console.shared.model.GwtUser;
import org.eclipse.kapua.app.console.shared.model.GwtUserCreator;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.shared.util.KapuaGwtConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.CredentialType;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.user.permission.UserPermissionCreator;
import org.eclipse.kapua.service.authorization.user.permission.UserPermissionFactory;
import org.eclipse.kapua.service.authorization.user.permission.UserPermissionService;
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
        throws GwtKapuaException
    {
        checkXSRFToken(xsrfToken);

        GwtUser gwtUser = null;
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            UserFactory userFactory = locator.getFactory(UserFactory.class);

            KapuaId scopeId = KapuaEid.parseShortId(gwtUserCreator.getScopeId());
            UserCreator userCreator = userFactory.newCreator(scopeId,
                                                             gwtUserCreator.getUsername());
            userCreator.setDisplayName(gwtUserCreator.getDisplayName());
            userCreator.setEmail(gwtUserCreator.getEmail());
            userCreator.setPhoneNumber(gwtUserCreator.getPhoneNumber());

            //
            // Create the User
            UserService userService = locator.getService(UserService.class);
            User user = userService.create(userCreator);

            //
            // Create permissions
            Set<String> permissions = new HashSet<String>();
            if (gwtUserCreator.getPermissions() != null) {
                // build the set of permissions
                permissions.addAll(Arrays.asList(gwtUserCreator.getPermissions().split(",")));
            }

            UserPermissionService userPermissionService = locator.getService(UserPermissionService.class);
            UserPermissionFactory userPermissionFactory = locator.getFactory(UserPermissionFactory.class);
            PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);

            for (String p : permissions) {
                UserPermissionCreator userPermissionCreator = userPermissionFactory.newCreator(user.getScopeId());
                userPermissionCreator.setUserId(scopeId);

                String[] tokens = p.split(":");
                String domain = null;
                Actions action = null;
                KapuaId targetScopeId = null;
                if (tokens.length > 0) {
                    domain = tokens[0];
                }
                if (tokens.length > 1) {
                    action = Actions.valueOf(tokens[1]);
                }
                if (tokens.length > 2) {
                    targetScopeId = KapuaEid.parseShortId(tokens[2]);
                }

                Permission permission = permissionFactory.newPermission(domain, action, targetScopeId);
                userPermissionCreator.setPermission(permission);

                userPermissionService.create(userPermissionCreator);
            }

            //
            // Create credentials
            CredentialService credentialService = locator.getService(CredentialService.class);
            CredentialFactory credentialFactory = locator.getFactory(CredentialFactory.class);

            CredentialCreator credentialCreator = credentialFactory.newCreator(scopeId,
                                                                               user.getId(),
                                                                               CredentialType.PASSWORD,
                                                                               gwtUserCreator.getPassword());
            credentialService.create(credentialCreator);

            // convert to GwtAccount and return
            // reload the user as we want to load all its permissions
            gwtUser = KapuaGwtConverter.convert(userService.find(user.getScopeId(), user.getId()));
        }
        catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtUser;
    }

    public GwtUser update(GwtXSRFToken xsrfToken, GwtUser gwtUser)
        throws GwtKapuaException
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

                //
                // Update user
                user.setName(gwtUser.getUnescapedUsername());
                user.setDisplayName(gwtUser.getUnescapedDisplayName());
                user.setEmail(gwtUser.getUnescapedEmail());
                user.setPhoneNumber(gwtUser.getUnescapedPhoneNumber());

                // status
                user.setStatus(UserStatus.valueOf(gwtUser.getStatus()));

                //
                // Update permissions
                Set<String> newPermissions = new HashSet<String>();
                if (gwtUser.getPermissions() != null) {
                    // build the set of permissions
                    newPermissions.addAll(Arrays.asList(gwtUser.getPermissions().split(",")));
                }

                UserPermissionService userPermissionService = locator.getService(UserPermissionService.class);
                UserPermissionFactory userPermissionFactory = locator.getFactory(UserPermissionFactory.class);
                PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);

                Set<UserPermissionCreator> newUserPermissions = new HashSet<UserPermissionCreator>();
                for (String p : newPermissions) {
                    UserPermissionCreator userPermissionCreator = userPermissionFactory.newCreator(user.getScopeId());
                    userPermissionCreator.setUserId(scopeId);

                    String[] tokens = p.split(":");
                    String domain = null;
                    Actions action = null;
                    KapuaId targetScopeId = null;
                    if (tokens.length > 0) {
                        domain = tokens[0];
                    }
                    if (tokens.length > 1) {
                        action = Actions.valueOf(tokens[1]);
                    }
                    if (tokens.length > 2) {
                        targetScopeId = KapuaEid.parseShortId(tokens[2]);
                    }

                    Permission permission = permissionFactory.newPermission(domain, action, targetScopeId);
                    userPermissionCreator.setPermission(permission);

                    userPermissionService.create(userPermissionCreator);
                }

                userPermissionService.merge(newUserPermissions);

                //
                // Update credentials
                if (gwtUser.getPassword() != null) {
                    CredentialService credentialService = locator.getService(CredentialService.class);
                    CredentialFactory credentialFactory = locator.getFactory(CredentialFactory.class);

                    CredentialListResult credentials = credentialService.findByUserId(scopeId, userId);
                    if (!credentials.isEmpty()) {
                        //
                        // Delete old PASSWORD credential
                        Credential oldCredential = null;
                        for (Credential c : credentials.getItems()) {
                            if (CredentialType.PASSWORD.equals(c.getCredentialType())) {
                                oldCredential = c;
                                break;
                            }
                        }
                        credentialService.delete(oldCredential.getScopeId(), oldCredential.getId());

                        //
                        // Create new PASSWORD credential
                        CredentialCreator credentialCreator = credentialFactory.newCreator(scopeId,
                                                                                           user.getId(),
                                                                                           CredentialType.PASSWORD,
                                                                                           gwtUser.getPassword());

                        credentialService.create(credentialCreator);
                    }
                }

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
            KapuaExceptionHandler.handle(t);
        }
        return gwtUserUpdated;
    }

    public void delete(GwtXSRFToken xsrfToken, String accountId, GwtUser gwtUser)
        throws GwtKapuaException
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
            KapuaExceptionHandler.handle(t);
        }
    }

    public GwtUser find(String accountId, String userIdString)
        throws GwtKapuaException
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
            KapuaExceptionHandler.handle(t);
        }

        return gwtUser;
    }

    public ListLoadResult<GwtUser> findAll(String scopeIdString)
        throws GwtKapuaException
    {
        KapuaId scopeId = KapuaEid.parseShortId(scopeIdString);
        List<GwtUser> gwtUserList = new ArrayList<GwtUser>();
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            UserService userService = locator.getService(UserService.class);
            UserFactory userFactory = locator.getFactory(UserFactory.class);
            UserQuery query = userFactory.newQuery(scopeId);
            UserListResult list = userService.query(query);

            for (User user : list.getItems()) {
                gwtUserList.add(KapuaGwtConverter.convert(user));
            }
        }
        catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BaseListLoadResult<GwtUser>(gwtUserList);
    }
}
