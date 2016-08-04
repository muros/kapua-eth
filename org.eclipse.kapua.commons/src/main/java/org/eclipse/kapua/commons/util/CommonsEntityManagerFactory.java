package org.eclipse.kapua.commons.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;

public class CommonsEntityManagerFactory extends org.eclipse.kapua.commons.util.AbstractEntityManagerFactory
{
    private static final String                PERSISTENCE_UNIT_NAME = "kapua-commons";
    private static final String                DATASOURCE_NAME       = "kapua-dbpool";
    private static final Map<String, String>   s_uniqueConstraints   = new HashMap<>();

    private static CommonsEntityManagerFactory instance              = new CommonsEntityManagerFactory();

    private CommonsEntityManagerFactory()
    {
        super(PERSISTENCE_UNIT_NAME,
              DATASOURCE_NAME,
              s_uniqueConstraints);
    }

    public static EntityManager getEntityManager()
        throws KapuaException
    {
        return instance.createEntityManager();
    }
}
