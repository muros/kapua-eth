/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.jpa;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSqlScriptExecutor
{
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(SimpleSqlScriptExecutor.class);

    private static String WILDCAR_ANY = "*";

    private static String RUN_SCRIPT_CMD = "RUNSCRIPT FROM '%s'";

    private static String DEFAULT_SCRIPTS_PATH = "src/main/sql/H2";

    // Members

    private List<String> queryStrings = new ArrayList<>();

    // Operations

    public void clearQueries()
    {
        queryStrings.clear();
    }
    
    public List<String> getQueries()
    {
        return Collections.unmodifiableList(queryStrings);
    }
    
    public SimpleSqlScriptExecutor scanScripts(String scanPath, String filenameFilter) {
        
        String prefix = "";
        String suffix = "";
        if(filenameFilter.contains(WILDCAR_ANY)) {
            int pos = filenameFilter.indexOf(WILDCAR_ANY);
            prefix = filenameFilter.substring(0, pos);
            suffix = filenameFilter.substring(pos+1);
        }
           
        final String finalPrefix = prefix;
        final String finalSuffix = suffix;
        
        FilenameFilter sqlfilter = (dir, name) -> {
            if (finalPrefix.isEmpty() && finalSuffix.isEmpty())
                return filenameFilter.equals(name);

            if (!finalPrefix.isEmpty() && !name.startsWith(finalPrefix))
                return false;

            if (!finalSuffix.isEmpty() && !name.endsWith(finalSuffix))
                return false;

            return true;
        };
        
        String[] dirContents = new String[] {};
        File sqlDir = new File(scanPath);
        if (sqlDir.isDirectory()) {
            dirContents = sqlDir.list(sqlfilter);
        }
        
        List<String> dropScripts= new ArrayList<String>();
        List<String> createScripts= new ArrayList<String>();
        List<String> seedScripts= new ArrayList<String>();
        
        String sep = String.valueOf(File.separatorChar);
        for(String sqlItem:dirContents) {
            String sqlFileName = scanPath + (scanPath.endsWith(sep) ? "" : sep) + sqlItem;
            File sqlFile = new File(sqlFileName);
            if(sqlFile.isFile() && sqlItem.endsWith("_drop.sql"))
                dropScripts.add(String.format(RUN_SCRIPT_CMD,sqlFileName));
            if(sqlFile.isFile() && sqlItem.endsWith("_create.sql"))
                createScripts.add(String.format(RUN_SCRIPT_CMD,sqlFileName));
            if(sqlFile.isFile() && sqlItem.endsWith("_seed.sql"))
                seedScripts.add(String.format(RUN_SCRIPT_CMD,sqlFileName));
        }
        
        this.addQueries(dropScripts);
        this.addQueries(createScripts);
        this.addQueries(seedScripts);
        return this;
    }

    public SimpleSqlScriptExecutor scanScripts(String filenameFilter) {
        return scanScripts(DEFAULT_SCRIPTS_PATH, filenameFilter);
    }

        public SimpleSqlScriptExecutor addQuery(String sqlString)
    {
        this.queryStrings.add(sqlString);
        return this;
    }
    
    public SimpleSqlScriptExecutor addQueries(List<String> sqlStrings)
    {
        if (sqlStrings==null)
            return this;
        
        this.queryStrings.addAll(sqlStrings);
        
        return this;
    }
    
    public int executeUpdate(EntityManager entityManager)
    {
        int i=0;
        
        for(String qStr:queryStrings) {
            logger.info("Running script: " + qStr);
            Query q = entityManager.createNativeQuery(qStr);
            q.executeUpdate();
            i++;
        }
        
        return i;
    }
}
