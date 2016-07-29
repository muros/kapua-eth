package org.eclipse.kapua.service.authentication.credential.shiro;

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.JpaUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.KapuaPredicate;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.eclipse.kapua.service.authentication.credential.CredentialPredicates;
import org.eclipse.kapua.service.authentication.credential.CredentialQuery;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;

public class CredentialServiceImpl implements CredentialService
{

    @Override
    public Credential create(CredentialCreator credentialCreator)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(credentialCreator, "credentialCreator");
        ArgumentValidator.notNull(credentialCreator.getScopeId(), "credentialCreator.scopeId");
        ArgumentValidator.notNull(credentialCreator.getUserId(), "credentialCreator.userId");
        ArgumentValidator.notNull(credentialCreator.getCredentialType(), "credentialCreator.credentialType");
        ArgumentValidator.notEmptyOrNull(credentialCreator.getCredentialPlainKey(), "credentialCreator.credentialKey");

        //
        // Check access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(CredentialDomain.CREDENTIAL, Actions.write, credentialCreator.getScopeId()));

        //
        // Do create
        Credential credential = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            JpaUtils.beginTransaction(em);

            credential = CredentialDAO.create(em, credentialCreator);

            credential = CredentialDAO.find(em, credential.getId());
            JpaUtils.commit(em);
        }
        catch (Exception pe) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(pe);
        }
        finally {
            JpaUtils.close(em);
        }

        return credential;
    }

    @Override
    public Credential update(Credential credential)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(credential, "credentialCreator");
        ArgumentValidator.notNull(credential.getId(), "credentialCreator.id");
        ArgumentValidator.notNull(credential.getScopeId(), "credentialCreator.scopeId");
        ArgumentValidator.notNull(credential.getUserId(), "credentialCreator.userId");
        ArgumentValidator.notNull(credential.getCredentialType(), "credentialCreator.credentialType");
        ArgumentValidator.notEmptyOrNull(credential.getCredentialKey(), "credentialCreator.credentialKey");

        //
        // Check access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(CredentialDomain.CREDENTIAL, Actions.write, credential.getScopeId()));

        //
        // Do update
        Credential credentialUpdated = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {

            Credential currentUser = CredentialDAO.find(em, credential.getId());
            if (currentUser == null) {
                throw new KapuaEntityNotFoundException(Credential.TYPE, credential.getId());
            }

            // Passing attributes??

            JpaUtils.beginTransaction(em);
            CredentialDAO.update(em, credential);
            JpaUtils.commit(em);

            credentialUpdated = CredentialDAO.find(em, credential.getId());
        }
        catch (Exception pe) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(pe);
        }
        finally {
            JpaUtils.close(em);
        }

        return credentialUpdated;
    }

    @Override
    public Credential find(KapuaId scopeId, KapuaId credentialId)
        throws KapuaException
    {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(credentialId, "credentialId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(CredentialDomain.CREDENTIAL, Actions.read, scopeId));

        //
        // Do find
        Credential credential = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            credential = CredentialDAO.find(em, credentialId);
        }
        catch (Exception pe) {
            throw JpaUtils.toKapuaException(pe);
        }
        finally {
            JpaUtils.close(em);
        }

        return credential;
    }

    @Override
    public CredentialListResult query(KapuaQuery<Credential> query)
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
        authorizationService.checkPermission(permissionFactory.newPermission(CredentialDomain.CREDENTIAL, Actions.read, query.getScopeId()));

        //
        // Do count
        CredentialListResult result = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            result = CredentialDAO.query(em, query);
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
    public long count(KapuaQuery<Credential> query)
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
        authorizationService.checkPermission(permissionFactory.newPermission(CredentialDomain.CREDENTIAL, Actions.read, query.getScopeId()));

        //
        // Do count
        long count = 0;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            count = CredentialDAO.count(em, query);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return count;
    }

    @Override
    public void delete(Credential credential)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(credential, "credential");
        ArgumentValidator.notNull(credential.getId(), "credential.id");
        ArgumentValidator.notNull(credential.getScopeId(), "credential.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(CredentialDomain.CREDENTIAL, Actions.delete, credential.getScopeId()));

        //
        // Do delete
        EntityManager em = JpaUtils.getEntityManager();
        try {
            KapuaId credentialId = credential.getId();

            if (CredentialDAO.find(em, credentialId) == null) {
                throw new KapuaEntityNotFoundException(Credential.TYPE, credentialId);
            }

            JpaUtils.beginTransaction(em);
            CredentialDAO.delete(em, credentialId);
            JpaUtils.commit(em);
        }
        catch (Exception e) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }
    }

    @Override
    public CredentialListResult findByUserId(KapuaId scopeId, KapuaId userId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "userId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(CredentialDomain.CREDENTIAL, Actions.read, scopeId));

        //
        // Build query
        CredentialQuery query = new CredentialQueryImpl(scopeId);
        KapuaPredicate predicate = new AttributePredicate<KapuaId>(CredentialPredicates.USER_ID, userId);
        query.setPredicate(predicate);

        //
        // Query and return result
        return query(query);
    }

}
