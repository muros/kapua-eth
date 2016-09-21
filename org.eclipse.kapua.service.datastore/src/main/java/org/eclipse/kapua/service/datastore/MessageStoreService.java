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
package org.eclipse.kapua.service.datastore;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.datastore.model.Message;
import org.eclipse.kapua.service.datastore.model.MessageCreator;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.StorableId;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;

/**
 * Service responsible for storing and accessing telemetry data generated by devices. If given operation is not
 * supported by an underlying store implementation then {@link DataStoreOperationNotSupportedException} should be thrown.
 */
public interface MessageStoreService extends KapuaService,
                                     KapuaConfigurableService
{
    StorableId store(KapuaId scopeId, MessageCreator message)
        throws KapuaException;

    void delete(KapuaId scopeId, StorableId id)
        throws KapuaException;

    Message find(KapuaId scopeId, StorableId id, MessageFetchStyle fetchStyle)
        throws KapuaException;

    MessageListResult query(KapuaId scopeId, MessageQuery query)
        throws KapuaException;

    long count(KapuaId scopeId, MessageQuery query)
        throws KapuaException;

    void delete(KapuaId scopeId, MessageQuery query)
        throws KapuaException;
}
