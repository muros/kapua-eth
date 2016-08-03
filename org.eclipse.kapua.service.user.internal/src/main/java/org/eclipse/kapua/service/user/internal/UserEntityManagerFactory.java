package org.eclipse.kapua.service.user.internal;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaException;

public class UserEntityManagerFactory extends org.eclipse.kapua.commons.util.JpaUtils
{
    private static final String              PERSISTENCE_UNIT_NAME = "kapua-user";
    private static final String              DATASOURCE_NAME       = "kapua-dbpool";
    private static final Map<String, String> s_uniqueConstraints   = new HashMap<>();

    private static UserEntityManagerFactory                  t                     = new UserEntityManagerFactory();

    private UserEntityManagerFactory()
    {
        super(PERSISTENCE_UNIT_NAME,
              DATASOURCE_NAME,
              s_uniqueConstraints);
    }

    public static EntityManager getEntityManager()
        throws KapuaException
    {
        return t.createEntityManager();
    }
}
