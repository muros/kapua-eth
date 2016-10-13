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
package org.eclipse.kapua.service.account.internal;

import java.util.List;

import javax.persistence.TypedQuery;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.configuration.AbstractKapuaConfigurableService;
import org.eclipse.kapua.commons.model.query.KapuaListResultImpl;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.util.KapuaExceptionUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;

/**
 * Account service implementation.
 * 
 * @since 1.0
 *
 */
public class AccountServiceImpl extends AbstractKapuaConfigurableService implements AccountService
{
    private static final long serialVersionUID = -312489270279852500L;

    /**
     * Constructor
     */
    public AccountServiceImpl()
    {
        super(AccountService.class.getName(), AccountDomain.ACCOUNT, AccountEntityManagerFactory.getInstance());
    }

    @Override
    public Account create(AccountCreator accountCreator)
        throws KapuaException
    {
        //
        // Validation of the fields
        ArgumentValidator.notNull(accountCreator, "accountCreator");
        ArgumentValidator.notEmptyOrNull(accountCreator.getName(), "name");
        ArgumentValidator.notEmptyOrNull(accountCreator.getAccountPassword(), "accountPassword");
        ArgumentValidator.notEmptyOrNull(accountCreator.getOrganizationName(), "organizationName");
        ArgumentValidator.notEmptyOrNull(accountCreator.getOrganizationEmail(), "organizationEmail");
        ArgumentValidator.notNull(accountCreator.getScopeId(), "scopeId");
        ArgumentValidator.notNull(accountCreator.getScopeId().getId(), "scopeId.id");
        ArgumentValidator.match(accountCreator.getAccountPassword(), ArgumentValidator.PASSWORD_REGEXP, "accountPassword");
        ArgumentValidator.match(accountCreator.getOrganizationEmail(), ArgumentValidator.EMAIL_REGEXP, "organizationEmail");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AccountDomain.ACCOUNT, Actions.write, accountCreator.getScopeId()));

        // Check if the parent account exists
        if (findById(accountCreator.getScopeId()) == null) {
            throw new KapuaIllegalArgumentException("scopeId", "parent account does not exist");
        }

        //
        // Create the account
        Account account = null;
        EntityManager em = AccountEntityManagerFactory.getInstance().createEntityManager();
        try {

            em.beginTransaction();

            account = AccountDAO.create(em, accountCreator);

            em.persist(account);

            // Set the parent account path
            String parentAccountPath = AccountDAO.find(em, accountCreator.getScopeId()).getParentAccountPath() + "/" + account.getId();
            account.setParentAccountPath(parentAccountPath);

            AccountDAO.update(em, account);

            em.commit();
        }
        catch (Exception pe) {
            em.rollback();
            throw KapuaExceptionUtils.convertPersistenceException(pe);
        }
        finally {
            em.close();
        }

        //
        // reload the latest version of the entity
        // to make sure we get all the latest info/version
        return find(account.getScopeId(), account.getId());
    }

    @Override
    public Account update(Account account)
        throws KapuaException
    {
        //
        // Validation of the fields
        ArgumentValidator.notNull(account.getId(), "id");
        ArgumentValidator.notNull(account.getId().getId(), "id.id");
        ArgumentValidator.notEmptyOrNull(account.getName(), "accountName");
        ArgumentValidator.notNull(account.getOrganization(), "organization");
        ArgumentValidator.match(account.getOrganization().getEmail(), ArgumentValidator.EMAIL_REGEXP, "organizationEmail");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AccountDomain.ACCOUNT, Actions.write, account.getScopeId()));

        //
        // Update the Account
        EntityManager em = AccountEntityManagerFactory.getInstance().createEntityManager();
        try {

            Account oldAccount = AccountDAO.find(em, account.getId());

            if (oldAccount == null) {
                throw new KapuaEntityNotFoundException(Account.TYPE, account.getId());
            }

            //
            // Verify unchanged parent account ID and parent account path
            if (!oldAccount.getScopeId().equals(account.getScopeId())) {
                throw new KapuaAccountException(KapuaAccountErrorCodes.ILLEGAL_ARGUMENT, null, "scopeId");
            }
            if (oldAccount.getParentAccountPath().compareTo(account.getParentAccountPath()) != 0) {
                throw new KapuaAccountException(KapuaAccountErrorCodes.ILLEGAL_ARGUMENT, null, "parentAccountPath");
            }
            if (oldAccount.getName().compareTo(account.getName()) != 0) {
                throw new KapuaAccountException(KapuaAccountErrorCodes.ILLEGAL_ARGUMENT, null, "accountName");
            }

            // Update
            em.beginTransaction();

            AccountDAO.update(em, account);

            em.commit();
        }
        catch (Exception e) {
            em.rollback();
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }

        return find(account.getScopeId(), account.getId());
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId accountId)
        throws KapuaException
    {

        //
        // Validation of the fields
        ArgumentValidator.notNull(accountId, "id");
        ArgumentValidator.notNull(scopeId, "id.id");

        //
        // Check Access
        Actions action = Actions.write;
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AccountDomain.ACCOUNT, action, scopeId));

        //
        // Check if it has children
        if (this.findChildAccountsTrusted(accountId).size() > 0) {
            throw new KapuaAccountException(KapuaAccountErrorCodes.OPERATION_NOT_ALLOWED, null, "This account cannot be deleted. Delete its child first.");
        }

        //
        // Delete the Account
        EntityManager em = AccountEntityManagerFactory.getInstance().createEntityManager();
        try {

            // Entity needs to be loaded in the context of the same EntityManger to be able to delete it afterwards
            Account accountx = AccountDAO.find(em, accountId);
            if (accountx == null) {
                throw new KapuaEntityNotFoundException(Account.TYPE, accountId);
            }

            // do not allow deletion of the kapua admin account
            SystemSetting settings = SystemSetting.getInstance();
            if (settings.getString(SystemSettingKey.SYS_PROVISION_ACCOUNT_NAME).equals(accountx.getName())) {
                throw new KapuaIllegalAccessException(action.name());
            }

            if (settings.getString(SystemSettingKey.SYS_ADMIN_ACCOUNT).equals(accountx.getName())) {
                throw new KapuaIllegalAccessException(action.name());
            }

            em.beginTransaction();
            AccountDAO.delete(em, accountId);
            em.commit();
        }
        catch (Exception e) {
            em.rollback();
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }
    }

    @Override
    public Account find(KapuaId scopeId, KapuaId id)
        throws KapuaException
    {
        //
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(scopeId.getId(), "scopeId.id");
        ArgumentValidator.notNull(id, "id");
        ArgumentValidator.notNull(id.getId(), "id.id");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AccountDomain.ACCOUNT, Actions.read, scopeId));

        //
        // Make sure account exists
        return findById(id);
    }

    @Override
    public Account find(KapuaId id)
        throws KapuaException
    {
        //
        // Validation of the fields
        ArgumentValidator.notNull(id, "id");
        ArgumentValidator.notNull(id.getId(), "id.id");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AccountDomain.ACCOUNT, Actions.read, id));

        //
        // Make sure account exists
        return findById(id);
    }

    @Override
    public Account findByName(String name)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notEmptyOrNull(name, "name");

        //
        // Do the find
        Account account = null;
        EntityManager em = AccountEntityManagerFactory.getInstance().createEntityManager();
        try {
            account = AccountDAO.findByName(em, name);
        }
        catch (Exception e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }

        //
        // Check Access
        if (account != null) {
            KapuaLocator locator = KapuaLocator.getInstance();
            AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
            PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
            authorizationService.checkPermission(permissionFactory.newPermission(AccountDomain.ACCOUNT, Actions.read, account.getId()));
        }

        return account;
    }

    @Override
    public AccountListResult findChildsRecursively(KapuaId id)
        throws KapuaException
    {

        //
        // Validation of the fields
        ArgumentValidator.notNull(id, "scopeId");
        ArgumentValidator.notNull(id.getId(), "scopeId.id");

        //
        // Make sure account exists
        Account account = findById(id);
        if (account == null) {
            throw new KapuaEntityNotFoundException(Account.TYPE, id);
        }

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AccountDomain.ACCOUNT, Actions.read, account.getId()));

        AccountListResult result = null;
        EntityManager em = AccountEntityManagerFactory.getInstance().createEntityManager();
        try {
            TypedQuery<Account> q;
            q = em.createNamedQuery("Account.findChildAccountsRecursive", Account.class);
            q.setParameter("parentAccountPath", account.getParentAccountPath() + "/%");

            result = new AccountListResultImpl();
            result.addItems(q.getResultList());

        }
        catch (Exception e) {
            em.rollback();
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }

        return result;
    }

    @Override
    public KapuaListResultImpl<Account> query(KapuaQuery<Account> query)
        throws KapuaException
    {
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AccountDomain.ACCOUNT, Actions.read, query.getScopeId()));

        //
        // Do count
        KapuaListResultImpl<Account> result = null;
        EntityManager em = AccountEntityManagerFactory.getInstance().createEntityManager();
        try {
            result = AccountDAO.query(em, query);
        }
        catch (Exception e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }

        return result;
    }

    @Override
    public long count(KapuaQuery<Account> query)
        throws KapuaException
    {
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AccountDomain.ACCOUNT, Actions.read, query.getScopeId()));

        //
        // Do count
        long count = 0;
        EntityManager em = AccountEntityManagerFactory.getInstance().createEntityManager();
        try {
            count = AccountDAO.count(em, query);
        }
        catch (Exception e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }

        return count;
    }

    /**
     * Find an account without authorization.
     * 
     * @param accountId
     * @return
     * @throws KapuaException
     */
    private Account findById(KapuaId accountId)
        throws KapuaException
    {

        //
        // Argument Validation
        ArgumentValidator.notNull(accountId, "accountId");

        //
        // Do the find
        Account account = null;
        EntityManager em = AccountEntityManagerFactory.getInstance().createEntityManager();
        try {
            account = AccountDAO.find(em, accountId);
        }
        catch (Exception e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }
        return account;
    }

    private List<Account> findChildAccountsTrusted(KapuaId accountId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountId, "accountId");
        ArgumentValidator.notNull(accountId.getId(), "accountId.id");

        //
        // Do the find
        List<Account> accounts = null;
        EntityManager em = AccountEntityManagerFactory.getInstance().createEntityManager();
        try {
            TypedQuery<Account> q = em.createNamedQuery("Account.findChildAccounts", Account.class);
            q.setParameter("scopeId", accountId);
            accounts = q.getResultList();
        }
        catch (Exception e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }
        return accounts;
    }
}
