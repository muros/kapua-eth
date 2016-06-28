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
import org.eclipse.kapua.service.datastore.model.TopicInfo;

public interface TopicStoreService extends KapuaService
{
    public void delete(String scopeName, String uuid) throws KapuaException;
    public TopicInfo find(String scopeName, String uuid) throws KapuaException;
    public StorableResultList<TopicInfo> query(String scopeName, StorableQuery<TopicInfo> query) throws KapuaException;
}
