package org.eclipse.kapua.app.console.client.widget;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

public class EdcPagingToolBar extends PagingToolBar {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);
    private boolean triggeredRemoveElements = false;

    public EdcPagingToolBar(int pageSize) {
        super(pageSize);
    }

    public void removeElements() {
        try {
            last.removeFromParent(); // Remove go to last page button
            afterText.removeFromParent(); // Remove total number of page text
            pageText.setAlignment(TextAlignment.CENTER);
            beforePage.setStyleName("edc-paging-text"); // Change font-family and size of text
            pageText.setStyleName("edc-paging-text"); // Change font-family and size of text
            pageText.setTitle(MSGS.pagingToolbarPage());
            triggeredRemoveElements = true;
        } catch (NullPointerException npe) {
            // Do nothing
        }

    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);
        if (!triggeredRemoveElements) {
            removeElements();
        }
    }

    @Override
    protected void onLoad(LoadEvent event) {
        super.onLoad(event);

        int temp = activePage == pages ? totalLength : start + pageSize;

        StringBuilder sb = new StringBuilder();

        sb.append(MSGS.pagingToolbarSummaryText(String.valueOf(start + 1),String.valueOf(temp)));

        String msg = sb.toString();
        if (totalLength == 0) {
            msg = msgs.getEmptyMsg();
        }
        displayText.setLabel(msg);
    }

    public void addUpdateButtonListener(SelectionListener<ButtonEvent> listener) {
        refresh.removeAllListeners();
        refresh.addSelectionListener(listener);
    }

}