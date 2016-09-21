/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.client.widget.dialog;

import org.eclipse.kapua.app.console.client.util.DialogUtils;
import org.eclipse.kapua.app.console.shared.model.GwtDeploymentPackage;
import org.eclipse.kapua.app.console.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.shared.service.GwtDeviceManagementService;
import org.eclipse.kapua.app.console.shared.service.GwtDeviceManagementServiceAsync;

import com.google.gwt.core.client.GWT;

public class SyncPackageUninstallDialog extends PackageUninstallDialog
{
    private final GwtDeviceManagementServiceAsync gwtDeviceManagementService = GWT.create(GwtDeviceManagementService.class);

    private GwtDevice m_selectedDevice;

    public SyncPackageUninstallDialog(GwtDevice selectedDevice,
                                      GwtDeploymentPackage selectedDeploymentPackage)
    {
        super(selectedDeploymentPackage);

        m_selectedDevice = selectedDevice;

        DialogUtils.resizeDialog(this, 400, 145);
    }

    @Override
    public void createBody()
    {
        // Nothing to do here...
    }

    @Override
    public void submit()
    {
        // gwtDeviceManagementService.uninstallPackage(token,
        // m_selectedDevice,
        // m_selectedDeploymentPackage.getUnescapedName(),
        // new AsyncCallback<Void>() {
        // @Override
        // public void onSuccess(Void arg0)
        // {
        // m_exitStatus = true;
        // m_exitMessage = MSGS.packageUninstallSyncSuccess();
        // hide();
        // }
        //
        // @Override
        // public void onFailure(Throwable caught)
        // {
        // m_exitStatus = false;
        // m_exitMessage = MSGS.packageInstallSyncFailure(caught.getMessage());
        // hide();
        // }
        // });
    }

    @Override
    protected void addListeners()
    {
        // TODO Auto-generated method stub

    }
}
