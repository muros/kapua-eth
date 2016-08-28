package org.eclipse.kapua.service.datastore.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.commons.util.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.util.EntityManagerFactory;

public class DatastoreEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory
{
    private static final String                PERSISTENCE_UNIT_NAME = "kapua-datastore";
    private static final String                DATASOURCE_NAME       = "kapua-dbpool";
    private static final Map<String, String>   s_uniqueConstraints   = new HashMap<>();

    private static DatastoreEntityManagerFactory instance              = new DatastoreEntityManagerFactory();

    private DatastoreEntityManagerFactory()
    {
        super(PERSISTENCE_UNIT_NAME,
              DATASOURCE_NAME,
              s_uniqueConstraints);
    }
    
    public static DatastoreEntityManagerFactory getInstance()
    {
        return instance;
    }
}
