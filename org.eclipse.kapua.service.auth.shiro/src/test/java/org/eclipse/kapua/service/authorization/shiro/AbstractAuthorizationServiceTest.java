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


import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.service.authentication.shiro.AuthenticationEntityManagerFactory;
import org.eclipse.kapua.service.authorization.shiro.setting.KapuaAuthorizationSetting;
import org.eclipse.kapua.service.authorization.shiro.setting.KapuaAuthorizationSettingKeys;
import org.eclipse.kapua.test.SimpleNativeQueryExecutor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAuthorizationServiceTest extends Assert
{
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AbstractAuthorizationServiceTest.class);
    
    
    private static String DEFAULT_PATH = "src/main/sql/H2";
    private static String PATH = KapuaAuthorizationSetting.getInstance().getString(KapuaAuthorizationSettingKeys.UNUSED, DEFAULT_PATH);
    private static String ATHZ_ROLE_DROP_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_role_drop.sql'", PATH);
    private static String ATHZ_ROLE_CREATE_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_role_create.sql'", PATH);
    private static String ATHZ_ROLE_SEED_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_role_seed.sql'", PATH);
    
    private static String ATHZ_ROLE_PERMISSION_DROP_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_role_permission_drop.sql'", PATH);
    private static String ATHZ_ROLE_PERMISSION_CREATE_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_role_permission_create.sql'", PATH);
    private static String ATHZ_ROLE_PERMISSION_SEED_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_role_permission_seed.sql'", PATH);
    
    private static String ATHZ_USER_PERMISSION_DROP_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_user_permission_drop.sql'", PATH);
    private static String ATHZ_USER_PERMISSION_CREATE_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_user_permission_create.sql'", PATH);
    private static String ATHZ_USER_PERMISSION_SEED_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_user_permission_seed.sql'", PATH);
    
    private static String ATHZ_USER_ROLE_DROP_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_user_role_drop.sql'", PATH);
    private static String ATHZ_USER_ROLE_CREATE_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_user_role_create.sql'", PATH);
    private static String ATHZ_USER_ROLE_SEED_SCRIPT = String.format("RUNSCRIPT FROM '%s/athz_user_role_seed.sql'", PATH);

    @BeforeClass
    public static void tearUp()
        throws KapuaException
    {
        EntityManager em = null;
        try {
            
            logger.info("Running database scripts...");
            
            em = AuthenticationEntityManagerFactory.getEntityManager();
            em.beginTransaction();
            
            SimpleNativeQueryExecutor nativeQueryExecutor = new SimpleNativeQueryExecutor();
            nativeQueryExecutor.addQuery(em, ATHZ_ROLE_DROP_SCRIPT);
            nativeQueryExecutor.addQuery(em, ATHZ_ROLE_PERMISSION_DROP_SCRIPT);
            nativeQueryExecutor.addQuery(em, ATHZ_USER_PERMISSION_DROP_SCRIPT);
            nativeQueryExecutor.addQuery(em, ATHZ_USER_ROLE_DROP_SCRIPT);
            
            nativeQueryExecutor.addQuery(em, ATHZ_ROLE_CREATE_SCRIPT);
            nativeQueryExecutor.addQuery(em, ATHZ_ROLE_PERMISSION_CREATE_SCRIPT);
            nativeQueryExecutor.addQuery(em, ATHZ_USER_PERMISSION_CREATE_SCRIPT);
            nativeQueryExecutor.addQuery(em, ATHZ_USER_ROLE_CREATE_SCRIPT);
            
            nativeQueryExecutor.addQuery(em, ATHZ_ROLE_SEED_SCRIPT);
            nativeQueryExecutor.addQuery(em, ATHZ_ROLE_PERMISSION_SEED_SCRIPT);
            nativeQueryExecutor.addQuery(em, ATHZ_USER_PERMISSION_SEED_SCRIPT);
            nativeQueryExecutor.addQuery(em, ATHZ_USER_ROLE_SEED_SCRIPT);

            nativeQueryExecutor.executeUpdate();
            
            em.commit();
            
            logger.info("...database scripts done!");
        }
        catch (KapuaException e) {
            logger.error("Database scripts failed!", e);
            if (em != null)
                em.rollback();
        }
        finally {
            if (em != null)
                em.close();
        }

    }
    
    @AfterClass
    public static void tearDown()
    {
    }
}
