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
package org.eclipse.kapua.service.datastore.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.StorableQuery;
import org.eclipse.kapua.service.datastore.StorableResultList;
import org.eclipse.kapua.service.datastore.TopicStoreService;
import org.eclipse.kapua.service.datastore.model.StorableId;
import org.eclipse.kapua.service.datastore.model.TopicInfo;

public class TopicStoreServiceImpl implements TopicStoreService
{

    @Override
    public void delete(KapuaId scopeId, StorableId uuid)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public TopicInfo find(KapuaId scopeId, StorableId uuid)
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StorableResultList<TopicInfo> query(KapuaId scopeId, StorableQuery<TopicInfo> query)
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
