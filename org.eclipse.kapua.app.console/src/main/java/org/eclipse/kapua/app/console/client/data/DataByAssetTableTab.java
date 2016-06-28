package org.eclipse.kapua.app.console.client.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.eclipse.kapua.app.console.client.resources.Resources;
import org.eclipse.kapua.app.console.client.util.FailureHandler;
import org.eclipse.kapua.app.console.client.util.UserAgentUtils;
import org.eclipse.kapua.app.console.client.widget.DateRangeSelector;
import org.eclipse.kapua.app.console.client.widget.DateRangeSelectorListener;
import org.eclipse.kapua.app.console.client.widget.EdcPagingToolBar;
import org.eclipse.kapua.app.console.shared.analytics.GoogleAnalytics;
import org.eclipse.kapua.app.console.shared.model.EdcBasePagingCursor;
import org.eclipse.kapua.app.console.shared.model.EdcBasePagingLoadConfig;
import org.eclipse.kapua.app.console.shared.model.EdcBasePagingLoader;
import org.eclipse.kapua.app.console.shared.model.EdcPagingLoadConfig;
import org.eclipse.kapua.app.console.shared.model.EdcPagingLoadResult;
import org.eclipse.kapua.app.console.shared.model.GwtAsset;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtMessage;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.service.GwtDataService;

