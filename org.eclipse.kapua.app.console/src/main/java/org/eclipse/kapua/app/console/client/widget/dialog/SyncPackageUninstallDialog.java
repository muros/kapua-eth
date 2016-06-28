package org.eclipse.kapua.app.console.client.widget.dialog;

import org.eclipse.kapua.app.console.client.util.DialogUtils;
import org.eclipse.kapua.app.console.shared.model.GwtDeploymentPackage;
import org.eclipse.kapua.app.console.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.shared.service.GwtDeviceService;

import com.eurotech.cloud.console.shared.service.GwtDeviceServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class SyncPackageUninstallDialog extends PackageUninstallDialog
{
    private final GwtDeviceServiceAsync gwtDeviceService = GWT.create(GwtDeviceService.class);

    private GwtDevice                   m_selectedDevice;

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
        gwtDeviceService.uninstallDeploymentPackage(m_token,
                                                    m_selectedDevice,
                                                    m_selectedDeploymentPackage.getUnescapedName(),
                                                    new AsyncCallback<Void>() {
                                                        @Override
                                                        public void onSuccess(Void arg0)
                                                        {
                                                            m_exitStatus = true;
                                                            m_exitMessage = MSGS.packageUninstallSyncSuccess();
                                                            hide();
                                                        }

                                                        @Override
                                                        public void onFailure(Throwable caught)
                                                        {
                                                            m_exitStatus = false;
                                                            m_exitMessage = MSGS.packageInstallSyncFailure(caught.getMessage());
                                                            hide();
                                                        }
                                                    });
    }

	@Override
	protected void addListeners() {
		// TODO Auto-generated method stub
		
	}
}
