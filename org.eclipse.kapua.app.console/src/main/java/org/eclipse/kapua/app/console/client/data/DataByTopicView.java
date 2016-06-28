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
package org.eclipse.kapua.app.console.client.data;

import java.util.List;

import org.eclipse.kapua.app.console.client.resources.Resources;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.model.GwtTopic;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DataByTopicView extends LayoutContainer {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    private GwtSession          m_currentSession;
    private DataByTopicFilter   m_dataFilter;
    private DataByTopicTableTab m_dataTable;
    private DataByTopicChartTab m_dataChart;
    private TabPanel       m_tabsPanel;
    private TabItem             m_tableTab;
    private TabItem       m_chartTab;
    private TabItem    m_infoTab;

    public DataByTopicView(GwtSession currentSession) {
        m_currentSession = currentSession;
    }

    protected void onRender(final Element parent, int index) {
        super.onRender(parent, index);
        setLayout(new FitLayout());
        setBorders(false);

        LayoutContainer mf = new LayoutContainer();
        mf.setLayout(new BorderLayout());

        m_dataFilter = new DataByTopicFilter  (this, m_currentSession);
        m_dataTable  = new DataByTopicTableTab(this, m_currentSession);
        m_dataChart  = new DataByTopicChartTab(this, m_currentSession);

        // north panel data filter
        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, .35F);
        northData.setMargins(new Margins(0, 0, 0, 0));
        northData.setSplit(true);
        northData.setMinSize(200);
        mf.add(m_dataFilter, northData);

        // center panel data results
        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER, .45F);
        centerData.setMargins(new Margins(0, 0, 0, 0));
        centerData.setSplit(true);
        centerData.setMinSize(0);

        m_tabsPanel = new TabPanel();
        m_tabsPanel.setPlain(true);
        m_tabsPanel.setBorders(false);
        m_tabsPanel.setBodyBorder(false);
        m_tabsPanel.setStyleAttribute("padding-top", "5px");

        m_tableTab = new TabItem(MSGS.dataTable());
        m_tableTab.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.table()));
        m_tableTab.setBorders(false);
        m_tableTab.setLayout(new FitLayout());
        m_tableTab.add(m_dataTable);
        m_tableTab.addListener(Events.Select, new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent be) {
                m_dataTable.refresh();
                m_dataChart.cleanSubscription();
            }
        });
        m_tabsPanel.add(m_tableTab);

        m_chartTab = new TabItem(MSGS.dataChart());
        m_chartTab.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.chart()));
        m_chartTab.setBorders(false);
        m_chartTab.setLayout(new FitLayout());
        m_chartTab.add(m_dataChart);
        m_chartTab.addListener(Events.Select, new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent be) {
                m_dataChart.refreshQuery();
            }
        });
        m_tabsPanel.add(m_chartTab);

        m_infoTab = new TabItem(MSGS.information());
        m_infoTab.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.info16()));
        m_infoTab.setBorders(false);
        m_infoTab.setLayout(new FitLayout());
        HtmlContainer htmlContainer = new HtmlContainer();
        htmlContainer.setHtml(getToolTipText());
        htmlContainer.setStyleAttribute("padding", "10px");
        htmlContainer.setStyleAttribute("font-size", "13px");
        m_infoTab.add(htmlContainer);
        m_tabsPanel.add(m_infoTab);
        m_dataFilter.setTabsPanel(m_tabsPanel);

        mf.add(m_tabsPanel, centerData);

        add(mf);
    }


    public void executeQuery(GwtTopic topic, List<GwtHeader> headers) {

        m_dataTable.setTopic(topic);
        m_dataTable.setHeaders(headers);
        m_dataTable.setDirty(true);

        m_dataChart.setTopic(topic);
        m_dataChart.setHeaders(headers);
        m_dataChart.setDirty(true);

        if (m_tabsPanel.getSelectedItem() == m_tableTab) {
            m_dataTable.refresh();
        } else {
            m_dataChart.refreshQuery();
        }
    }


    public void queryDone() {
        m_dataFilter.queryDone();
    }

    private String getToolTipText() {
        return  "A topic ending in a slash is considered equivalent to the same topic without the slash, which enables the <br />" +
                "wildcards to behave like this: <br /><br />" +
                "<ul style=\"list-style: disc;\">" +
                "<li style=\"margin-left: 3.4em;\">A subscription to A/# is a subscription to the topic A and all topics beneath A</li>" +
                "<li style=\"margin-left: 3.4em;\">A subscription to A/+/# is a subscription to all topics beneath A, but not A itself</li>" +
                "</ul><br /><br />"+
                "More information can be found at <a href=\"http://mqtt.org/wiki/doku.php/topic_format\">http://mqtt.org/wiki/doku.php/topic_format</a>";
    }
}
