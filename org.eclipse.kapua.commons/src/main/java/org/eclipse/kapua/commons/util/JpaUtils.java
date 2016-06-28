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
package org.eclipse.kapua.commons.util;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.KapuaOptimisticLockingException;
import org.eclipse.kapua.commons.config.KapuaEnvironmentConfig;
import org.eclipse.kapua.commons.config.KapuaEnvironmentConfigKeys;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.PooledDataSource;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

/**
 * Utility class for JPA operations
 */
public class JpaUtils
{
    private static final Logger               s_logger            = LoggerFactory.getLogger(JpaUtils.class);

    private static final String               DATASOURCE_NAME     = "kapua-dbpool";

    private static final Map<String, String>  s_uniqueConstraints = new HashMap<String, String>();
    private static final EntityManagerFactory s_emf;

    static {
        // Initialize the EntityManagerFactory
        try {

            KapuaEnvironmentConfig config = KapuaEnvironmentConfig.getInstance();
            String dbUrl = config.get(String.class, KapuaEnvironmentConfigKeys.DB_URL);
            dbUrl = dbUrl + "?useTimezone=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8";

            Map<String, Object> configOverrides = new HashMap<String, Object>();
            configOverrides.put("javax.persistence.spi.PersistenceProvider", "org.hibernate.jpa.HibernatePersistenceProvider");
            configOverrides.put("hibernate.connection.url", dbUrl);
            configOverrides.put("hibernate.connection.username", config.getString(KapuaEnvironmentConfigKeys.DB_USERNAME));
            configOverrides.put("hibernate.connection.password", config.getString(KapuaEnvironmentConfigKeys.DB_PASSWORD));
            configOverrides.put("hibernate.c3p0.dataSourceName", DATASOURCE_NAME);
            configOverrides.put("hibernate.c3p0.min_size", config.getInt(KapuaEnvironmentConfigKeys.DB_MIN_SIZE));
            configOverrides.put("hibernate.c3p0.max_size", config.getInt(KapuaEnvironmentConfigKeys.DB_MAX_SIZE));
            configOverrides.put("hibernate.c3p0.acquire_increment", config.getString(KapuaEnvironmentConfigKeys.DB_INCREMENT));
            configOverrides.put("hibernate.c3p0.timeout", config.getInt(KapuaEnvironmentConfigKeys.DB_TIMEOUT));
            configOverrides.put("hibernate.connection.zeroDateTimeBehavior", "convertToNull");

            if (config.getBoolean(KapuaEnvironmentConfigKeys.OSGI_CONTEXT)) {
                // OSGi JPA
                // Could get this by wiring up OsgiTestBundleActivator as well.
                org.osgi.framework.Bundle thisBundle = org.osgi.framework.FrameworkUtil.getBundle(JpaUtils.class);
                org.osgi.framework.BundleContext context = thisBundle.getBundleContext();
                s_logger.info(">>> Bundle context: " + context);

                @SuppressWarnings("rawtypes")
                org.osgi.framework.ServiceReference serviceReference = context.getServiceReference(PersistenceProvider.class.getName());
                s_logger.info(">>> Service Reference: " + serviceReference);

                @SuppressWarnings("unchecked")
                PersistenceProvider persistenceProvider = (PersistenceProvider) context.getService(serviceReference);
                s_logger.info(">>> Persistence Provider: " + persistenceProvider);

                s_emf = persistenceProvider.createEntityManagerFactory("kapua", configOverrides);
            }
            else {

                // Standalone JPA
                s_emf = Persistence.createEntityManagerFactory("kapua", configOverrides);
                ((EntityManagerFactoryImpl) s_emf).getSessionFactory().getSettings();
            }
        }
        catch (Throwable ex) {
            ex.printStackTrace();
            s_logger.error("Error creating EntityManagerFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }

        //
        // Initialize the Unique Constraints Maps
        s_uniqueConstraints.put("uc_accountName", "accountName");
        s_uniqueConstraints.put("uc_accountNamespace", "accountNamespace");
        s_uniqueConstraints.put("uc_organizationName", "organizationName");
        s_uniqueConstraints.put("uc_organizationEmail", "organizationEmail");
        s_uniqueConstraints.put("uc_userName", "userName");
        s_uniqueConstraints.put("uc_userEmail", "userEmail");
        s_uniqueConstraints.put("uc_ruleName", "ruleName");
        s_uniqueConstraints.put("uc_clientId", "clientId");
        s_uniqueConstraints.put("uc_displayName", "displayName");
        s_uniqueConstraints.put("uc_domainName", "domainName");
    }

    private JpaUtils()
    {
    }

    /**
     * Returns a reference to the DataSource used by Hibernate
     */
    public static DataSource getDataSource()
        throws KapuaException
    {
        return C3P0Registry.pooledDataSourceByName(DATASOURCE_NAME);
    }

    public static void cleanUpDataSource()
        throws Exception
    {
        //
        // C3p0 clean up
        @SuppressWarnings("unchecked")
        Set<PooledDataSource> pooledDataSourceSet = (Set<PooledDataSource>) C3P0Registry.getPooledDataSources();
        for (PooledDataSource dataSource : pooledDataSourceSet) {
            try {
                s_logger.info("Closing PooledDataSource: {}...", dataSource.getDataSourceName());
                dataSource.close();
                s_logger.info("Closed PooledDataSource: {}!", dataSource.getDataSourceName());

            }
            catch (SQLException e) {
                s_logger.error("Error while closing PooledDataSource: " + dataSource.getDataSourceName(), e);
            }

        }

        //
        // JDBC AbandonedConnectionCleanupThread clean up
        try {
            s_logger.info("Closing AbandonedConnectionCleanupThread...");
            AbandonedConnectionCleanupThread.shutdown();
            s_logger.info("Closed AbandonedConnectionCleanupThread!");
        }
        catch (InterruptedException e) {
            s_logger.error("Error while closing AbandonedConnectionCleanupThread", e);
        }
    }

    /**
     * Returns an EntityManager instance.
     */
    public static EntityManager getEntityManager()
        throws KapuaException
    {
        EntityManager em = s_emf.createEntityManager();
        if (em == null) {
            throw KapuaException.internalError("Cannot create an EntityManager");
        }
        return em;
    }

    /**
     * Opens a Jpa Transaction.
     */
    public static void beginTransaction(EntityManager em)
        throws KapuaException
    {
        if (em == null) {
            throw KapuaException.internalError("null EntityManager");
        }
        em.getTransaction().begin();
    }

    /**
     * Commits the current Jpa Transaction.
     */
    public static void commit(EntityManager em)
        throws KapuaException
    {
        if (em == null) {
            throw KapuaException.internalError("null EntityManager");
        }
        if (!em.getTransaction().isActive()) {
            throw KapuaException.internalError("Transaction Not Active");
        }

        try {
            em.getTransaction().commit();
        }
        catch (Exception e) {
            throw KapuaException.internalError(e, "Commit Error");
        }
    }

    /**
     * Rollbacks the current Jpa Transaction. No exception will be thrown when rolling back so that the original exception that caused the rollback can be thrown.
     */
    public static void rollback(EntityManager em)
    {
        try {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        catch (Exception e) {
            s_logger.warn("Rollback Error", e);
        }
    }

    /**
     * Closes the EntityManager
     * 
     * @param em
     *            EntityManager to be closed.
     */
    public static void close(EntityManager em)
    {
        if (em != null) {
            em.close();
        }
    }

    public static long getUuidShort(EntityManager em)
    {
        Query q = em.createNativeQuery("SELECT UUID_SHORT() FROM DUAL");
        BigInteger bi = (BigInteger) q.getSingleResult();
        return bi.longValue();
    }

    /**
     * Converts a low-level PersistenceException/SQLException to a business-level KapuaException.
     * 
     * @param e
     * @throws KapuaException
     */
    public static KapuaException toKapuaException(Exception e)
    {
        Exception rootException = e;

        // Handle the case of an incoming EdcException
        if (e instanceof KapuaException && e.getCause() != null && e.getCause() instanceof PersistenceException) {
            rootException = (Exception) e.getCause();
        }
        else if (e instanceof KapuaException) {
            return (KapuaException) rootException;
        }

        // process the Persistence Exception
        KapuaException ee = null;
        if (rootException instanceof OptimisticLockException) {
            ee = new KapuaOptimisticLockingException(rootException);
        }
        else {
            Throwable t = rootException.getCause();
            if (t instanceof PersistenceException) {
                t = t.getCause();
            }
            if (t instanceof RollbackException) {
                t = t.getCause();
            }
            if (t instanceof ConstraintViolationException) {

                // Handle Unique Constraints Exception
                ConstraintViolationException cve = (ConstraintViolationException) t;

                int sqlErrorCode = cve.getErrorCode();
                String sqlErrorMsg = cve.getSQLState();
                switch (sqlErrorCode) {

                    // SQL Error: 1062, SQLState: 23000 - ER_DUP_KEYNAME - Unique Constraints Exception
                    case 1062:
                        if ("23000".equals(sqlErrorMsg)) {

                            //
                            // Extract the constraint name
                            // e.g. SQL Message: Duplicate entry 'test_account_1,322,584,746,357' for key 'uc_accountName'
                            String message = cve.getSQLException().getMessage();
                            String[] parts = message.split("'");
                            String constraintName = parts[parts.length - 1];

                            //
                            // populate the duplicated field name
                            String duplicateNameField = s_uniqueConstraints.get(constraintName);
                            if (duplicateNameField != null) {
                                ee = new KapuaDuplicateNameException(duplicateNameField);
                            }
                        }
                        break;

                    // SQL Error: 1048, SQLSTATE: 23000 - ER_BAD_NULL_ERROR - Not Null Violation
                    case 1048:
                        if ("23000".equals(sqlErrorMsg)) {

                            //
                            // Extract the name of the null attribute
                            // e.g. SQL Message: Column '%s' cannot be null
                            String message = cve.getSQLException().getMessage();
                            String[] parts = message.split("'");
                            String columnName = null;
                            if (parts.length == 3) {
                                columnName = parts[1];
                            }

                            //
                            // populate the null field name
                            if (columnName != null) {
                                ee = new KapuaIllegalNullArgumentException(columnName);
                            }

                        }
                        break;
                }
            }
        }
        // Handle all other Exceptions
        if (ee == null) {
            ee = KapuaException.internalError(rootException, "Error during Persistence Operation");
        }
        return ee;
    }
}
