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
package org.eclipse.kapua.service.datastore.model.query;

import java.util.List;

import org.eclipse.kapua.service.datastore.StorableQuery;
import org.eclipse.kapua.service.datastore.model.Message;

public interface MessageQuery extends StorableQuery<Message>
{    
    public long getStartDate();

    public MessageQuery setStartDate(long startDate);
    
    public long getEndDate();

    public MessageQuery setEndDate(long endDate);

    public MessageFetchStyle getFetchStyle();
    
    public MessageQuery setFetchStyle(MessageFetchStyle fetchStyle);

    public boolean includePayload();
    
    public boolean includeHeaders();

    public boolean isAskTotalCount();

    public void setAskTotalCount(boolean m_askTotalCount);

    public boolean isAscendingSort();

    public void setAscendingSort(boolean ascendingSort);

    public boolean isDescendingSort();

    public void setDescendingSort(boolean descendingSort);
    
    public void setKeys(List<String> key);
    
    public void addKey(String key);
    
    public List<String> getKeys();
}
