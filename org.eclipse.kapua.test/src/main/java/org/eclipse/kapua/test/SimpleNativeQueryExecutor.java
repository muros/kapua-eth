package org.eclipse.kapua.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.eclipse.kapua.commons.jpa.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleNativeQueryExecutor
{
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(SimpleNativeQueryExecutor.class);

    private List<Query> queries;
    
    public SimpleNativeQueryExecutor()
    {
        queries = new ArrayList<Query>();
    }
    
    public void clearQueries()
    {
        queries.clear();
    }
    
    public List<Query> getQueries()
    {
        return Collections.unmodifiableList(queries);
    }
    
    public SimpleNativeQueryExecutor addQuery(EntityManager entityManager, String sqlString)
    {
        Query q = entityManager.createNativeQuery(sqlString);
        this.queries.add(q);
        return this;
    }
    
    public int executeUpdate()
    {
        int i=0;
        
        for(Query q:queries) {
            logger.info("Running " + q.toString());
            q.executeUpdate();
            i++;
        }
        
        return i;
    }
}
