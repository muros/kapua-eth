package org.eclipse.kapua.service.account.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManager;

public class AccountEntityManagerFactory extends AbstractEntityManagerFactory
{
    private static final String                PERSISTENCE_UNIT_NAME = "kapua-account";
    private static final String                DATASOURCE_NAME       = "kapua-dbpool";
    private static final Map<String, String>   s_uniqueConstraints   = new HashMap<>();

    private static AccountEntityManagerFactory instance              = new AccountEntityManagerFactory();

    private AccountEntityManagerFactory()
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
