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
package org.eclipse.kapua.service.device.management.deploy.message.internal;

import org.eclipse.kapua.message.internal.KapuaPayloadImpl;
import org.eclipse.kapua.service.device.management.deploy.internal.DeployAppProperties;
import org.eclipse.kapua.service.device.management.request.KapuaRequestPayload;

public class DeployRequestPayload extends KapuaPayloadImpl implements KapuaRequestPayload
{
    public String getDeployInstallUrl()
    {
        return (String) getProperties().get(DeployAppProperties.APP_PROPERTY_DEPLOY_INSTALL_URL.getValue());
    }

    public void setDeployInstallUrl(String deployInstallUrl)
    {
        if (deployInstallUrl != null) {
            getProperties().put(DeployAppProperties.APP_PROPERTY_DEPLOY_INSTALL_URL.getValue(), deployInstallUrl);
        }
    }
    
    public String getDeployInstallFileName()
    {
        return (String) getProperties().get(DeployAppProperties.APP_PROPERTY_DEPLOY_INSTALL_FILENAME.getValue());
    }

    public void setDeployInstallFileName(String installFileName)
    {
        if (installFileName != null) {
            getProperties().put(DeployAppProperties.APP_PROPERTY_DEPLOY_INSTALL_FILENAME.getValue(), installFileName);
        }
    }
    
    public String getDeployUninstallFileName()
    {
        return (String) getProperties().get(DeployAppProperties.APP_PROPERTY_DEPLOY_UNINSTALL_FILENAME.getValue());
    }

    public void setDeployUninstallFileName(String uninstallFileName)
    {
        if (uninstallFileName != null) {
            getProperties().put(DeployAppProperties.APP_PROPERTY_DEPLOY_UNINSTALL_FILENAME.getValue(), uninstallFileName);
        }
    }

}
