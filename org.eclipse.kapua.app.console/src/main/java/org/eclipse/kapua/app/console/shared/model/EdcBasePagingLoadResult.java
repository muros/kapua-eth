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
package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;

public class EdcBasePagingLoadResult<Data> extends BasePagingLoadResult<Data> implements EdcPagingLoadResult<Data>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7139907276082424059L;

    protected int virtualOffset = 0;
    private int lastOffset = 0;
    private Stack<EdcBasePagingCursor> cursorOffset;

    EdcBasePagingLoadResult() {
        super(null);
    }

    public EdcBasePagingLoadResult(List<Data> data) {
        super(data);
    }

    public EdcBasePagingLoadResult(List<Data> data, int offset, int totalLength) {
        super(data, offset, totalLength);
    }

    public EdcBasePagingLoadResult(List<Data> data, int offset, int totalLength, int virtualOffset) {
        super(data, offset, totalLength);
        this.virtualOffset = virtualOffset;
    }

    public EdcBasePagingLoadResult(List<Data> data, int offset, int totalLength, int virtualOffset, Stack<EdcBasePagingCursor> cursors) {
        super(data, offset, totalLength);
        this.virtualOffset = virtualOffset;
        this.cursorOffset = cursors;
    }

    public int getVirtualOffset() {
        return virtualOffset;
    }

    public void setVirtualOffset(int virtualOffset) {
        this.virtualOffset = virtualOffset;
    }

    public Stack<EdcBasePagingCursor> getCursorOffset() {
        return cursorOffset;
    }

    public void setCursorOffset(Stack<EdcBasePagingCursor> cursorOffset) {
        this.cursorOffset = cursorOffset;
    }

    public int getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(int lastOffset) {
        this.lastOffset = lastOffset;
    }


}
