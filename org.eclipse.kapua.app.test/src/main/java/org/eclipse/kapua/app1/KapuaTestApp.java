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
package org.eclipse.kapua.app1;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.internal.AccountDomain;
import org.eclipse.kapua.service.authentication.AuthenticationCredentials;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.CredentialType;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.Permission;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.authorization.permission.UserPermissionCreator;
import org.eclipse.kapua.service.authorization.permission.UserPermissionFactory;
import org.eclipse.kapua.service.authorization.permission.UserPermissionService;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RoleFactory;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.device.registry.lifecycle.DeviceLifecycleDomain;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.internal.UserDomain;

/**
 * Hello world!
 *
 */
public class KapuaTestApp
{
    private static KapuaLocator                 locator               = KapuaLocator.getInstance();

    private static UserPermissionService        userPermissionService = locator.getService(UserPermissionService.class);
    private static UserPermissionFactory        userPermissionFactory = locator.getFactory(UserPermissionFactory.class);
    private static PermissionFactory            permissionFactory     = locator.getFactory(PermissionFactory.class);

    private static RoleService                  roleService           = locator.getService(RoleService.class);
    private static RoleFactory                  roleFactory           = locator.getFactory(RoleFactory.class);

    private static AuthenticationService        authenticationService = locator.getService(AuthenticationService.class);
    private static UsernamePasswordTokenFactory credentialsFactory    = locator.getFactory(UsernamePasswordTokenFactory.class);

    public static void main(String[] args)
    {
        String username = "kapua-sys";
        String password = "We!come12345";

        System.out.println("Hello World!");
        try {

            //
            // Test login
            authenticationService.login(credentialsFactory.newInstance(username, password.toCharArray()));
            Subject subject = SecurityUtils.getSubject();
            System.err.println("Is Authenticated: " + subject.isAuthenticated());
            System.err.println("Logged user: " + subject.getPrincipal());
            System.err.println("");

            //
            // Test find user
            UserService userService = locator.getService(UserService.class);
            User adminUser = userService.findByName(username);
            System.err.println("Found admin user: " + adminUser.getName() + " with userId: " + adminUser.getId());
            System.err.println("");

            //
            // Test find account
            AccountService accountService = locator.getService(AccountService.class);
            Account account = accountService.find(adminUser.getScopeId());
            System.err.println("Found admin account: " + account.getName() + " with userId: " + account.getId());
            System.err.println("");

            //
            // Test create user
            KapuaId scopeId = adminUser.getScopeId();
            UserFactory userFactory = locator.getFactory(UserFactory.class);
            UserCreator userCreator = userFactory.newCreator(scopeId, "user-" + scopeId.getShortId() + "-" + System.currentTimeMillis());
            userCreator.setDisplayName("Test Display Name");
            userCreator.setEmail("test@email.com");
            userCreator.setPhoneNumber("+1 555 123 4567");
            User user = userService.create(userCreator);

            //
            // Test create user credentials
            CredentialService credentialService = locator.getService(CredentialService.class);
            CredentialFactory credentialFactory = locator.getFactory(CredentialFactory.class);
            CredentialCreator credentialCreator = credentialFactory.newCreator(user.getScopeId(),
                                                                               user.getId(),
                                                                               CredentialType.PASSWORD,
                                                                               "We!come12345");
            credentialService.create(credentialCreator);

            //
            // Test update user
            user.setDisplayName("Test Display Name Update");
            user = userService.update(user);

            //
            // Test create user permission
            createPermission(scopeId, user.getId(), AccountDomain.ACCOUNT, Actions.write, scopeId);
            createPermission(scopeId, user.getId(), AccountDomain.ACCOUNT, Actions.read, scopeId);
            createPermission(scopeId, user.getId(), AccountDomain.ACCOUNT, Actions.delete, scopeId);

            createPermission(scopeId, user.getId(), UserDomain.USER, Actions.write, scopeId);
            createPermission(scopeId, user.getId(), UserDomain.USER, Actions.read, scopeId);
            createPermission(scopeId, user.getId(), UserDomain.USER, Actions.delete, scopeId);

            UserQuery userQuery = userFactory.newQuery(scopeId);
            KapuaListResult<User> userList = userService.query(userQuery);

            System.err.println("I found those users");
            for (User u : userList) {
                System.err.print("Id: " + u.getId());
                System.err.print("\t - User: " + u.getName());
                System.err.println("\t - Created On: " + u.getCreatedOn());
            }
            System.err.println("");

            //
            // Test create role
            Set<RolePermission> permissions = new HashSet<>();
            permissions.add(permissionFactory.newRolePermission(scopeId, AccountDomain.ACCOUNT, Actions.write, scopeId));
            permissions.add(permissionFactory.newRolePermission(scopeId, AccountDomain.ACCOUNT, Actions.read, scopeId));
            permissions.add(permissionFactory.newRolePermission(scopeId, DeviceLifecycleDomain.DEVICE_LIFECYCLE, Actions.read, scopeId));

            RoleCreator roleCreator = roleFactory.newCreator(scopeId);
            roleCreator.setName("role-" + scopeId.getShortId() + "-" + System.currentTimeMillis());
            roleCreator.setPermissions(permissions);

            Role role = roleService.create(roleCreator);

            System.err.println("Role Created:");
            System.err.println("Name: " + role.getName());
            System.err.println("Permissions:");
            for (RolePermission p : role.getPermissions()) {
                System.err.println("\t" + p.toString());
            }
            System.err.println("");

            role = roleService.find(role.getScopeId(), role.getId());
            System.err.println("Role Found:");
            System.err.println("Name: " + role.getName());
            System.err.println("Permissions:");
            for (RolePermission p : role.getPermissions()) {
                System.err.println("\t" + p.toString());
            }

            //
            // Test delete role
            roleService.delete(role);

            role = roleService.find(role.getScopeId(), role.getId());
            System.err.println("Role Deleted?? " + (role == null));

            //
            // Test logout
            authenticationService.logout();
            subject = SecurityUtils.getSubject();
            System.err.println("Is Authenticated: " + subject.isAuthenticated());
            System.err.println("Logged user: " + subject.getPrincipal());
            System.err.println("");

            //
            // Test BCrypt performance
            AuthenticationCredentials credentials = credentialsFactory.newInstance(user.getName(),
                                                                                   credentialCreator.getCredentialPlainKey().toCharArray());

            int tries = 1;
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < tries; i++) {
                authenticationService.login(credentials);
                authenticationService.logout();
            }
            long endTime = System.currentTimeMillis();

            System.err.println("Time taken for 100 logins: " + (endTime - startTime) / 1000 + "s");
            System.err.println("Avg time for 100 logins:   " + (endTime - startTime) / tries + "ms");
        }
        catch (KapuaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void createPermission(KapuaId scopeId, KapuaId userId, String domain, Actions action, KapuaId targetScopeId)
        throws KapuaException
    {
        UserPermissionCreator userPermissionCreator = userPermissionFactory.newCreator(scopeId);
        userPermissionCreator.setUserId(userId);

        Permission permission = permissionFactory.newPermission(domain, action, targetScopeId);
        userPermissionCreator.setPermission(permission);

        userPermissionService.create(userPermissionCreator);
    }
}
