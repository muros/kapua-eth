/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
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
package org.eclipse.kapua.service;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;

/**
 * Common interface for all KapuaService that are managing identifiable entities.
 *
 * @param <E> - Class of the KapuaEntity being managed by this Service
 * @param <C> - Creator Class of the KapuaEntity being managed by this Service
 */
public interface KapuaEntityService<E extends KapuaEntity, C extends KapuaEntityCreator<E>> extends KapuaService
{
    public E create(C creator)
        throws KapuaException;

    public E find(KapuaId scopeId, KapuaId entityId)
        throws KapuaException;

    public KapuaListResult<E> query(KapuaQuery<E> query)
        throws KapuaException;

    public long count(KapuaQuery<E> query)
        throws KapuaException;

    public void delete(KapuaId scopeId, KapuaId entityId)
        throws KapuaException;
}
