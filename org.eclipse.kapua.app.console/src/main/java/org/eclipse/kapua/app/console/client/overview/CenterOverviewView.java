package org.eclipse.kapua.app.console.client.overview;

import org.eclipse.kapua.app.console.shared.model.GwtSession;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class CenterOverviewView extends LayoutContainer {

    private GwtSession m_currentSession;

    private LastMessagesView m_lastMessagesView;
    private LastDataReceivedChartView m_lastDataReceivedChartView;

    public CenterOverviewView(GwtSession currentSession) {
        m_currentSession = currentSession;
    }

    protected void onRender(final Element parent, int index) {
        super.onRender(parent, index);

        setLayout(new FitLayout());

        ContentPanel mainPanel = new ContentPanel();
        mainPanel.setBorders(false);
        mainPanel.setBodyBorder(false);
        mainPanel.setHeaderVisible(false);
        mainPanel.setLayout(new FitLayout());

        add(mainPanel);

        LayoutContainer layoutContainer = new LayoutContainer();
        layoutContainer.setLayout(new BorderLayout());

        mainPanel.add(layoutContainer);

        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, .50F);
        westData.setMargins(new Margins(0, 5, 0, 0));
        westData.setSplit(false);

        ContentPanel eastPanel = new ContentPanel();
        eastPanel.setBodyBorder(false);
        eastPanel.setBorders(false);
        eastPanel.setHeaderVisible(false);
        eastPanel.setLayout(new FitLayout());
        m_lastMessagesView = new LastMessagesView(m_currentSession);
        eastPanel.add(m_lastMessagesView);

        layoutContainer.add(eastPanel, westData);

        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0, 0, 0, 0));
        centerData.setSplit(false);

        ContentPanel centerPanel = new ContentPanel();
        centerPanel.setBodyBorder(false);
        centerPanel.setBorders(false);
        centerPanel.setHeaderVisible(false);
        centerPanel.setLayout(new FitLayout());
        m_lastDataReceivedChartView = new LastDataReceivedChartView(m_currentSession);
        centerPanel.add(m_lastDataReceivedChartView);

        layoutContainer.add(centerPanel, centerData);
    }

    public void refresh() {
        if (rendered) {
            m_lastMessagesView.refresh();
            m_lastDataReceivedChartView.refresh();
        }
    }
}