import com.allen_sauer.gwt.log.client.Log;
import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.eurotech.cloud.console.shared.service.GwtDataServiceAsync;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DataByAssetTableTab extends LayoutContainer {
    private static final ConsoleMessages                         MSGS              = GWT.create(ConsoleMessages.class);
    private final GwtDataServiceAsync                            gwtDataService    = GWT.create(GwtDataService.class);

    private static final int                                     MESSAGE_PAGE_SIZE = 250;

    private DataByAssetView                                      m_dataView;
    private GwtSession                                           m_currentSession;
    private boolean                                              m_dirty;

    private GwtAsset                                             m_asset;
    private List<GwtHeader>                                      m_headers;

    private ContentPanel                                         m_msgsTablePanel;
    private ToolBar                                              m_toolBar;
    private Button                                               m_export;
    private DateRangeSelector                                    m_dateRangeSelector;

    private Grid<GwtMessage>                                     m_grid;
    private PagingToolBar                                        m_pagingToolBar;

    private Stack<EdcBasePagingCursor>                           m_pagingCursors;
    private int                                                  m_lastOffset;
    private Date                                                 m_startDate;
    private Date                                                 m_endDate;
    private EdcBasePagingLoader<EdcPagingLoadResult<GwtMessage>> m_loader;

    public DataByAssetTableTab(DataByAssetView dataView, GwtSession currentSession) {
        m_dataView = dataView;
        m_currentSession = currentSession;

        m_dirty = false;
    }

    public GwtAsset getAsset() {
        return m_asset;
    }

    public void setAsset(GwtAsset asset) {
        m_asset = asset;
    }

    public List<GwtHeader> getHeaders() {
        return m_headers;
    }

    public void setHeaders(List<GwtHeader> headers) {
        m_headers = headers;
    }

    public boolean isDirty() {
        return m_dirty;
    }

    public void setDirty(boolean dirty) {
        m_dirty = dirty;
    }

    protected void onRender(Element parent, int index) {

        super.onRender(parent, index);
        setLayout(new FitLayout());
        setBorders(false);

        // Toolbar
        initToolbar();

        // empty table
        // column config
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        ColumnConfig date = new ColumnConfig("timestamp", "Timestamp", 150);
        date.setDateTimeFormat(DateTimeFormat.getFormat("MM/dd/yyyy HH:mm:ss.SSS"));
        columns.add(date);

        m_grid = new Grid<GwtMessage>(new ListStore<GwtMessage>(), new ColumnModel(columns));
        m_grid.setBorders(false);
        m_grid.getView().setEmptyText(MSGS.noResults());
        m_grid.setAutoExpandColumn("timestamp");

        m_msgsTablePanel = new ContentPanel();
        m_msgsTablePanel.setBorders(false);
        m_msgsTablePanel.setBodyBorder(false);
        m_msgsTablePanel.setHeaderVisible(false);
        m_msgsTablePanel.setScrollMode(Scroll.AUTO);
        m_msgsTablePanel.setLayout(new FitLayout());
        m_msgsTablePanel.add(m_grid);

        m_msgsTablePanel.setTopComponent(m_toolBar);

        m_pagingToolBar = new EdcPagingToolBar(MESSAGE_PAGE_SIZE);
        m_pagingToolBar.getItems().get(4).getElement().setAttribute("readonly", "readonly");
        m_pagingToolBar.disable();
        m_msgsTablePanel.setBottomComponent(m_pagingToolBar);

        ((EdcPagingToolBar) m_pagingToolBar).addUpdateButtonListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                m_dirty = true;
                refresh();
            }
        });

        add(m_msgsTablePanel);
    }

    public void initToolbar() {

        m_toolBar = new ToolBar();
        m_export = new SplitButton(MSGS.export());
        m_export.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.download()));
        Menu menu = new Menu();
        menu.add(new MenuItem(MSGS.exportToExcel(),
                              AbstractImagePrototype.create(Resources.INSTANCE.exportExcel()),
        new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                export("xls");
            }
        }));
        menu.add(new MenuItem(MSGS.exportToCSV(),
                              AbstractImagePrototype.create(Resources.INSTANCE.exportCSV()),
        new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                export("csv");
            }
        }));
        m_export.setMenu(menu);
        m_export.disable();

        m_dateRangeSelector = new DateRangeSelector();
        m_dateRangeSelector.setListener(new DateRangeSelectorListener() {
            public void onUpdate() {
                m_dirty = true;
                refresh();
            }
        });
        m_dateRangeSelector.disable();

        m_toolBar.add(m_export);
        m_toolBar.add(new SeparatorToolItem());
        m_toolBar.add(new FillToolItem());
        m_toolBar.add(new LabelToolItem(MSGS.dataDateRange()));
        m_toolBar.add(m_dateRangeSelector);
    }

    public void refresh() {
        if (!m_dirty || m_asset == null || m_headers == null) {
            return;
        }

        GoogleAnalytics.trackPageview(GoogleAnalytics.GA_DATA_BYASSET_TABLE);

        m_startDate = m_dateRangeSelector.getStartDate();
        m_endDate = m_dateRangeSelector.getEndDate();

        // Reset previous data
        m_pagingCursors = new Stack<EdcBasePagingCursor>();
        m_lastOffset = 0;

        // set the columns
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        ColumnConfig date = new ColumnConfig("m&t@_timestamp", MSGS.timestamp(), 150);
        date.setDateTimeFormat(DateTimeFormat.getFormat("MM/dd/y HH:mm:ss.SSS"));
        date.setSortable(false);
        columns.add(date);

        ColumnConfig asset = new ColumnConfig("m&t@_asset", MSGS.asset(), 100);
        asset.setSortable(false);
        columns.add(asset);

        ColumnConfig topic = new ColumnConfig("m&t@_topic", MSGS.topic(), 150);
        topic.setSortable(false);
        columns.add(topic);

        ColumnConfig cc = null;
        for (GwtHeader header : m_headers) {
            cc = new ColumnConfig(header.getName(), header.getName(), 60);
            cc.setSortable(false);
            columns.add(cc);
        }

        // loader and store
        RpcProxy<EdcPagingLoadResult<GwtMessage>> proxy = new RpcProxy<EdcPagingLoadResult<GwtMessage>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<EdcPagingLoadResult<GwtMessage>> callback) {
                Log.debug("About to set the limit: " + loadConfig.getClass().toString());
                ((EdcBasePagingLoadConfig) loadConfig).setLimit(MESSAGE_PAGE_SIZE);
                ((EdcBasePagingLoadConfig) loadConfig).setOffsetCursors(m_pagingCursors);
                ((EdcBasePagingLoadConfig) loadConfig).setLastOffset(m_lastOffset);
                gwtDataService.findMessagesByAsset((EdcPagingLoadConfig) loadConfig,
                                                   m_currentSession.getSelectedAccount().getName(),
                                                   m_asset,
                                                   m_headers,
                                                   m_startDate,
                                                   m_endDate,
                                                   callback);
            }
        };
        m_loader = new EdcBasePagingLoader<EdcPagingLoadResult<GwtMessage>>(proxy);
        m_loader.setSortDir(SortDir.DESC);
        m_loader.setSortField("timestamp");
        m_loader.setRemoteSort(true);
        m_loader.addLoadListener(new DataLoadListener());

        ListStore<GwtMessage> store = new ListStore<GwtMessage>(m_loader);
        m_msgsTablePanel.remove(m_grid);

        m_grid = new Grid<GwtMessage>(store, new ColumnModel(columns));
        m_grid.setStateId("gwtMessageByAssetGrid");
        m_grid.setBorders(false);
        m_grid.setStateful(false);
        m_grid.setLoadMask(true);
        m_grid.getView().setEmptyText(MSGS.noResults());
        m_grid.setAutoExpandColumn("m&t@_topic");
        m_grid.disableTextSelection(false);

        m_pagingToolBar.bind(m_loader);
        m_pagingToolBar.enable();

        m_msgsTablePanel.add(m_grid);
        m_msgsTablePanel.layout();

        m_loader.load();
        m_dirty = false;
    }

    private void export(String format) {
        StringBuilder sbUrl = new StringBuilder();

        if (UserAgentUtils.isSafari() || UserAgentUtils.isChrome()) {
            sbUrl.append("console/exporter?");
        } else {
            sbUrl.append("exporter?");
        }

        sbUrl.append("format=")
        .append(format)
        .append("&account=")
        .append(URL.encodeQueryString(m_currentSession.getSelectedAccount().getName()))
        .append("&asset=")
        .append(URL.encodeQueryString(m_asset.getAsset()))
        .append("&startDate=")
        .append(m_dateRangeSelector.getStartDate().getTime())
        .append("&endDate=")
        .append(m_dateRangeSelector.getEndDate().getTime())
        .append("&headers=");
        for (GwtHeader header : m_headers) {
            sbUrl.append(URL.encodeQueryString(header.getName())).append(",");
        }
        Window.open(sbUrl.toString(), "_blank", "location=no");
    }

    private class DataLoadListener extends LoadListener {

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void loaderLoad(LoadEvent le) {
            m_export.enable();
            m_dateRangeSelector.enable();

            m_pagingCursors = ((EdcPagingLoadResult) le.getData()).getCursorOffset();
            m_lastOffset = ((EdcPagingLoadResult) le.getData()).getLastOffset();

            m_dataView.queryDone();
        }

        public void loaderLoadException(LoadEvent le) {
            m_dataView.queryDone();
            if (le.exception != null) {
                FailureHandler.handle(le.exception);
            }
        }
    }
}
