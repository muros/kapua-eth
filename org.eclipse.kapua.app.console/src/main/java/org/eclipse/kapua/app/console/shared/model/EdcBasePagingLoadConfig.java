package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;
import java.util.Stack;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;

public class EdcBasePagingLoadConfig extends BasePagingLoadConfig implements EdcPagingLoadConfig, Serializable {

    private static final long serialVersionUID = 8550045117054490792L;

    private int lastOffset;
    private Stack<EdcBasePagingCursor> offsetCursors;

    public EdcBasePagingLoadConfig() {
        super();
        offsetCursors = new Stack<EdcBasePagingCursor>();
    }

    public EdcBasePagingLoadConfig(int offset, int limit) {
        super(offset, limit);
    }

    public EdcBasePagingLoadConfig(int offset, int limit, Stack<EdcBasePagingCursor> offsetCursors) {
        super(offset, limit);
        this.offsetCursors = offsetCursors;
    }

    public int getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(int lastOffset) {
        this.lastOffset = lastOffset;
    }

    public Stack<EdcBasePagingCursor> getOffsetCursors() {
        return offsetCursors;
    }

    public void setOffsetCursors(Stack<EdcBasePagingCursor> offsetCursors) {
        this.offsetCursors = offsetCursors;
    }

}
