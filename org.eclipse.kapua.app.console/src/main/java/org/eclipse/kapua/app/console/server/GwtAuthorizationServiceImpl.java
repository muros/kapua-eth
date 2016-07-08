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

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.config.ConsoleConfig;
import org.eclipse.kapua.app.console.config.ConsoleConfigKeys;
import org.eclipse.kapua.app.console.server.util.EdcExceptionHandler;
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.model.GwtUser;
import org.eclipse.kapua.app.console.shared.service.GwtAuthorizationService;
import org.eclipse.kapua.app.console.shared.util.KapuaGwtConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.setting.KapuaEnvironmentConfig;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.AccessToken;
import org.eclipse.kapua.service.authentication.AuthenticationCredentials;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.KapuaSession;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.Permission;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GwtAuthorizationServiceImpl extends KapuaRemoteServiceServlet implements GwtAuthorizationService
{
    private static final long   serialVersionUID     = -3919578632016541047L;

    private static final Logger s_logger             = LoggerFactory.getLogger(GwtAuthorizationServiceImpl.class);

    public static final String  SESSION_CURRENT      = "console.current.session";
    public static final String  SESSION_CURRENT_USER = "console.current.user";

    /**
     * Login call in response to the login dialog.
     */
    public GwtSession login(GwtUser tmpUser)
        throws GwtEdcException
    {
        // VIP
        // keep this here to make sure we initialize the logger.
        // Without the following, console logger may not log anything when deployed into tomcat.
        s_logger.info(">>> THIS IS INFO <<<");
        s_logger.warn(">>> THIS IS WARN <<<");
        s_logger.debug(">>> THIS IS DEBUG <<<");

        GwtSession gwtSession = null;
        try {
            // Get the user
            KapuaLocator locator = KapuaLocator.getInstance();
            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
            UsernamePasswordTokenFactory credentialsFactory = locator.getFactory(UsernamePasswordTokenFactory.class);
            AuthenticationCredentials credentials = credentialsFactory.newInstance(tmpUser.getUsername(),
                                                                                   tmpUser.getPassword().toCharArray());

            // Login
            authenticationService.login(credentials);

            // Get the session infos
            gwtSession = establishSession();
        }
        catch (Throwable t) {
            logout();
            EdcExceptionHandler.handle(t);
        }
        return gwtSession;
    }

    /**
     * Return the currently authenticated user or null if no session has been established.
     */
    public GwtSession getCurrentSession()
        throws GwtEdcException
    {
        GwtSession gwtSession = null;
        try {
            Subject currentUser = SecurityUtils.getSubject();
            if (currentUser != null && currentUser.isAuthenticated()) {

                Session session = currentUser.getSession();
                gwtSession = (GwtSession) session.getAttribute(SESSION_CURRENT);

                // Store the user information in the sessions
                String username = (String) currentUser.getPrincipal();

                KapuaLocator locator = KapuaLocator.getInstance();
                UserService userService = locator.getService(UserService.class);
                User user = userService.findByName(username);

                // get the session
                if (gwtSession == null) {
                    gwtSession = establishSession();
                }
                else {
                    gwtSession.setGwtUser(KapuaGwtConverter.convert(user));
                }
            }
        }
        catch (Throwable t) {
            s_logger.warn("Error in getCurrentSession.", t);
            EdcExceptionHandler.handle(t);
        }

        return gwtSession;
    }

    private GwtSession establishSession()
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();

        //
        // Get info from session
        AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
        // KapuaSession kapuaSession = authenticationService.getCurrentSession();
        KapuaSession kapuaSession = new KapuaSession() {

            @Override
            public AccessToken getTokenInfo()
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public KapuaId getScopeId()
            {
                return new KapuaEid(new BigInteger("1"));
            }

            @Override
            public String getCurrentUsername()
            {
                return "kapua-sys";
            }

            @Override
            public KapuaId getCurrentUserId()
            {
                return new KapuaEid(new BigInteger("1"));
            }
        };

        //
        // Get user info
        UserService userService = locator.getService(UserService.class);
        User user = userService.find(kapuaSession.getScopeId(),
                                     kapuaSession.getCurrentUserId());

        //
        // Get permission info
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);

        boolean hasAccountCreate = authorizationService.isPermitted(permissionFactory.newPermission("account", "create", kapuaSession.getScopeId()));
        boolean hasAccountRead = authorizationService.isPermitted(permissionFactory.newPermission("account", "read", kapuaSession.getScopeId()));
        boolean hasAccountUpdate = authorizationService.isPermitted(permissionFactory.newPermission("account", "update", kapuaSession.getScopeId()));
        boolean hasAccountDelete = authorizationService.isPermitted(permissionFactory.newPermission("account", "delete", kapuaSession.getScopeId()));
        boolean hasAccountAll = authorizationService.isPermitted(permissionFactory.newPermission("account", null, null));

        boolean hasDeviceCreate = authorizationService.isPermitted(permissionFactory.newPermission("device", "create", kapuaSession.getScopeId()));
        boolean hasDeviceRead = authorizationService.isPermitted(permissionFactory.newPermission("device", "read", kapuaSession.getScopeId()));
        boolean hasDeviceUpdate = authorizationService.isPermitted(permissionFactory.newPermission("device", "update", kapuaSession.getScopeId()));
        boolean hasDeviceDelete = authorizationService.isPermitted(permissionFactory.newPermission("device", "delete", kapuaSession.getScopeId()));
        boolean hasDeviceManage = authorizationService.isPermitted(permissionFactory.newPermission("device", "manage", kapuaSession.getScopeId()));

        boolean hasDataRead = authorizationService.isPermitted(permissionFactory.newPermission("data", "read", kapuaSession.getScopeId()));

        boolean hasUserCreate = authorizationService.isPermitted(permissionFactory.newPermission("user", "create", kapuaSession.getScopeId()));
        boolean hasUserRead = authorizationService.isPermitted(permissionFactory.newPermission("user", "read", kapuaSession.getScopeId()));
        boolean hasUserUpdate = authorizationService.isPermitted(permissionFactory.newPermission("user", "update", kapuaSession.getScopeId()));
        boolean hasUserDelete = authorizationService.isPermitted(permissionFactory.newPermission("user", "delete", kapuaSession.getScopeId()));

        //
        // Get account info
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.find(kapuaSession.getScopeId());

        //
        // Convert entities
        GwtUser gwtUser = KapuaGwtConverter.convert(user);
        GwtAccount gwtAccount = KapuaGwtConverter.convert(account);

        //
        // Build the session
        GwtSession gwtSession = new GwtSession();

        // Console info
        KapuaEnvironmentConfig commonsConfig = KapuaEnvironmentConfig.getInstance();
        gwtSession.setVersion(commonsConfig.getString(SystemSettingKey.VERSION));
        gwtSession.setBuildVersion(commonsConfig.getString(SystemSettingKey.BUILD_VERSION));
        gwtSession.setBuildNumber(commonsConfig.getString(SystemSettingKey.BUILD_NUMBER));

        // Google Analytics in
        ConsoleConfig consoleConfig = ConsoleConfig.getInstance();
        gwtSession.setGoogleAnalyticsTrackingId(consoleConfig.getString(ConsoleConfigKeys.GOOGLE_ANALYTICS_TRACKING_ID));

        // User info
        gwtSession.setGwtUser(gwtUser);
        gwtSession.setGwtAccount(gwtAccount);
        gwtSession.setRootAccount(gwtAccount);
        gwtSession.setSelectedAccount(gwtAccount);

        // Permission info
        gwtSession.setAccountCreatePermission(hasAccountCreate);
        gwtSession.setAccountReadPermission(hasAccountRead);
        gwtSession.setAccountUpdatePermission(hasAccountUpdate);
        gwtSession.setAccountDeletePermission(hasAccountDelete);
        gwtSession.setAccountAllPermission(hasAccountAll);

        gwtSession.setDeviceCreatePermission(hasDeviceCreate);
        gwtSession.setDeviceReadPermission(hasDeviceRead);
        gwtSession.setDeviceUpdatePermission(hasDeviceUpdate);
        gwtSession.setDeviceDeletePermission(hasDeviceDelete);
        gwtSession.setDeviceManagePermission(hasDeviceManage);

        gwtSession.setDataReadPermission(hasDataRead);

        gwtSession.setUserCreatePermission(hasUserCreate);
        gwtSession.setUserReadPermission(hasUserRead);
        gwtSession.setUserUpdatePermission(hasUserUpdate);
        gwtSession.setUserDeletePermission(hasUserDelete);

        //
        // Saving session data in session
        // Session session = currentUser.getSession();
        // session.setAttribute(SESSION_CURRENT, gwtSession);
        // session.setAttribute(SESSION_CURRENT_USER, user);

        return gwtSession;
    }

    /**
     * Returns true if the currently connected user has the specified permission granted.
     */
    public Boolean hasAccess(String gwtPermission)
        throws GwtEdcException
    {
        Boolean hasAccess = false;
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            AuthorizationService authorizationService = locator.getService(AuthorizationService.class);

            // Parse from string
            PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
            Permission permission = permissionFactory.parseString(gwtPermission);

            // Check
            hasAccess = authorizationService.isPermitted(permission);
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return hasAccess;
    }

    /**
     * Logout call in response to the logout link/button.
     */
    public void logout()
        throws GwtEdcException
    {
        try {
            // Logout
            KapuaLocator locator = KapuaLocator.getInstance();
            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
            authenticationService.logout();

            // Invalidate http session
            HttpServletRequest request = getThreadLocalRequest();
            request.getSession().invalidate();
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }
}
