package org.eclipse.kapua.app.console.client.overview;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class ForbiddenPanelView extends LayoutContainer {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    public ForbiddenPanelView() {
    }

    protected void onRender(final Element parent, int index) {
        super.onRender(parent, index);
        setBorders(false);
        setLayout(new FitLayout());

        ContentPanel forbiddenPanel = new ContentPanel();
        forbiddenPanel.setBorders(false);
        forbiddenPanel.setBodyBorder(false);
        forbiddenPanel.setHeaderVisible(false);
        forbiddenPanel.setLayout(new FitLayout());

        Text forbiddenText = new Text(MSGS.dashboardNotAllowedSection());
        forbiddenText.setStyleAttribute("margin", "5px");
        forbiddenText.setStyleAttribute("color", "grey");
        forbiddenText.setStyleAttribute("font-size", "12px");

        forbiddenPanel.add(forbiddenText);

        add(forbiddenPanel);
    }

}
