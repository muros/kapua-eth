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
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import java.util.ArrayList;

public class KapuaListResult<T> extends ArrayList<T> {

    private static final long serialVersionUID = 7198708741359891625L;

    private Object m_nextKeyOffset;
    private int    m_totalCount;

    public KapuaListResult() {
        m_nextKeyOffset = null;
        m_totalCount    = -1;
    }

    public KapuaListResult(Object nextKeyOffset) {
        m_nextKeyOffset = nextKeyOffset;
        m_totalCount    = -1;
    }

    public KapuaListResult(Object nextKeyOffset, int totalCount) {
        this(nextKeyOffset);
        m_totalCount = totalCount;
    }

    public Object nextKeyOffset() {
        return m_nextKeyOffset;
    }

    public Object getNextKeyOffset() {
        return m_nextKeyOffset;
    }

    public int getTotalCount() {
        return m_totalCount;
    }
}

