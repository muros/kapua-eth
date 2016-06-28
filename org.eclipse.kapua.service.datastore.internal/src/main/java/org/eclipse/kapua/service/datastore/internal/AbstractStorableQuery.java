/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal;

import org.eclipse.kapua.service.datastore.Storable;
import org.eclipse.kapua.service.datastore.StorableQuery;

public abstract class AbstractStorableQuery<E extends Storable> implements StorableQuery<E>
{
    private int    limit;
    private Object keyOffset;
    private int    indexOffset;

    public AbstractStorableQuery() {
        limit = 50;
        keyOffset = null;
        indexOffset = 0;
    }

    public Object getKeyOffset() {
        return keyOffset;
    }

    public StorableQuery<E> setKeyOffset(Object offset) {
        this.keyOffset = offset;
        return this;
    }

    public int getIndexOffset() {
        return indexOffset;
    }

    public StorableQuery<E> setIndexOffset(int offset) {
        this.indexOffset = offset;
        return this;
    }

    public int addIndexOffset(int delta)
    {
        indexOffset += delta;
        return indexOffset;
    }

    public StorableQuery<E> setLimit(int limit)
    {
        this.limit = limit;
        return this;
    }

    public int getLimit() {
        return limit;
    }
}
