package org.eclipse.kapua.app.console.client.widget.dialog;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public abstract class PackageInstallDialog extends TabbedDialog
{
    public PackageInstallDialog()
    {
        super();
    }

    @Override
    public AbstractImagePrototype getHeaderIcon()
    {
        return null;
    }

    @Override
    public String getHeaderMessage()
    {
        return MSGS.packageInstallNewPackage();
    }

    @Override
    public Image getInfoIcon()
    {
        return null;
    }
}
