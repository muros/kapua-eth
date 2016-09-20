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
package org.eclipse.kapua.app.console.client.device;

import org.eclipse.kapua.app.console.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.client.resources.Resources;
import org.eclipse.kapua.app.console.client.widget.dialog.InfoDialog;
import org.eclipse.kapua.app.console.client.widget.dialog.InfoDialog.InfoDialogType;
import org.eclipse.kapua.app.console.client.widget.dialog.SimpleDialog;
import org.eclipse.kapua.app.console.client.widget.dialog.SyncPackageInstallDialog;
import org.eclipse.kapua.app.console.client.widget.dialog.SyncPackageUninstallDialog;
import org.eclipse.kapua.app.console.client.widget.dialog.TabbedDialog;
import org.eclipse.kapua.app.console.shared.model.GwtDeploymentPackage;
import org.eclipse.kapua.app.console.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.shared.model.GwtDevice.GwtDeviceApplication;
import org.eclipse.kapua.app.console.shared.model.GwtSession;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DeviceTabPackages extends LayoutContainer {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    private final static String SERVLET_URL = "console/file/deploy";

    @SuppressWarnings("unused")
    private GwtSession m_currentSession;
    private DeviceTabs m_deviceTabs;

    private boolean m_initialized = false;
    private GwtDevice m_selectedDevice;

    private ToolBar m_toolBar;
    private Button m_refreshButton;
    private Button m_installButton;
    private Button m_uninstallButton;

    private TabPanel m_tabsPanel;
    private DeviceTabPackagesTabInstalled m_installedPackageTab;

    public DeviceTabPackages(GwtSession currentSession,
            DeviceTabs deviceTabs) {
        m_currentSession = currentSession;
        m_deviceTabs = deviceTabs;
    }

    public void setDevice(GwtDevice selectedDevice) {
        setDirty();
        m_selectedDevice = selectedDevice;

        if (m_initialized) {
            m_tabsPanel.setSelection(m_installedPackageTab);
        }
    }

    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setLayout(new FitLayout());
        setBorders(false);

        //
        // Init actions toolbar
        initToolBar();

        //
        // Init Packages tab
        initPackagesTabs();

        ContentPanel devicesConfigurationPanel = new ContentPanel();
        devicesConfigurationPanel.setBorders(false);
        devicesConfigurationPanel.setBodyBorder(false);
        devicesConfigurationPanel.setHeaderVisible(false);
        devicesConfigurationPanel.setLayout(new FitLayout());
        devicesConfigurationPanel.setScrollMode(Scroll.AUTO);
        devicesConfigurationPanel.setTopComponent(m_toolBar);
        devicesConfigurationPanel.add(m_tabsPanel);

        add(devicesConfigurationPanel);

        m_initialized = true;
    }

    //
    // INITIALIZERS
    //

    private void initToolBar() {
        m_toolBar = new ToolBar();

        m_refreshButton = new Button(MSGS.refreshButton(),
                AbstractImagePrototype.create(Resources.INSTANCE.refresh()),
                new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        if (m_selectedDevice.isOnline()) {

                            setDirty();
                            refresh();
                        } else {
                            openDeviceOfflineAlertDialog();
                        }
                    }
                });

        m_installButton = new Button(MSGS.packageAddButton(),
                AbstractImagePrototype.create(Resources.INSTANCE.packageAdd()),
                new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        if (m_selectedDevice.isOnline()) {
                            openInstallDialog();
                        } else {
                            openDeviceOfflineAlertDialog();
                        }
                    }
                });

        m_uninstallButton = new Button(MSGS.packageDeleteButton(),
                AbstractImagePrototype.create(Resources.INSTANCE.packageDelete()),
                new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        if (m_selectedDevice.isOnline()) {
                            openUninstallDialog();
                        } else {
                            openDeviceOfflineAlertDialog();
                        }
                    }
                });

        m_toolBar.add(m_refreshButton);
        m_toolBar.add(new SeparatorToolItem());
        m_toolBar.add(m_installButton);
        m_toolBar.add(new SeparatorToolItem());
        m_toolBar.add(m_uninstallButton);

        m_toolBar.disable();
    }

    private void initPackagesTabs() {
        m_tabsPanel = new TabPanel();
        m_tabsPanel.setPlain(true);
        m_tabsPanel.setBorders(false);
        m_tabsPanel.setTabPosition(TabPosition.BOTTOM);

        //
        // Installed Package Tab
        m_installedPackageTab = new DeviceTabPackagesTabInstalled(this);
        m_installedPackageTab.setText(MSGS.deviceInstallTabInstalled());
        m_installedPackageTab.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.packageGreen16()));
        m_installedPackageTab.setBorders(false);
        m_installedPackageTab.setLayout(new FitLayout());

        m_installedPackageTab.addListener(Events.Select, new Listener<ComponentEvent>() {

            public void handleEvent(ComponentEvent be) {
                refresh();
            }
        });
        m_tabsPanel.add(m_installedPackageTab);

        add(m_tabsPanel);
    }

    //
    // ACTIONS DIALOGS
    //
    private void openInstallDialog() {
        m_toolBar.disable();

        final TabbedDialog packageInstallDialog;
        if (m_selectedDevice.hasApplication(GwtDeviceApplication.APP_DEPLOY_V1)) {
            packageInstallDialog = new SyncPackageInstallDialog(SERVLET_URL,
                    m_selectedDevice.getScopeId(),
                    m_selectedDevice.getUnescapedClientId());

        } else {
            packageInstallDialog = null;
        }

        if (packageInstallDialog != null) {
            packageInstallDialog.addListener(Events.Hide, new Listener<BaseEvent>() {

                @Override
                public void handleEvent(BaseEvent be) {
                    m_toolBar.enable();

                    Boolean exitStatus = packageInstallDialog.getExitStatus();
                    if (exitStatus == null) { // Operation Aborted
                        return;
                    } else {

                        InfoDialogType exitDialogType;
                        String exitMessage = packageInstallDialog.getExitMessage();

                        if (exitStatus == true) { // Operation Success
                            exitDialogType = InfoDialogType.INFO;
                        } else { // Operaton Failed
                            exitDialogType = InfoDialogType.ERROR;
                        }

                        //
                        // Exit dialog
                        InfoDialog exitDialog = new InfoDialog(exitDialogType,
                                exitMessage);

                        exitDialog.show();

                        m_uninstallButton.disable();
                        m_deviceTabs.setDevice(m_selectedDevice);
                    }
                }
            });

            packageInstallDialog.show();
        }
    }

    private void openUninstallDialog() {
        final GwtDeploymentPackage selectedDeploymentPackage = m_installedPackageTab.getSelectedDeploymentPackage();

        if (selectedDeploymentPackage != null) {
            m_toolBar.disable();

            final SimpleDialog packageUninstallDialog;
            if (m_selectedDevice.hasApplication(GwtDeviceApplication.APP_DEPLOY_V1)) {
                packageUninstallDialog = new SyncPackageUninstallDialog(m_selectedDevice,
                        selectedDeploymentPackage);

            } else {
                packageUninstallDialog = null;
            }

            if (packageUninstallDialog != null) {
                packageUninstallDialog.addListener(Events.Hide, new Listener<BaseEvent>() {

                    @Override
                    public void handleEvent(BaseEvent be) {
                        m_toolBar.enable();

                        Boolean exitStatus = packageUninstallDialog.getExitStatus();
                        if (exitStatus == null) { // Operation Aborted
                            return;
                        } else {

                            InfoDialogType exitDialogType;
                            String exitMessage = packageUninstallDialog.getExitMessage();

                            if (exitStatus == true) { // Operation Success
                                exitDialogType = InfoDialogType.INFO;
                            } else { // Operaton Failed
                                exitDialogType = InfoDialogType.ERROR;
                            }

                            //
                            // Exit dialog
                            InfoDialog exitDialog = new InfoDialog(exitDialogType,
                                    exitMessage);

                            exitDialog.show();

                            m_uninstallButton.disable();
                            m_deviceTabs.setDevice(m_selectedDevice);
                        }
                    }
                });

                packageUninstallDialog.show();
            }
        }
    }

    public void openDeviceOfflineAlertDialog() {
        InfoDialog errorDialog = new InfoDialog(InfoDialogType.INFO,
                MSGS.deviceOffline());
        errorDialog.show();
    }

    //
    // REFRESHER
    //
    public void refresh() {
        //
        // Refresh the installed tab if selected
        m_installedPackageTab.refresh();

        //
        // Manage buttons
        if (m_selectedDevice != null && m_selectedDevice.isOnline()) {
            m_toolBar.enable();
            m_uninstallButton.disable();
        } else {
            m_toolBar.disable();
        }

    }

    //
    // ACCESSORS
    //

    public GwtDevice getSelectedDevice() {
        return m_selectedDevice;
    }

    public void setDirty() {
        if (m_initialized) {
            m_installedPackageTab.setDirty(true);
        }
    }

    public Button getUninstallButton() {
        return m_uninstallButton;
    }

}
