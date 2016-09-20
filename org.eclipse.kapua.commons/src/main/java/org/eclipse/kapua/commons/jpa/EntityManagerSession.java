/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.kapua.commons.jpa;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.KapuaExceptionUtils;

public class EntityManagerSession {

    private final AbstractEntityManagerFactory entityManagerFactory;

    public EntityManagerSession(AbstractEntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public <T> void onEntityAction(EntityManagerActionCallback entityManagerActionCallback) throws KapuaException {
        EntityManager manager = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            entityManagerActionCallback.actionOn(manager);
        } catch (KapuaException e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }
    }

    public <T> T onEntityResult(EntityManagerResultCallback<T> entityManagerResultCallback) throws KapuaException {
        EntityManager manager = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            return entityManagerResultCallback.onEntityManager(manager);
        } catch (KapuaException e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }
    }

}
