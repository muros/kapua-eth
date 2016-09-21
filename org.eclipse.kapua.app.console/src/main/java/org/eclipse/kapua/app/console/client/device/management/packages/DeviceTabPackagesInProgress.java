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

    private boolean componentInitialized = false;
    private boolean contentDirty = true;

    private DeviceTabPackages parentTabPanel;
    private Grid<GwtPackageInstallOperation> grid;
    private ListStore<GwtPackageInstallOperation> gridStore = new ListStore<GwtPackageInstallOperation>();
    private BaseListLoader<ListLoadResult<GwtPackageInstallOperation>> storeLoader;

    public DeviceTabPackagesInProgress(DeviceTabPackages parentTabPanel) {
        this.parentTabPanel = parentTabPanel;
    }

    public GwtPackageInstallOperation getSelectedOperation() {
        return grid.getSelectionModel().getSelectedItem();
    }

    private GwtDevice getSelectedDevice() {
        return parentTabPanel.getSelectedDevice();
    }

    public void setDirty(boolean isDirty) {
        contentDirty = isDirty;
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

        storeLoader = new BaseListLoader<ListLoadResult<GwtPackageInstallOperation>>(proxy);

        ListStore<GwtPackageInstallOperation> store = new ListStore<GwtPackageInstallOperation>(storeLoader);

        grid = new Grid<GwtPackageInstallOperation>(store, columnModel);
        grid.setBorders(false);
        grid.setStateful(false);
        grid.setLoadMask(true);
        grid.setStripeRows(true);
        grid.setTrackMouseOver(false);
        grid.disableTextSelection(false);
        grid.setAutoExpandColumn("statusMessage");
        grid.getView().setAutoFill(true);
        grid.getView().setEmptyText(MSGS.deviceInstallTabInProgressTableEmpty());

        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<GwtPackageInstallOperation>() {

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
        rootContentPanel.add(grid);

        add(rootContentPanel);

        componentInitialized = true;
    }

    public void refresh() {
        if (contentDirty && componentInitialized) {

            GwtDevice selectedDevice = getSelectedDevice();
            if (selectedDevice == null) {
                gridStore.removeAll();
                grid.unmask();
                grid.getView().setEmptyText(MSGS.deviceNoDeviceSelectedOrOffline());
            } else {
                grid.mask(MSGS.loading());
                storeLoader.load();
            }

            contentDirty = false;
        }
    }
}