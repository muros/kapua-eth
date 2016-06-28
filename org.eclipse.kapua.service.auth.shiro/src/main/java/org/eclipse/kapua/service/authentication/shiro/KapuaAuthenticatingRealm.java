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
package org.eclipse.kapua.service.authentication.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authentication.shiro.credential.BCryptCredentialsMatcher;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JPA-based application's one and only configured Apache Shiro Realm.
 */
public class KapuaAuthenticatingRealm extends AuthenticatingRealm
{
    @SuppressWarnings("unused")
    private static final Logger s_logger               = LoggerFactory.getLogger(KapuaAuthenticatingRealm.class);

    public static final String  REALM_NAME             = "kapuaAuthenticatingRealm";
    public static final String  PRINCIPAL_USER_ID_KEY  = "userIdPrincipal";
    public static final String  PRINCIPAL_USERNAME_KEY = "usernamePrincipal";

    public KapuaAuthenticatingRealm() throws KapuaException
    {
        // This name must match the name in the User class's getPrincipals() method
        setName(REALM_NAME);

        CredentialsMatcher credentialsMather = new BCryptCredentialsMatcher();
        setCredentialsMatcher(credentialsMather);
    }

    /**
     * Authentication
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
        throws AuthenticationException
    {
        //
        // Extract credentials
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String tokenUsername = token.getUsername();
        // char[] tokenPassword = token.getPassword();

        //
        // Get Services
        KapuaLocator locator;
        UserService userService;
        // AccountService accountService;

        try {
            locator = KapuaLocator.getInstance();
            userService = locator.getService(UserService.class);
            // accountService; = locator.getService(AccountService.class);
        }
        catch (KapuaRuntimeException kre) {
            throw new ShiroException("Error while getting services!", kre);
        }

        //
        // Get the associated user by name
        User user = null;
        try {
            user = userService.findByName(tokenUsername);
        }
        catch (KapuaException ke) {
            throw new ShiroException("Error while find user!", ke);
        }

        //
        // Check existence
        if (user == null) {
            throw new UnknownAccountException();
        }

        //
        // Check disabled
        if (UserStatus.DISABLED.equals(user.getStatus())) {
            throw new DisabledAccountException();
        }

        //
        // Get the associated account by scopeId
        // Account account = null;
        // try {
        // account = accountService.find(null, user.getScopeId());
        // }
        // catch (KapuaException ke) {
        // throw new ShiroException("Error while find account.", ke);
        // }

        // //
        // // Check existence
        // if (account == null) {
        // throw new ShiroException("Account(" + user.getScopeId() + ") not found for user: " + user.getId());
        // }

        //
        // BuildAuthenticationInfo
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getName(),
                                                                     user.getPassword(),
                                                                     getName());

        // try {
        // ServiceLocator locator = ServiceLocator.getInstance();
        // AuthorizationService authorizationService = locator.getAuthorizationService();
        // AccountService accountService = locator.getAccountService();
        //
        // // Check the user
        // User user = authorizationService.findUserByNameWithAccessInfo(username);
        // if (user == null) {
        // unknownUser.inc();
        // throw new UnknownAccountException(username);
        // }
        //
        // // If specified, check @accountName info
        // Long runAsAccountId = null;
        // AccountServicePlan runAsAccountServicePlan = null;
        // if (runAsAccountName != null) {
        // runAsAccountId = accountService.getAccountId(runAsAccountName);
        // if (runAsAccountId==null) {
        // unknownAccount.inc();
        // throw new KapuaEntityNotFoundException(Account.class, runAsAccountName);
        // }
        // runAsAccountServicePlan = accountService.getAccountServicePlanTrusted(runAsAccountId);
        // }
        //
        // if (UserStatus.DISABLED.equals(user.getStatus())) {
        // disabledUser.inc();
        // throw new DisabledAccountException(username);
        // }
        //
        // // Load account name from ID
        // // Load account service plan as a single DAO call (do not load the whole account)
        // // UserService.isUserLocked(User, ASP.gteLockoutPolicy)
        // // load account service plan for the user
        // AccountServicePlan accountServicePlan = accountService.getAccountServicePlanTrusted(user.getAccountId());
        // UserService userService = ServiceLocator.getInstance().getUserService();
        // if (userService.isLocked(user, accountServicePlan.getLockoutPolicy())) {
        // // increment the number of login attempts
        // UserService us = locator.getUserService();
        // us.updateLoginInfo(user.getAccountId(),
        // user.getId(),
        // user.getStatus(),
        // user.getLoginOn(),
        // user.getLoginAttempts() + 1,
        // user.getLoginAttemptsResetOn(),
        // user.getLockedOn(),
        // user.getUnlockOn());
        // lockedUser.inc();
        // throw new LockedAccountException(username);
        // }
        //
        // if (!AccountStatus.ENABLED.equals(accountServicePlan.getStatus())) {
        // String msg = MessageFormat.format("Account {0} disabled for user {1}", accountServicePlan.getAccountId(), username);
        // disabledAccount.inc();
        // throw new DisabledAccountException(msg);
        // }
        // Date now = new Date();
        // if (accountServicePlan.getExpirationDate() != null && now.after(accountServicePlan.getExpirationDate())) {
        // String msg = MessageFormat.format("Account {0} expired for user {1}", accountServicePlan.getAccountId(), username);
        // expiredAccount.inc();
        // throw new ExpiredCredentialsException(msg);
        // }
        //
        // // All good
        // // A User was found and it is enabled.
        // // Build a SimpleAuthenticationInfo, EdcSession and return it.
        // EdcSimpleByteSource sbs = null;
        // if (user.getSalt() != null) {
        // sbs = new EdcSimpleByteSource(user.getSalt());
        // }
        //
        // // load account name
        // String accountName = accountService.getAccountName(user.getAccountId());
        //
        // // create edc session object
        // EdcSession edcSession = new EdcSession(user, accountServicePlan, accountName);
        // if (runAsAccountName != null) {
        // edcSession.setRunAsAccountId(runAsAccountId);
        // edcSession.setRunAsAccountName(runAsAccountName);
        // edcSession.setRunAsAccountServicePlan(runAsAccountServicePlan);
        // }

        // EdcSimpleAuthenticationInfo should keep: User, AccountServicePlan, accountName

        // edcSession,
        // user.getId(),
        // user.getPassword(),
        // sbs,
        // getName());
        // }
        // catch (AuthenticationException ae) {
        // s_logger.error("AuthenticationException during doGetAuthenticationInfo", ae);
        // throw ae;
        // }
        // catch (KapuaException ee) {
        // s_logger.error("EdcException during doGetAuthenticationInfo", ee);
        // throw new AuthenticationException(ee);
        // }
        // catch (Throwable t) {
        // s_logger.error("Throwable during doGetAuthenticationInfo", t);
        // throw new AuthenticationException(t);
        // }

        return info;
    }

    /**
     * Keeps track of the number of login attempts.
     */
    protected void assertCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info)
        throws AuthenticationException
    {
        // ServiceLocator locator = ServiceLocator.getInstance();
        // AuthorizationService authServ = locator.getAuthorizationService();
        //
        // EdcSimpleAuthenticationInfo edcai = (EdcSimpleAuthenticationInfo) info;
        // EdcSession edcSession = edcai.getEdcSession();
        // User user = edcSession.getUser();
        //
        // EdcUsernamePasswordToken token = (EdcUsernamePasswordToken) authcToken;
        //
        // IncorrectCredentialsException ice = null;
        //

        super.assertCredentialsMatch(authcToken, info);

        //
        // // updating session
        // // Session should track from EdcSimpleAuthenticationInfo: User, AccountServicePlan, accountName
        // // EdcSession: User, AccountServicePlan, accountName, runAsAccountId, runAsAccountName, runAsAccountServicePlan
        // // EdcSession.getSessionAccountServicePlan() : runAsAccountServicePlan != null ? runAsAccountServicePlan : AccountServicePlan
        // // EdcSession.getSessionAccountId() : runAsAccountId != null ? runAsAccountId : accountId
        // // EdcSession.getSessionAccountName() : runAsAccountName != null ? runAsAccountName : accountName

        Subject currentSubject = SecurityUtils.getSubject();
        // Storing raw password and user in session
        // if (!LoginSourceType.MQTT_DEVICE.equals(token.getSourceType())) {
        // user.setRawPassword(new String(token.getPassword()));
        // }
        Session session = currentSubject.getSession();
        // session.setAttribute(AuthorizationServiceBean.EDC_SESSION, edcSession);

    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken)
    {
        return (authenticationToken instanceof UsernamePasswordToken);
    }
}
