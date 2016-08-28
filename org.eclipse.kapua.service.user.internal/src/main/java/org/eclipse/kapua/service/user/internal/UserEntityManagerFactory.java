package org.eclipse.kapua.service.user.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserEntityManagerFactory extends AbstractEntityManagerFactory implements EntityManagerFactory
{
    @SuppressWarnings("unused")
    private static final Logger              LOG                 = LoggerFactory.getLogger(UserEntityManagerFactory.class);

    private static final String              PERSISTENCE_UNIT_NAME = "kapua-user";
    private static final String              DATASOURCE_NAME       = "kapua-dbpool";
    private static final Map<String, String> uniqueConstraints   = new HashMap<>();

    private static UserEntityManagerFactory  instance              = new UserEntityManagerFactory();

    private UserEntityManagerFactory()
    {
        super(PERSISTENCE_UNIT_NAME, DATASOURCE_NAME, uniqueConstraints);
    }

    public static EntityManagerFactory getInstance()
    {
        return instance;
    }
}
