package org.eclipse.kapua.commons.util;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityManager
{
    private static final Logger             LOG = LoggerFactory.getLogger(AbstractEntityManagerFactory.class);

    private javax.persistence.EntityManager javaxPersitenceEntityManager;

    public EntityManager(javax.persistence.EntityManager javaxPersitenceEntityManager)
    {
        this.javaxPersitenceEntityManager = javaxPersitenceEntityManager;
    }

    /**
     * Opens a Jpa Transaction.<br/>
     * <br/>
     * The transaction MUST be closed after being commited or rollbacked, using {@link AbstractEntityManagerFactory#close(EntityManager)}
     * 
     * @throws KapuaException if given {@link EntityManager} is {@code null}
     * 
     * @since 1.0.0
     */
    public void beginTransaction()
        throws KapuaException
    {
        if (javaxPersitenceEntityManager == null) {
            throw KapuaException.internalError(new NullPointerException(), "null EntityManager");
        }
        javaxPersitenceEntityManager.getTransaction().begin();
    }

    /**
     * Commits the current Jpa Transaction.
     */
    public void commit()
        throws KapuaException
    {
        if (javaxPersitenceEntityManager == null) {
            throw KapuaException.internalError("null EntityManager");
        }
        if (!javaxPersitenceEntityManager.getTransaction().isActive()) {
            throw KapuaException.internalError("Transaction Not Active");
        }

        try {
            javaxPersitenceEntityManager.getTransaction().commit();
        }
        catch (Exception e) {
            throw KapuaException.internalError(e, "Commit Error");
        }
    }

    /**
     * Rollbacks the current Jpa Transaction. No exception will be thrown when rolling back so that the original exception that caused the rollback can be thrown.
     */
    public void rollback()
    {
        try {
            if (javaxPersitenceEntityManager != null &&
                javaxPersitenceEntityManager.getTransaction().isActive()) {
                javaxPersitenceEntityManager.getTransaction().rollback();
            }
        }
        catch (Exception e) {
            LOG.warn("Rollback Error", e);
        }
    }

    /**
     * Closes the EntityManager
     * 
     * @param em
     *            EntityManager to be closed.
     */
    public void close()
    {
        if (javaxPersitenceEntityManager != null) {
            javaxPersitenceEntityManager.close();
        }
    }

    public <E extends KapuaEntity> void persist(E entity)
    {
        javaxPersitenceEntityManager.persist(entity);
    }

    public void flush()
    {
        javaxPersitenceEntityManager.flush();
    }

    public <E extends KapuaEntity> E find(Class<E> clazz, KapuaId id)
    {
        return javaxPersitenceEntityManager.find(clazz, id);
    }

    public <E extends KapuaEntity> void merge(E entity)
    {
        javaxPersitenceEntityManager.merge(entity);
    }

    public <E extends KapuaEntity> void refresh(E entity)
    {
        javaxPersitenceEntityManager.refresh(entity);
    }

    public <E extends KapuaEntity> void remove(E entity)
    {
        javaxPersitenceEntityManager.remove(entity);
    }

    public CriteriaBuilder getCriteriaBuilder()
    {
        return javaxPersitenceEntityManager.getCriteriaBuilder();
    }

    public <E> TypedQuery<E> createQuery(CriteriaQuery<E> criteriaSelectQuery)
    {
        return javaxPersitenceEntityManager.createQuery(criteriaSelectQuery);
    }

    public <E> TypedQuery<E> createNamedQuery(String queryName, Class<E> clazz)
    {
        return javaxPersitenceEntityManager.createNamedQuery(queryName, clazz);
    }

    public <E> Query createNativeQuery(String querySelectUuidShort)
    {
        return javaxPersitenceEntityManager.createNativeQuery(querySelectUuidShort);
    }
}
