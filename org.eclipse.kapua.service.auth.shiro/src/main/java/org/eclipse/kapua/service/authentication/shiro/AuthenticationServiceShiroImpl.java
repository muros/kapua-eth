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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.SubjectUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.AccessToken;
import org.eclipse.kapua.service.authentication.AuthenticationCredentials;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.KapuaSession;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.MDC;

public class AuthenticationServiceShiroImpl implements AuthenticationService, KapuaService
{
    static {
        Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

    @Override
    public AccessToken login(AuthenticationCredentials authenticationToken)
        throws KapuaException
    {
        Subject currentUser = SecurityUtils.getSubject();

        if (currentUser.isAuthenticated()) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.SUBJECT_ALREADY_LOGGED);
        }

        // AccessToken accessToken = null;
        if (authenticationToken instanceof UsernamePasswordTokenImpl) {

            UsernamePasswordTokenImpl usernamePasswordToken = (UsernamePasswordTokenImpl) authenticationToken;

            MDC.put(SubjectUtils.MDC_USERNAME, usernamePasswordToken.getUsername());

            UsernamePasswordToken shiroToken = new UsernamePasswordToken(usernamePasswordToken.getUsername(),
                                                                         usernamePasswordToken.getPassword());
            try {
                currentUser.login(shiroToken);

                // TODO: CRUD operations
                // AccessTokenCreator accessTokenCreator = new AccessTokenCreatorImpl();
                //
                // // some setters...
                //
                // ServiceLocator locator = ServiceLocator.getInstance();
                // AccessTokenService accessTokenService = locator.getAccessTokenService()
                // accessToken = accessTokenService.create(accessTokenCreator);
            }
            catch (ShiroException se) {

                KapuaAuthenticationException kae = null;
                if (se instanceof UnknownAccountException) {
                    kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INVALID_USERNAME, se, usernamePasswordToken.getUsername());
                }
                else if (se instanceof DisabledAccountException) {
                    kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.DISABLED_USERNAME, se, usernamePasswordToken.getUsername());
                }
                else if (se instanceof LockedAccountException) {
                    kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.LOCKED_USERNAME, se, usernamePasswordToken.getUsername());
                }
                else if (se instanceof IncorrectCredentialsException) {
                    kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INVALID_CREDENTIALS, se, usernamePasswordToken.getUsername());
                }
                else if (se instanceof ExpiredCredentialsException) {
                    kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.EXPIRED_CREDENTIALS, se, usernamePasswordToken.getUsername());
                }
                else {
                    throw KapuaAuthenticationException.internalError(se);
                }

                currentUser.logout();

                throw kae;
            }
        }
        else {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INVALID_CREDENTIALS_TOKEN_PROVIDED);
        }

        // return accessToken = new AccessTokenImpl();
        return null;
    }

    @Override
    public void logout()
        throws KapuaException
    {

        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
    }

    @Override
    public KapuaId getCurrentUserId()
        throws KapuaException
    {
        KapuaId currentUserId = null;
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            String username = (String) subject.getPrincipal();

            KapuaLocator locator = KapuaLocator.getInstance();
            UserService userService = locator.getService(UserService.class);

            User user = null;
            try {
                user = userService.findByName(username);
            }
            catch (KapuaException ke) {
                throw new ShiroException("Error while find user.", ke);
            }

            if (user != null) {
                currentUserId = user.getId();
            }
        }
        return currentUserId;
    }

    @Override
    public String cryptCredential(String plainValue)
        throws KapuaException
    {
        //
        // Argument validator
        ArgumentValidator.notEmptyOrNull(plainValue, "plainValue");

        //
        // Do crypt
        String cryptedValue = null;
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            String salt = BCrypt.gensalt(12, random);
            cryptedValue = BCrypt.hashpw(plainValue, salt);
        }
        catch (NoSuchAlgorithmException e) {
            throw new KapuaRuntimeException(KapuaAuthenticationErrorCodes.CREDENTIAL_CRYPT_ERROR, e, (Object[]) null);
        }

        return cryptedValue;
    }

    @Override
    public KapuaSession getCurrentSession()
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
