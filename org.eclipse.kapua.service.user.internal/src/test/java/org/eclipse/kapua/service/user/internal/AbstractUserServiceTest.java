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
package org.eclipse.kapua.service.user.internal;


import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.service.user.internal.setting.KapuaUserSetting;
import org.eclipse.kapua.service.user.internal.setting.KapuaUserSettingKeys;
import org.eclipse.kapua.test.KapuaTest;
import org.eclipse.kapua.test.SimpleSqlScriptExecutor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUserServiceTest extends KapuaTest
{
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AbstractUserServiceTest.class);
    
    public static String DEFAULT_PATH = "./src/main/sql/H2/";
    public static String DEFAULT_FILTER = "usr_*.sql";
    public static String DROP_FILTER = "usr_*_drop.sql";

    public static void scriptSession(String fileFilter)
    {
        EntityManager em = null;
        try {
            
            logger.info("Running database scripts...");
            
            em = UserEntityManagerFactory.getEntityManager();
            em.beginTransaction();
                        
            String path = KapuaUserSetting.getInstance().getString(KapuaUserSettingKeys.USER_KEY, DEFAULT_PATH);
            String filter = KapuaUserSetting.getInstance().getString(KapuaUserSettingKeys.USER_KEY, fileFilter);
            SimpleSqlScriptExecutor sqlScriptExecutor = new SimpleSqlScriptExecutor();
            sqlScriptExecutor.scanScripts(path, filter);
            sqlScriptExecutor.executeUpdate(em);
            
            em.commit();
            
            logger.info("...database scripts done!");
        }
        catch (KapuaException e) {
            logger.error("Database scripts failed: {}", e.getMessage());
            if (em != null)
                em.rollback();
        }
        finally {
            if (em != null)
                em.close();
        }

    }
    
    @BeforeClass
    public static void tearUp()
        throws KapuaException
    {
        scriptSession(DEFAULT_FILTER);
    }
    
    @AfterClass
    public static void tearDown()
    {
        scriptSession(DROP_FILTER);
    }
}
