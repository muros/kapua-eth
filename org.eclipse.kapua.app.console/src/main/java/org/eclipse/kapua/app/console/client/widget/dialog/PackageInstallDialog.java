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
