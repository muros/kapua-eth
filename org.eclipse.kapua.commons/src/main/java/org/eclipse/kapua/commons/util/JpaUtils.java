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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaOptimisticLockingException;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
//import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for JPA operations
 */
public class JpaUtils
{
    private static final Logger              LOG                     = LoggerFactory.getLogger(JpaUtils.class);
    private static final String              QUERY_SELECT_UUID_SHORT = "SELECT UUID_SHORT() FROM DUAL";

    private static final Map<String, String> s_uniqueConstraints     = new HashMap<>();
    private EntityManagerFactory             entityManagerFactory;

    protected JpaUtils(String persistenceUnitName, String datasourceName, Map<String, String> uniqueConstraints)
    {
        //
        // Initialize the EntityManagerFactory
        try {

            SystemSetting config = SystemSetting.getInstance();

            // Mandatory connection parameters
            String dbName = config.getString(SystemSettingKey.DB_NAME);
            String dbConnectionScheme = config.getString(SystemSettingKey.DB_CONNECTION_SCHEME);
            String dbConnectionHost = config.getString(SystemSettingKey.DB_CONNECTION_HOST);
            String dbConnectionPort = config.getString(SystemSettingKey.DB_CONNECTION_PORT);

            StringBuilder dbConnectionString = new StringBuilder().append(dbConnectionScheme)
                                                                  .append("://")
                                                                  .append(dbConnectionHost)
                                                                  .append(":")
                                                                  .append(dbConnectionPort)
                                                                  .append("/")
                                                                  .append(dbName)
                                                                  .append("?");

            // Optional connection parameters
            String useTimezone = config.getString(SystemSettingKey.DB_USE_TIMEZIONE);
            if (useTimezone != null) {
                dbConnectionString.append("useTimezone=")
                                  .append(useTimezone)
                                  .append("&");
            }

            String useLegacyDatetimeCode = config.getString(SystemSettingKey.DB_USE_LEGACY_DATETIME_CODE);
            if (useLegacyDatetimeCode != null) {
                dbConnectionString.append("useLegacyDatetimeCode=")
                                  .append(useLegacyDatetimeCode)
                                  .append("&");
            }

            String serverTimezone = config.getString(SystemSettingKey.DB_SERVER_TIMEZONE);
            if (serverTimezone != null) {
                dbConnectionString.append("serverTimezone=")
                                  .append(serverTimezone)
                                  .append("&");
            }

            String characterEncoding = config.getString(SystemSettingKey.DB_CHAR_ENCODING);
            if (characterEncoding != null) {
                dbConnectionString.append("characterEncoding=")
                                  .append(characterEncoding)
                                  .append("&");
            }

            // This deletes the trailing '?' or '&'
            dbConnectionString.deleteCharAt(dbConnectionString.length() - 1);

            //
            // JPA configuration overrides
            Map<String, Object> configOverrides = new HashMap<String, Object>();
            configOverrides.put("eclipselink.connection-pool.default.url", dbConnectionString.toString());
            configOverrides.put("eclipselink.connection-pool.default.user", config.getString(SystemSettingKey.DB_USERNAME));
            configOverrides.put("eclipselink.connection-pool.default.password", config.getString(SystemSettingKey.DB_PASSWORD));

            configOverrides.put("eclipselink.connection-pool.default.dataSourceName", datasourceName);
            configOverrides.put("eclipselink.connection-pool.default.initial", config.getString(SystemSettingKey.DB_POOL_SIZE_INITIAL));
            configOverrides.put("eclipselink.connection-pool.default.min", config.getString(SystemSettingKey.DB_POOL_SIZE_MIN));
            configOverrides.put("eclipselink.connection-pool.default.max", config.getString(SystemSettingKey.DB_POOL_SIZE_MAX));
            configOverrides.put("eclipselink.connection-pool.default.wait", config.getString(SystemSettingKey.DB_POOL_BORROW_TIMEOUT));

            if (config.getBoolean(SystemSettingKey.OSGI_CONTEXT)) {
                // OSGi JPA
                // Could get this by wiring up OsgiTestBundleActivator as well.
                org.osgi.framework.Bundle thisBundle = org.osgi.framework.FrameworkUtil.getBundle(JpaUtils.class);
                org.osgi.framework.BundleContext context = thisBundle.getBundleContext();
                LOG.info(">>> Bundle context: {}", context);

                @SuppressWarnings("rawtypes")
                org.osgi.framework.ServiceReference serviceReference = context.getServiceReference(PersistenceProvider.class.getName());
                LOG.info(">>> Service Reference: {}", serviceReference);

                @SuppressWarnings("unchecked")
                PersistenceProvider persistenceProvider = (PersistenceProvider) context.getService(serviceReference);
                LOG.info(">>> Persistence Provider: {}", persistenceProvider);

                entityManagerFactory = persistenceProvider.createEntityManagerFactory(persistenceUnitName,
                                                                                      configOverrides);
            }
            else {

                // Standalone JPA
                entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName,
                                                                              configOverrides);
            }
        }
        catch (Throwable ex) {
            LOG.error("Error creating EntityManagerFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }

        //
        // Set unique constrains for this persistence unit
        // FIXME: this is needed? With EclipseLink we lost the ConstraintViolationException.
        for (Entry<String, String> uc : uniqueConstraints.entrySet()) {
            s_uniqueConstraints.put(uc.getKey(), uc.getValue());
        }
    }

    public static void cleanUpDataSource()
        throws Exception
    {
        //
        // JDBC AbandonedConnectionCleanupThread clean up
        // FIXME: close clean up thread.
        // try {
        // s_logger.info("Closing AbandonedConnectionCleanupThread...");
        // AbandonedConnectionCleanupThread.shutdown();
        // s_logger.info("Closed AbandonedConnectionCleanupThread!");
        // }
        // catch (InterruptedException e) {
        // s_logger.error("Error while closing AbandonedConnectionCleanupThread", e);
        // }
    }

    /**
     * Returns an EntityManager instance.
     * 
     * @return An entity manager for the persistence unit.
     * @throws KapuaException If {@link EntityManagerFactory#createEntityManager()} cannot create the {@link EntityManager}
     * 
     * @since 1.0.0
     */
    public EntityManager createEntityManager()
        throws KapuaException
    {
        EntityManager em = entityManagerFactory.createEntityManager();
        if (em == null) {
            throw KapuaException.internalError("Cannot create an EntityManager");
        }
        return em;
    }

    /**
     * Opens a Jpa Transaction.<br/>
     * <br/>
     * The transaction MUST be closed after being commited or rollbacked, using {@link JpaUtils#close(EntityManager)}
     * 
     * @param em The {@link EntityManager} on which start the transaction.
     * @throws KapuaException if given {@link EntityManager} is {@code null}
     * 
     * @since 1.0.0
     */
    public static void beginTransaction(EntityManager em)
        throws KapuaException
    {
        if (em == null) {
            throw KapuaException.internalError(new NullPointerException(), "null EntityManager");
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
            LOG.warn("Rollback Error", e);
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

    public static BigInteger generateUuidShort()
        throws KapuaException
    {
        return null;
        //
        // EntityManager em = entityManagerFactory.createEntityManager();
        // if (em == null) {
        // throw KapuaException.internalError("Cannot create an EntityManager");
        // }
        //
        // Query q = em.createNativeQuery(QUERY_SELECT_UUID_SHORT);
        // return (BigInteger) q.getSingleResult();
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
            // FIXME: find the constraint violation exception in eclipse LINK
            // if (t instanceof ConstraintViolationException) {
            //
            // // Handle Unique Constraints Exception
            // ConstraintViolationException cve = (ConstraintViolationException) t;
            //
            // int sqlErrorCode = cve.getErrorCode();
            // String sqlErrorMsg = cve.getSQLState();
            // switch (sqlErrorCode) {
            //
            // // SQL Error: 1062, SQLState: 23000 - ER_DUP_KEYNAME - Unique Constraints Exception
            // case 1062:
            // if ("23000".equals(sqlErrorMsg)) {
            //
            // //
            // // Extract the constraint name
            // // e.g. SQL Message: Duplicate entry 'test_account_1,322,584,746,357' for key 'uc_accountName'
            // String message = cve.getSQLException().getMessage();
            // String[] parts = message.split("'");
            // String constraintName = parts[parts.length - 1];
            //
            // //
            // // populate the duplicated field name
            // String duplicateNameField = s_uniqueConstraints.get(constraintName);
            // if (duplicateNameField != null) {
            // ee = new KapuaDuplicateNameException(duplicateNameField);
            // }
            // }
            // break;
            //
            // // SQL Error: 1048, SQLSTATE: 23000 - ER_BAD_NULL_ERROR - Not Null Violation
            // case 1048:
            // if ("23000".equals(sqlErrorMsg)) {
            //
            // //
            // // Extract the name of the null attribute
            // // e.g. SQL Message: Column '%s' cannot be null
            // String message = cve.getSQLException().getMessage();
            // String[] parts = message.split("'");
            // String columnName = null;
            // if (parts.length == 3) {
            // columnName = parts[1];
            // }
            //
            // //
            // // populate the null field name
            // if (columnName != null) {
            // ee = new KapuaIllegalNullArgumentException(columnName);
            // }
            //
            // }
            // break;
            // }
            // }
        }
        // Handle all other Exceptions
        if (ee == null) {
            ee = KapuaException.internalError(rootException, "Error during Persistence Operation");
        }
        return ee;
    }
}
