package org.eclipse.kapua.app.console.shared.model;

import java.util.Stack;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

public interface EdcPagingLoadResult<Data> extends PagingLoadResult<Data> {

    public int getVirtualOffset();

    public void setVirtualOffset(int offset);

    public Stack<EdcBasePagingCursor> getCursorOffset();

    public int getLastOffset();
}
