package org.eclipse.kapua.app.console.shared.model;

import java.util.Stack;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public interface EdcPagingLoadConfig extends PagingLoadConfig {

    public void setLastOffset(int lastOffset);

    public Stack<EdcBasePagingCursor> getOffsetCursors();
}
