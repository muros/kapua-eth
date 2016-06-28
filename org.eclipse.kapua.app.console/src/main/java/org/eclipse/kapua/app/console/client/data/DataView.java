package org.eclipse.kapua.app.console.client.data;

import org.eclipse.kapua.app.console.client.resources.Resources;
import org.eclipse.kapua.app.console.shared.model.GwtSession;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DataView extends LayoutContainer
{

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    private GwtSession                   m_currentSession;
    private TabPanel                     m_tabsPanel;

    public DataView(GwtSession currentSession)
    {
        m_currentSession = currentSession;
    }

    protected void onRender(final Element parent, int index)
    {
        super.onRender(parent, index);

        setLayout(new FitLayout());
        setBorders(false);

        //
        // Create a new panel for Tab Layout
        m_tabsPanel = new TabPanel();
        m_tabsPanel.setMinTabWidth(100);
        m_tabsPanel.setCloseContextMenu(true);
        m_tabsPanel.setPlain(false);
        m_tabsPanel.setBorders(false);

        //
        // Add data by asset tab
        TabItem topicTab = new TabItem();
        topicTab.setText(MSGS.dataByTopic());
        topicTab.setClosable(false);
        topicTab.setLayout(new FitLayout());
        topicTab.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.dataByTopic()));
        topicTab.add(new DataByTopicView(m_currentSession));
        m_tabsPanel.add(topicTab);

        if (m_currentSession.hasDeviceReadPermission()) {
            //
            // Add data by asset tab
            TabItem assetTab = new TabItem();
            assetTab.setText(MSGS.dataByAsset());
            assetTab.setClosable(false);
            assetTab.setLayout(new FitLayout());
            assetTab.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.dataByAsset()));
            assetTab.add(new DataByAssetView(m_currentSession));
            m_tabsPanel.add(assetTab);
        }

        add(m_tabsPanel);
    }
}
