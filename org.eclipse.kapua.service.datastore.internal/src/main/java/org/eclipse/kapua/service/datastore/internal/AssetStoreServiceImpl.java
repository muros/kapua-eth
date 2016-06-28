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
import org.eclipse.kapua.service.datastore.AssetStoreService;
import org.eclipse.kapua.service.datastore.StorableQuery;
import org.eclipse.kapua.service.datastore.StorableResultList;
import org.eclipse.kapua.service.datastore.model.AssetInfo;

public class AssetStoreServiceImpl implements AssetStoreService
{

    @Override
    public void delete(String scopeName, String uuid)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public AssetInfo find(String scopeName, String uuid)
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StorableResultList<AssetInfo> query(String scopeName, StorableQuery<AssetInfo> query)
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
