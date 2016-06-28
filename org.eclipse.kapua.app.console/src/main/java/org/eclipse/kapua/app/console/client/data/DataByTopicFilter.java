package org.eclipse.kapua.app.console.client.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.app.console.client.resources.Resources;
import org.eclipse.kapua.app.console.client.util.EdcLoadListener;
import org.eclipse.kapua.app.console.client.util.FailureHandler;
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
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class DataByTopicFilter extends LayoutContainer {

    private static final ConsoleMessages          MSGS           = GWT.create(ConsoleMessages.class);

    private final GwtDataServiceAsync             gwtDataService = GWT.create(GwtDataService.class);

    private DataByTopicView                       m_dataView;
    private GwtSession                            m_currentSession;

    private ContentPanel                          m_filterInfoPanel;
    private Button                                m_queryButton;
    private TabPanel                              m_tabsPanel;
    private TabItem                               m_currentTab;

    private ContentPanel                          m_topicsPanel;
    private TreeGrid<GwtTopic>                    m_topicsGrid;
    private TreeStore<GwtTopic>                   m_topicStore;

    private ContentPanel                          m_headersPanel;
    private ListLoader<ListLoadResult<GwtHeader>> m_headersLoader;
    private EditorGrid<GwtHeader>                 m_headersGrid;

    public DataByTopicFilter(DataByTopicView dataView,
                             GwtSession currentSession) {
        m_dataView = dataView;
        m_currentSession = currentSession;
    }

    protected void onRender(final Element parent, int index) {
        super.onRender(parent, index);

        setLayout(new FitLayout());

        initInfo();
        initTopics();
        initHeaders();

        LayoutContainer mf = new LayoutContainer();
        mf.setLayout(new BorderLayout());

        // north panel: instructions
        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH);
        northData.setMargins(new Margins(10, 5, 0, 10));
        northData.setSize(52);
        northData.setSplit(false);
        mf.add(m_filterInfoPanel, northData);

        // center panel: topics
        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0, 5, 0, 10));
        centerData.setSplit(true);
        centerData.setMinSize(100);
        mf.add(m_topicsPanel, centerData);

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
            public void componentSelected(ButtonEvent ce) {
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

        Text info = new Text(MSGS.dataByTopicFilterInfo());
        info.setStyleAttribute("background-color", "#F0F0F0");
        m_filterInfoPanel.add(info);
    }

    public void initTopics() {
        m_topicsPanel = new ContentPanel();
        m_topicsPanel.setBorders(false);
        m_topicsPanel.setBodyBorder(true);
        m_topicsPanel.setHeaderVisible(false);
        m_topicsPanel.setScrollMode(Scroll.NONE);
        m_topicsPanel.setLayout(new FitLayout());
        m_topicsPanel.setStyleAttribute("background-color", "transparent");

        final Text topicsLabel = new Text(MSGS.dataFilterAvailableTopics());
        topicsLabel.setHeight(18);
        topicsLabel.setStyleAttribute("background-color", "#F0F0F0");
        topicsLabel.setStyleAttribute("font", "11px tahoma, arial, helvetica, sans-serif");

        HorizontalPanel hp = new HorizontalPanel();
        hp.setTableWidth("100%");
        hp.add(topicsLabel);

        TableData td = new TableData();
        td.setHorizontalAlign(HorizontalAlignment.RIGHT);
        Image infoIcon = new Image(Resources.INSTANCE.info16());
        infoIcon.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent arg0) {
                m_currentTab = m_tabsPanel.getSelectedItem();
                m_tabsPanel.setSelection(m_tabsPanel.getItem(2));
            }
        });
        infoIcon.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent arg0) {
                m_tabsPanel.setSelection(m_currentTab);
            }
        });
        hp.add(infoIcon, td);

        m_topicsPanel.setTopComponent(hp);
        initTopicsGrid();
        m_topicsPanel.add(m_topicsGrid);
    }

    private void initTopicsGrid() {

        //
        // Column Configuration
        ColumnConfig column = null;
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        // topic
        column = new ColumnConfig("topicNameLimited", MSGS.dataTopic(), 80);
        column.setAlignment(HorizontalAlignment.LEFT);
        column.setRenderer(new TreeGridCellRenderer<ModelData>());
        configs.add(column);

        // Timestamp
        column = new ColumnConfig("timestampFormatted", MSGS.dataLastPostDate(), 40);
        column.setAlignment(HorizontalAlignment.LEFT);
        configs.add(column);

        ColumnModel columnModel = new ColumnModel(configs);

        m_topicStore = new TreeStore<GwtTopic>();

        // Grid
        m_topicsGrid = new TreeGrid<GwtTopic>(m_topicStore, columnModel);
        m_topicsGrid.setBorders(false);
        m_topicsGrid.setStripeRows(true);
        m_topicsGrid.setTrackMouseOver(false);
        m_topicsGrid.getView().setAutoFill(true);
        m_topicsGrid.setAutoExpandColumn("topicName");
        m_topicsGrid.setLoadMask(true);
        m_topicsGrid.getView().setEmptyText(MSGS.dataNoTopics());

        GridSelectionModel<GwtTopic> sm = new GridSelectionModel<GwtTopic>();
        sm.setSelectionMode(SelectionMode.SINGLE);
        sm.addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<GwtTopic>>() {
            public void handleEvent(SelectionChangedEvent<GwtTopic> be) {
                Text text = (Text) m_headersPanel.getTopComponent();
                text.setText(MSGS.dataFilterAvailableHeadersForTopic() + " " + m_currentSession.getSelectedAccount().getName() + "/+/" + be.getSelectedItem().getSemanticTopic());
                text.setToolTip(m_currentSession.getSelectedAccount().getName() + "/+/" + be.getSelectedItem().getSemanticTopic());
                text.setStyleAttribute("white-space", "nowrap");
                text.setStyleAttribute("overflow", "hidden");

                m_headersLoader.load();
                refreshQueryButton();
            }
        });
        m_topicsGrid.setSelectionModel(sm);

        // do the load for the topics
        m_topicsGrid.mask(MSGS.loading());
        gwtDataService.findTopicsTree(m_currentSession.getSelectedAccount().getName(), new AsyncCallback<GwtTopic>() {
            public void onFailure(Throwable caught) {
                FailureHandler.handle(caught);
                m_topicsGrid.unmask();
            }

            public void onSuccess(GwtTopic gwtRootTopic) {
                m_topicStore.removeAll();
                m_topicStore.add((GwtTopic) gwtRootTopic, true);
                m_topicsGrid.unmask();
            }
        });
    }

    private void initHeaders() {
        m_headersPanel = new ContentPanel();
        m_headersPanel.setBorders(false);
        m_headersPanel.setBodyBorder(true);
        m_headersPanel.setHeaderVisible(false);
        m_headersPanel.setScrollMode(Scroll.NONE);
        m_headersPanel.setLayout(new FitLayout());
        m_headersPanel.setStyleAttribute("background-color", "transparent");
        m_headersPanel.setStyleAttribute("padding-left", "2px");

        Text headersLabel = new Text(MSGS.dataFilterAvailableHeadersForTopic());
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
                GwtTopic topic = m_topicsGrid.getSelectionModel().getSelectedItem();
                if (topic != null) {
                    gwtDataService.findHeaders((LoadConfig) loadConfig,
                                               m_currentSession.getSelectedAccount().getName(),
                                               topic,
                                               callback);
                }
            }
        };

        // tree loader
        m_headersLoader = new BaseListLoader<ListLoadResult<GwtHeader>>(proxy);
        m_headersLoader.addLoadListener(new EdcLoadListener());
        ListStore<GwtHeader> store = new ListStore<GwtHeader>(m_headersLoader);

        m_headersGrid = new EditorGrid<GwtHeader>(store, columnModel);
        m_headersGrid.setBorders(false);
        m_headersGrid.setStripeRows(true);
        m_headersGrid.getView().setAutoFill(true);
        m_headersGrid.setWidth(365);
        m_headersGrid.setHeight(155);
        m_headersGrid.setSelectionModel(sm);
        m_headersGrid.setAutoExpandColumn("name");
        m_headersGrid.setLoadMask(true);
        m_headersGrid.getView().setEmptyText(MSGS.dataNoHeaders());
        m_headersGrid.addPlugin(sm);

    }

    private void executeQuery() {

        GwtTopic topic = m_topicsGrid.getSelectionModel().getSelectedItem();
        if (topic != null) {

            List<GwtHeader> headers = m_headersGrid.getSelectionModel().getSelectedItems();
            if (headers.size() > 0) {

                m_queryButton.setEnabled(false);
                m_dataView.executeQuery(topic, headers);
            }
        }
    }

    private void refreshQueryButton() {

        if (m_topicsGrid.getSelectionModel().getSelectedItems().size() > 0 &&
                m_headersGrid.getSelectionModel().getSelectedItems().size() > 0) {
            m_queryButton.setEnabled(true);
        } else {
            m_queryButton.setEnabled(false);
        }
    }

    public void queryDone() {
        m_queryButton.setEnabled(true);
    }

    public void setTabsPanel(TabPanel tabPanel) {
        m_tabsPanel = tabPanel;
    }
}
