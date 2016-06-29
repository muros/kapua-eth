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
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.datastore.model.Message;
import org.eclipse.kapua.service.datastore.model.MessageCreator;
import org.eclipse.kapua.service.datastore.model.StorableId;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;

public interface MessageStoreService extends KapuaService,
                                     KapuaConfigurableService
{
    public StorableId store(String scopeName, MessageCreator message)
        throws KapuaException;

    public void delete(String scopeName, StorableId id)
        throws KapuaException;

    public Message find(String scopeName, StorableId id, MessageFetchStyle fetchStyle)
        throws KapuaException;

    public StorableResultList<Message> query(String scopeName, StorableQuery<Message> query)
        throws KapuaException;
}
