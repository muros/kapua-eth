package org.eclipse.kapua.app.console.client.device.management.packages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.app.console.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.shared.model.device.management.packages.GwtPackageInstallOperation;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DeviceTabPackagesInProgress extends TabItem {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);
    private static final int DEVICE_PROCESS_PAGE_SIZE = 50;

    private boolean m_initialized = false;
    private boolean m_dirty = true;

    private DeviceTabPackages m_rootTabPanel;
    private Grid<GwtPackageInstallOperation> m_grid;
    private ListStore<GwtPackageInstallOperation> m_store = new ListStore<GwtPackageInstallOperation>();
    private BaseListLoader<ListLoadResult<GwtPackageInstallOperation>> m_deviceJobTargetLoader;

    public DeviceTabPackagesInProgress(DeviceTabPackages rootTabPanel) {
        m_rootTabPanel = rootTabPanel;
    }

    public GwtPackageInstallOperation getSelectedOperation() {
        return m_grid.getSelectionModel().getSelectedItem();
    }

    private GwtDevice getSelectedDevice() {
        return m_rootTabPanel.getSelectedDevice();
    }

    public void setDirty(boolean isDirty) {
        m_dirty = isDirty;
    }

    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        //
        // Column Configuration
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        GridCellRenderer<GwtPackageInstallOperation> deviceJobTargetStatusIconRender = new GridCellRenderer<GwtPackageInstallOperation>() {

            @Override
            public Object render(GwtPackageInstallOperation model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<GwtPackageInstallOperation> store,
                    Grid<GwtPackageInstallOperation> grid) {
                // TODO Auto-generated method stub
                return null;
            }

        };

        ColumnConfig column = new ColumnConfig();
        column.setId("deviceJobTargetStatus");
        column.setWidth(20);
        column.setAlignment(HorizontalAlignment.CENTER);
        column.setRenderer(deviceJobTargetStatusIconRender);
        configs.add(column);

        GridCellRenderer<GwtPackageInstallOperation> deviceJobTargetOperationRender = new GridCellRenderer<GwtPackageInstallOperation>() {

            @Override
            public Object render(GwtPackageInstallOperation model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<GwtPackageInstallOperation> store,
                    Grid<GwtPackageInstallOperation> grid) {
                // TODO Auto-generated method stub
                return null;
            }
        };

        column = new ColumnConfig();
        column.setId("operation");
        column.setHeader(MSGS.deviceInstallTabInProgressTableOperation());
        column.setAlignment(HorizontalAlignment.CENTER);
        column.setWidth(80);
        column.setRenderer(deviceJobTargetOperationRender);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("name");
        column.setHeader(MSGS.deviceInstallTabInProgressTableDpName());
        column.setAlignment(HorizontalAlignment.CENTER);
        column.setWidth(120);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("lastUpdateOnFormatted");
        column.setHeader(MSGS.deviceInstallTabInProgressTableLastUpdateOn());
        column.setAlignment(HorizontalAlignment.CENTER);
        column.setWidth(120);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("statusMessage");
        column.setHeader(MSGS.deviceInstallTabInProgressTableStatusMessage());
        column.setWidth(200);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("progressPercentage");
        column.setHeader(MSGS.deviceInstallTabInProgressTableProgressPercentage());
        column.setWidth(80);
        column.setAlignment(HorizontalAlignment.CENTER);
        configs.add(column);

        ColumnModel columnModel = new ColumnModel(configs);

        RpcProxy<ListLoadResult<GwtPackageInstallOperation>> proxy = new RpcProxy<ListLoadResult<GwtPackageInstallOperation>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<ListLoadResult<GwtPackageInstallOperation>> callback) {
                // TODO Auto-generated method stub

            }

        };

        m_deviceJobTargetLoader = new BaseListLoader<ListLoadResult<GwtPackageInstallOperation>>(proxy);

        ListStore<GwtPackageInstallOperation> store = new ListStore<GwtPackageInstallOperation>(m_deviceJobTargetLoader);

        m_grid = new Grid<GwtPackageInstallOperation>(store, columnModel);
        m_grid.setBorders(false);
        m_grid.setStateful(false);
        m_grid.setLoadMask(true);
        m_grid.setStripeRows(true);
        m_grid.setTrackMouseOver(false);
        m_grid.disableTextSelection(false);
        m_grid.setAutoExpandColumn("statusMessage");
        m_grid.getView().setAutoFill(true);
        m_grid.getView().setEmptyText(MSGS.deviceInstallTabInProgressTableEmpty());

        m_grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<GwtPackageInstallOperation>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<GwtPackageInstallOperation> se) {
                ModelData selectedItem = se.getSelectedItem();

            }
        });

        ContentPanel rootContentPanel = new ContentPanel();
        rootContentPanel.setLayout(new FitLayout());
        rootContentPanel.setBorders(false);
        rootContentPanel.setBodyBorder(false);
        rootContentPanel.setHeaderVisible(false);
        rootContentPanel.add(m_grid);

        add(rootContentPanel);

        m_initialized = true;
    }

    public void refresh() {
        if (m_dirty && m_initialized) {

            GwtDevice selectedDevice = getSelectedDevice();
            if (selectedDevice == null) {
                m_store.removeAll();
                m_grid.unmask();
                m_grid.getView().setEmptyText(MSGS.deviceNoDeviceSelectedOrOffline());
            } else {
                m_grid.mask(MSGS.loading());
                m_deviceJobTargetLoader.load();
            }

            m_dirty = false;
        }
    }
}