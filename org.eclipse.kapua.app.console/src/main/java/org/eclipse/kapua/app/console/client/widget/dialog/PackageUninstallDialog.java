package org.eclipse.kapua.app.console.client.widget.dialog;

import org.eclipse.kapua.app.console.client.resources.Resources;
import org.eclipse.kapua.app.console.shared.model.GwtDeploymentPackage;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public abstract class PackageUninstallDialog extends SimpleDialog 
{
    protected GwtDeploymentPackage m_selectedDeploymentPackage;

    public PackageUninstallDialog(GwtDeploymentPackage selectedDeploymentPackage)
    {
        super();

        m_selectedDeploymentPackage = selectedDeploymentPackage;
    }

    @Override
    public AbstractImagePrototype getHeaderIcon()
    {
        return AbstractImagePrototype.create(Resources.INSTANCE.help16());
    }

    @Override
    public String getHeaderMessage()
    {
        return MSGS.deviceUninstallPackage();
    }

    @Override
    public Image getInfoIcon()
    {
        return new Image(Resources.INSTANCE.help32());
    }

    @Override
    public String getInfoMessage()
    {
        return MSGS.deviceUninstallPackageInfo("com.eurotech.kura.client." + m_selectedDeploymentPackage.getName());
    }

    @Override
    public String getSubmitButtonText()
    {
        return MSGS.deviceUninstallButton();
    }
}
