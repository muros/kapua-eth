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
package org.eclipse.kapua.service.datastore.internal.model.query;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.service.datastore.internal.AbstractStorableQuery;
import org.eclipse.kapua.service.datastore.model.Message;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;

public class StorableMessageQueryImpl extends AbstractStorableQuery<Message> implements MessageQuery
{
    private long              startDate     = -1;
    private long              endDate       = -1;
    private boolean           askTotalCount = false;
    private boolean           ascendingSort = false;
    private MessageFetchStyle fetchStyle    = MessageFetchStyle.METADATA_HEADERS_PAYLOAD;
    private List<String>      keys          = new ArrayList<String>();

    public StorableMessageQueryImpl()
    {
    }

    public MessageQuery setDateRange(long startDate, long endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    public long getStartDate()
    {
        return startDate;
    }

    public MessageQuery setStartDate(long startDate)
    {
        this.startDate = startDate;
        return this;
    }

    public long getEndDate()
    {
        return endDate;
    }

    public MessageQuery setEndDate(long endDate)
    {
        this.endDate = endDate;
        return this;
    }

    public MessageFetchStyle getFetchStyle()
    {
        return fetchStyle;
    }

    public MessageQuery setFetchStyle(MessageFetchStyle fetchStyle)
    {
        this.fetchStyle = fetchStyle;
        return this;
    }

    public MessageQuery setKeyOffset(Object offset)
    {
        super.setKeyOffset(offset);
        return this;
    }

    public MessageQuery setIndexOffset(int offset)
    {
        super.setIndexOffset(offset);
        return this;
    }

    public MessageQuery setLimit(int limit)
    {
        super.setLimit(limit);
        return this;
    }

    public boolean includePayload()
    {
        if (fetchStyle == MessageFetchStyle.METADATA_HEADERS_PAYLOAD) {
            return true;
        }
        return false;
    }

    public boolean includeHeaders()
    {
        if (fetchStyle == MessageFetchStyle.METADATA_HEADERS_PAYLOAD ||
            fetchStyle == MessageFetchStyle.METADATA_HEADERS) {
            return true;
        }
        return false;
    }

    public boolean isAskTotalCount()
    {
        return askTotalCount;
    }

    public void setAskTotalCount(boolean m_askTotalCount)
    {
        this.askTotalCount = m_askTotalCount;
    }

    public boolean isAscendingSort()
    {
        return ascendingSort;
    }

    public void setAscendingSort(boolean ascendingSort)
    {
        this.ascendingSort = ascendingSort;
    }

    public boolean isDescendingSort()
    {
        return !ascendingSort;
    }

    public void setDescendingSort(boolean descendingSort)
    {
        this.ascendingSort = !descendingSort;
    }

    public void setKeys(List<String> key)
    {
        this.keys = key;
    }

    public void addKey(String key)
    {
        this.keys.add(key);
    }

    public List<String> getKeys()
    {
        return keys;
    }
}
