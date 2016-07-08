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

import java.util.concurrent.Callable;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.Credential;
import org.eclipse.kapua.service.authentication.CredentialService;
import org.eclipse.kapua.service.authentication.shiro.credential.BCryptCredentialsMatcher;
import org.eclipse.kapua.service.authentication.shiro.credential.KapuaSimpleAuthenticationInfo;
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
        AccountService accountService;
        CredentialService credentialService;

        try {
            locator = KapuaLocator.getInstance();
            userService = locator.getService(UserService.class);
            accountService = locator.getService(AccountService.class);
            credentialService = locator.getService(CredentialService.class);
        }
        catch (KapuaRuntimeException kre) {
            throw new ShiroException("Error while getting services!", kre);
        }

        //
        // Get the associated user by name
        final User user;
        try {
            user = KapuaSecurityUtils.doPriviledge(new Callable<User>() {

                @Override
                public User call()
                    throws Exception
                {
                    return userService.findByName(tokenUsername);
                }
            });
        }
        catch (Exception e) {
            throw new ShiroException("Error while find user!", e);
        }

        // Check existence
        if (user == null) {
            throw new UnknownAccountException();
        }

        // Check disabled
        if (UserStatus.DISABLED.equals(user.getStatus())) {
            throw new DisabledAccountException();
        }

        //
        // Find account
        final Account account;
        try {
            account = KapuaSecurityUtils.doPriviledge(new Callable<Account>() {

                @Override
                public Account call()
                    throws Exception
                {
                    return accountService.find(user.getScopeId());
                }
            });
        }
        catch (Exception e) {
            throw new ShiroException("Error while find account!", e);
        }

        // Check existence
        if (account == null) {
            throw new UnknownAccountException();
        }

        //
        // Find credentials
        Credential credential = null;
        try {
            credential = KapuaSecurityUtils.doPriviledge(new Callable<Credential>() {

                @Override
                public Credential call()
                    throws Exception
                {
                    return credentialService.find(user.getScopeId(),
                                                  user.getId());
                }
            });
        }
        catch (Exception e) {
            throw new ShiroException("Error while find credentials!", e);
        }

        //
        // BuildAuthenticationInfo
        KapuaSimpleAuthenticationInfo info = new KapuaSimpleAuthenticationInfo(user,
                                                                               credential,
                                                                               account,
                                                                               getName());

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

        KapuaSimpleAuthenticationInfo kapuaInfo = (KapuaSimpleAuthenticationInfo) info;

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
        session.setAttribute("scopeId", kapuaInfo.getUser().getScopeId());
        session.setAttribute("userScopeId", kapuaInfo.getUser().getScopeId());
        session.setAttribute("userId", kapuaInfo.getUser().getId());

        // session.setAttribute(AuthorizationServiceBean.EDC_SESSION, edcSession);

    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken)
    {
        return (authenticationToken instanceof UsernamePasswordToken);
    }
}
