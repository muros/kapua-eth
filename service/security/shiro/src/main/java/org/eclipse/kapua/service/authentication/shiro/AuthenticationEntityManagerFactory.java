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
package org.eclipse.kapua.service.authentication.shiro;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.AbstractEntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManager;

/**
 * Entity manager factory for the authentication module.
 *
 * @since 1.0
 *
 */
public class AuthenticationEntityManagerFactory extends AbstractEntityManagerFactory
{
    private static final String                       PERSISTENCE_UNIT_NAME = "kapua-authentication";
    private static final String                       DATASOURCE_NAME       = "kapua-dbpool";
    private static final Map<String, String>          s_uniqueConstraints   = new HashMap<>();

    private static AuthenticationEntityManagerFactory instance              = new AuthenticationEntityManagerFactory();

    /**
     * Constructs a new entity manager factory and configure it to use the authentication persistence unit.
     */
    private AuthenticationEntityManagerFactory()
    {
        super(PERSISTENCE_UNIT_NAME,
              DATASOURCE_NAME,
              s_uniqueConstraints);
    }

    /**
     * Return the {@link EntityManager} singleton instance
     * 
     * @return
     * @throws KapuaException
     */
    public static EntityManager getEntityManager()
        throws KapuaException
    {
        return instance.createEntityManager();
    }
}
