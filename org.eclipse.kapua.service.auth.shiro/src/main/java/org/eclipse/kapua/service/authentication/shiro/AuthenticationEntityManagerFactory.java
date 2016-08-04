package org.eclipse.kapua.service.authentication.shiro;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.EntityManager;

public class AuthenticationEntityManagerFactory extends org.eclipse.kapua.commons.util.AbstractEntityManagerFactory
{
    private static final String                       PERSISTENCE_UNIT_NAME = "kapua-authentication";
    private static final String                       DATASOURCE_NAME       = "kapua-dbpool";
    private static final Map<String, String>          s_uniqueConstraints   = new HashMap<>();

    private static AuthenticationEntityManagerFactory instance              = new AuthenticationEntityManagerFactory();

    private AuthenticationEntityManagerFactory()
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
