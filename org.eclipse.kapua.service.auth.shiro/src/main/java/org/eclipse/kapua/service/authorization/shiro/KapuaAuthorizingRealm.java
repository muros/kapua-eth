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
package org.eclipse.kapua.service.authorization.shiro;

import java.util.concurrent.Callable;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.predicate.KapuaPredicate;
import org.eclipse.kapua.service.authorization.Permission;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.authorization.permission.UserPermission;
import org.eclipse.kapua.service.authorization.permission.UserPermissionFactory;
import org.eclipse.kapua.service.authorization.permission.UserPermissionQuery;
import org.eclipse.kapua.service.authorization.permission.UserPermissionService;
import org.eclipse.kapua.service.authorization.permission.shiro.UserPermissionPredicates;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JPA-based application's one and only configured Apache Shiro Realm.
 */
public class KapuaAuthorizingRealm extends AuthorizingRealm
{
    private static final Logger logger     = LoggerFactory.getLogger(KapuaAuthorizingRealm.class);

    public static final String  REALM_NAME = "kapuaAuthorizingRealm";

    public KapuaAuthorizingRealm() throws KapuaException
    {
        setName(REALM_NAME);
    }

    /**
     * Authorization.
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
        throws AuthenticationException
    {
        //
        // Extract principal
        String username = (String) principals.getPrimaryPrincipal();
        logger.debug("Getting authorization info for: {}", username);

        //
        // Get Services
        KapuaLocator locator = KapuaLocator.getInstance();
        UserService userService = locator.getService(UserService.class);
        UserPermissionService userPermissionService = locator.getService(UserPermissionService.class);
        UserPermissionFactory userPermissionFactory = locator.getFactory(UserPermissionFactory.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);

        //
        // Get the associated user by name
        final User user;
        try {
            user = KapuaSecurityUtils.doPriviledge(new Callable<User>() {

                @Override
                public User call()
                    throws Exception
                {
                    return userService.findByName(username);
                }
            });
        }
        catch (Exception e) {
        	//to preserve the original exception messaage (if possible)
        	if (e instanceof AuthenticationException) {
				throw (AuthenticationException) e;
			}
			else {
				throw new ShiroException("Error while find user!", e);
			}
        }

        //
        // Check existence
        if (user == null) {
            throw new UnknownAccountException();
        }

        //
        // Get user permissions set
        UserPermissionQuery query = userPermissionFactory.newQuery(user.getScopeId());
        KapuaPredicate predicate = new AttributePredicate<KapuaId>(UserPermissionPredicates.USER_ID, user.getId());
        query.setPredicate(predicate);

        final KapuaListResult<UserPermission> userPermissions;
        try {
            userPermissions = KapuaSecurityUtils.doPriviledge(new Callable<KapuaListResult<UserPermission>>() {

                @Override
                public KapuaListResult<UserPermission> call()
                    throws Exception
                {
                    return userPermissionService.query(query);
                }
            });
        }
        catch (Exception e) {
        	//to preserve the original exception messaage (if possible)
        	if (e instanceof AuthenticationException) {
				throw (AuthenticationException) e;
			}
			else {
				throw new ShiroException("Error while find permissions!", e);
			}
        }

        //
        // Create SimpleAuthorizationInfo with principals permissions
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        for (UserPermission userPermission : userPermissions) {

            Permission p = permissionFactory.newPermission(userPermission.getDomain(),
                                                           userPermission.getAction(),
                                                           userPermission.getTargetScopeId());

            logger.trace("Username: {} has permission: {}", username, p);
            info.addStringPermission(p.toString());
        }

        return info;
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken)
    {
        /**
         * This method always returns false as it works only as AuthorizingReam.
         */
        return false;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
        throws AuthenticationException
    {
        /**
         * This method can always return null as it does not support any authentication token.
         */
        return null;
    }

}
