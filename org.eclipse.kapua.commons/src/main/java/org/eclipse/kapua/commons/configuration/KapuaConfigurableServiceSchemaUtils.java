package org.eclipse.kapua.commons.configuration;

import java.io.File;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.CommonsEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.jpa.SimpleSqlScriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KapuaConfigurableServiceSchemaUtils {
	
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(KapuaConfigurableServiceSchemaUtils.class);
    
    public static String DEFAULT_PATH = "src/main/sql/H2";
    public static String DEFAULT_FILTER = "sys_*.sql";
    public static String DROP_FILTER = "sys_*_drop.sql";

    public static void scriptSession(String path, String fileFilter)
    {
        EntityManager em = null;
        try {
            
            logger.info("Running database scripts...");
            
            em = CommonsEntityManagerFactory.getEntityManager();
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
    
    public static void createSchemaObjects(String path)
        throws KapuaException
    {
    	String pathSep = String.valueOf(File.separatorChar);
    	String sep = path.endsWith(pathSep) ? "" : pathSep;
        scriptSession(path + sep + DEFAULT_PATH, DEFAULT_FILTER);
    }
    
    public static void dropSchemaObjects(String path)
    {
    	String pathSep = String.valueOf(File.separatorChar);
    	String sep = path.endsWith(pathSep) ? "" : pathSep;
        scriptSession(path + sep + DEFAULT_PATH, DROP_FILTER);
    }
}
