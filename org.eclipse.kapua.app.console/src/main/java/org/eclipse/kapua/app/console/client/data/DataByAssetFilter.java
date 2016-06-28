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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.app.console.client.resources.Resources;
import org.eclipse.kapua.app.console.client.util.EdcLoadListener;
import org.eclipse.kapua.app.console.shared.model.GwtAsset;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.model.GwtTopic;
import org.eclipse.kapua.app.console.shared.service.GwtDataService;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.eurotech.cloud.console.shared.service.GwtDataServiceAsync;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DataByAssetFilter extends LayoutContainer {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    private final GwtDataServiceAsync gwtDataService = GWT.create(GwtDataService.class);

    private DataByAssetView m_dataView;
    private GwtSession      m_currentSession;

    private ContentPanel m_filterInfoPanel;
    private Button       m_queryButton;

    private ContentPanel    m_assetsPanel;
    private Grid<GwtAsset>  m_assetsGrid;

    private ContentPanel                m_headersPanel;
    private ListLoader<ListLoadResult<GwtHeader>> m_headersLoader;
    private EditorGrid<GwtHeader>       m_headersGrid;


    public DataByAssetFilter(DataByAssetView dataView,
                             GwtSession currentSession) {
        m_dataView = dataView;
        m_currentSession = currentSession;
    }

    protected void onRender(final Element parent, int index) {
        super.onRender(parent, index);

        setLayout(new FitLayout());
        setBorders(false);

        initInfo();
        initAssets();
        initHeaders();

        LayoutContainer mf = new LayoutContainer();
        mf.setLayout(new BorderLayout());

		// north panel: instructions
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH);
		northData.setMargins(new Margins(10, 5, 0, 10));
		northData.setSize(52);
		northData.setSplit(false);
		mf.add(m_filterInfoPanel, northData);

		// center panel: assets
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0, 5, 0, 10));
		centerData.setSplit(true);
		centerData.setMinSize(100);
		mf.add(m_assetsPanel, centerData);

		// east panel: header
		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, .47F);
		eastData.setMargins(new Margins(0, 10, 0, 0));
		eastData.setSplit(true);
		eastData.setMinSize(100);
		mf.add(m_headersPanel, eastData);

        // south button
		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH);
		southData.setMargins(new Margins(5, 10, 0, 10));
		southData.setSplit(false);
		southData.setSize(40);
		
		m_queryButton = new Button(MSGS.queryButton(), AbstractImagePrototype.create(Resources.INSTANCE.magnifier16()));
		m_queryButton.setScale(ButtonScale.MEDIUM);
		m_queryButton.addSelectionListener( new SelectionListener<ButtonEvent>() {
			@Override
            public void componentSelected(ButtonEvent ce)
            {
				executeQuery();
			}
		});
		m_queryButton.setEnabled(false);
		
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setAlignment(HorizontalAlignment.LEFT);
		buttonBar.add(m_queryButton);
		
		mf.add(buttonBar, southData);
		
		add(mf);
    }
    
    private void initInfo() {
        m_filterInfoPanel = new ContentPanel();
        m_filterInfoPanel.setBorders(false);
        m_filterInfoPanel.setBodyBorder(false);
        m_filterInfoPanel.setHeaderVisible(false);
        m_filterInfoPanel.setScrollMode(Scroll.NONE);
        m_filterInfoPanel.setLayout(new FitLayout());
		m_filterInfoPanel.setStyleAttribute("background-color", "transparent");
		
		Text info = new Text(MSGS.dataByAssetFilterInfo());
		info.setStyleAttribute("background-color", "#F0F0F0");
		m_filterInfoPanel.add(info);
	}
	
    public void initAssets()
    {
		m_assetsPanel = new ContentPanel();
		m_assetsPanel.setBorders(false);
		m_assetsPanel.setBodyBorder(true);
		m_assetsPanel.setHeaderVisible(false);
		m_assetsPanel.setScrollMode(Scroll.NONE);
		m_assetsPanel.setLayout(new FitLayout());
		m_assetsPanel.setStyleAttribute("background-color", "transparent");
		
		Text assetsLabel = new Text(MSGS.dataFilterAvailableAssets());
        assetsLabel.setHeight(18);
		assetsLabel.setStyleAttribute("background-color", "#F0F0F0");
		assetsLabel.setStyleAttribute("font", "11px tahoma, arial, helvetica, sans-serif");
		m_assetsPanel.setTopComponent(assetsLabel);

        initAssetsGrid();
        m_assetsPanel.add(m_assetsGrid);
    }

    private void initAssetsGrid() {

        //
        // Column Configuration
        ColumnConfig column = null;
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        // asset
        column = new ColumnConfig("friendlyAsset", MSGS.dataAsset(), 80);
        column.setAlignment(HorizontalAlignment.LEFT);
        configs.add(column);

        // timestamp
        column = new ColumnConfig("timestampFormatted", MSGS.dataLastPostDate(), 40);
        column.setAlignment(HorizontalAlignment.LEFT);
        configs.add(column);

        ColumnModel columnModel = new ColumnModel(configs);

        // rpc data proxy
        RpcProxy<ListLoadResult<GwtAsset>> proxy = new RpcProxy<ListLoadResult<GwtAsset>>() {
            @Override
            protected void load(Object loadConfig, AsyncCallback<ListLoadResult<GwtAsset>> callback) {
                gwtDataService.findAssets((LoadConfig) loadConfig,
                                          m_currentSession.getSelectedAccount().getName(),
                                          callback);
            }
        };
        ListLoader<ListLoadResult<GwtAsset>> assetsLoader = new BaseListLoader<ListLoadResult<GwtAsset>>(proxy);
        assetsLoader.addLoadListener( new EdcLoadListener());
        ListStore<GwtAsset> store = new ListStore<GwtAsset>(assetsLoader);

        // Grid
        m_assetsGrid = new Grid<GwtAsset>(store, columnModel);
        m_assetsGrid.setBorders(false);
        m_assetsGrid.setStripeRows(true);
        m_assetsGrid.setTrackMouseOver(false);
        m_assetsGrid.getView().setAutoFill(true);
        m_assetsGrid.setAutoExpandColumn("friendlyAsset");
        m_assetsGrid.setLoadMask(true);
        m_assetsGrid.getView().setEmptyText(MSGS.dataNoAsset());

        GridSelectionModel<GwtAsset> sm = new GridSelectionModel<GwtAsset>();
        sm.setSelectionMode(SelectionMode.SINGLE);
        sm.addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<GwtTopic>>() {
            public void handleEvent(SelectionChangedEvent<GwtTopic> be) {
                m_headersLoader.load();
                refreshQueryButton();
            }
        });
        m_assetsGrid.setSelectionModel(sm);

        // do the load for the topics
        m_assetsGrid.mask(MSGS.loading());
        assetsLoader.load();
    }

    private void initHeaders()
    {
		m_headersPanel = new ContentPanel();
		m_headersPanel.setBorders(false);
		m_headersPanel.setBodyBorder(true);
		m_headersPanel.setHeaderVisible(false);
		m_headersPanel.setScrollMode(Scroll.NONE);
		m_headersPanel.setLayout(new FitLayout());
		m_headersPanel.setStyleAttribute("background-color", "transparent");
		m_headersPanel.setStyleAttribute("padding-left", "2px");
		
		Text headersLabel = new Text(MSGS.dataFilterAvailableHeadersForAsset());
        headersLabel.setHeight(20);
        headersLabel.setStyleAttribute("background-color", "#F0F0F0");
        m_headersPanel.setTopComponent(headersLabel);

        initHeadersGrid();
        m_headersPanel.add(m_headersGrid);
    }

    private void initHeadersGrid() {

        // headers table columns
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        final CheckBoxSelectionModel<GwtHeader> sm = new CheckBoxSelectionModel<GwtHeader>();
        configs.add(sm.getColumn());
        sm.addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<GwtTopic>>() {
            public void handleEvent(SelectionChangedEvent<GwtTopic> be) {
                refreshQueryButton();
            }
        });

        ColumnConfig column = null;
        column = new ColumnConfig("name", MSGS.dataHeader(), 270);
        column.setAlignment(HorizontalAlignment.LEFT);
        configs.add(column);

        column = new ColumnConfig("typeFormatted", MSGS.dataHeaderType(), 150);
        column.setAlignment(HorizontalAlignment.LEFT);
        configs.add(column);

        // headers table
        ColumnModel columnModel = new ColumnModel(configs);

        // rpc data proxy
        RpcProxy<ListLoadResult<GwtHeader>> proxy = new RpcProxy<ListLoadResult<GwtHeader>>() {
            @Override
            protected void load(Object loadConfig, AsyncCallback<ListLoadResult<GwtHeader>> callback) {
                GwtAsset asset = m_assetsGrid.getSelectionModel().getSelectedItem();
                if (asset != null) {
                    Text text = (Text) m_headersPanel.getTopComponent();
                    text.setText(MSGS.dataFilterAvailableHeadersForAsset() + " " + asset.getAsset());
                    gwtDataService.findHeaders((LoadConfig) loadConfig,
                                               m_currentSession.getSelectedAccount().getName(),
                                               asset,
                                               callback);
                }
            }
        };

        // tree loader
        m_headersLoader = new BaseListLoader<ListLoadResult<GwtHeader>>(proxy);
        m_headersLoader.addLoadListener( new EdcLoadListener());
        ListStore<GwtHeader> store = new ListStore<GwtHeader>(m_headersLoader);

        m_headersGrid = new EditorGrid<GwtHeader>(store, columnModel);
        m_headersGrid.setBorders(false);
        m_headersGrid.setStripeRows(true);
        m_headersGrid.getView().setAutoFill(true);
        m_headersGrid.setWidth(365);
        m_headersGrid.setHeight(155);
        m_headersGrid.setSelectionModel(sm);
        m_headersGrid.setAutoExpandColumn("header");
        m_headersGrid.setLoadMask(true);
        m_headersGrid.getView().setEmptyText(MSGS.dataNoHeaders());
        m_headersGrid.addPlugin(sm);
    }

    private void executeQuery() {

        GwtAsset asset = m_assetsGrid.getSelectionModel().getSelectedItem();
        if (asset != null) {

            List<GwtHeader> headers = m_headersGrid.getSelectionModel().getSelectedItems();
            if (headers.size() > 0) {

                m_queryButton.setEnabled(false);
                m_dataView.executeQuery(asset, headers);
            }
        }
    }

    private void refreshQueryButton() {

        if (m_assetsGrid.getSelectionModel().getSelectedItems().size() > 0 &&
                m_headersGrid.getSelectionModel().getSelectedItems().size() > 0) {
            m_queryButton.setEnabled(true);
        } else {
            m_queryButton.setEnabled(false);
        }
    }

    public void queryDone() {
        m_queryButton.setEnabled(true);
    }
}
