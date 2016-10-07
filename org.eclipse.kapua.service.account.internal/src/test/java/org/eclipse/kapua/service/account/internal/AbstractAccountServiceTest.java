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


import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceSchemaUtils;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.jpa.SimpleSqlScriptExecutor;
import org.eclipse.kapua.test.KapuaTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAccountServiceTest extends KapuaTest
{
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AbstractAccountServiceTest.class);
    
    public static String DEFAULT_PATH = "src/main/sql/H2";
    public static String DEFAULT_COMMONS_PATH = "../commons";
    public static String DEFAULT_FILTER = "act_*.sql";
    public static String DROP_FILTER = "act_*_drop.sql";


    public static void scriptSession(String path, String fileFilter)
    {
        EntityManager em = null;
        try {
            
            logger.info("Running database scripts...");
            
            em = AccountEntityManagerFactory.getInstance().createEntityManager();
            em.beginTransaction();
            
            SimpleSqlScriptExecutor sqlScriptExecutor = new SimpleSqlScriptExecutor();
            sqlScriptExecutor.scanScripts(path, fileFilter);
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
    	KapuaConfigurableServiceSchemaUtils.createSchemaObjects(DEFAULT_COMMONS_PATH);
        scriptSession(DEFAULT_PATH, DEFAULT_FILTER);
    }
    
    @AfterClass
    public static void tearDown()
    {
        scriptSession(DEFAULT_PATH, DROP_FILTER);
    	KapuaConfigurableServiceSchemaUtils.dropSchemaObjects(DEFAULT_COMMONS_PATH);

    }
}
