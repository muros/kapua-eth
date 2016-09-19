package org.eclipse.kapua.service.device.management.packages.message.internal;

import org.eclipse.kapua.message.internal.KapuaPayloadImpl;
import org.eclipse.kapua.service.device.management.packages.internal.PackageAppProperties;
import org.eclipse.kapua.service.device.management.request.KapuaRequestPayload;

public class PackageRequestPayload extends KapuaPayloadImpl implements KapuaRequestPayload
{
    public String getDeployInstallUrl()
    {
        return (String) getProperties().get(PackageAppProperties.APP_PROPERTY_DEPLOY_INSTALL_URL.getValue());
    }

    public void setDeployInstallUrl(String deployInstallUrl)
    {
        if (deployInstallUrl != null) {
            getProperties().put(PackageAppProperties.APP_PROPERTY_DEPLOY_INSTALL_URL.getValue(), deployInstallUrl);
        }
    }
    
    public String getDeployInstallFileName()
    {
        return (String) getProperties().get(PackageAppProperties.APP_PROPERTY_DEPLOY_INSTALL_FILENAME.getValue());
    }

    public void setDeployInstallFileName(String installFileName)
    {
        if (installFileName != null) {
            getProperties().put(PackageAppProperties.APP_PROPERTY_DEPLOY_INSTALL_FILENAME.getValue(), installFileName);
        }
    }
    
    public String getDeployUninstallFileName()
    {
        return (String) getProperties().get(PackageAppProperties.APP_PROPERTY_DEPLOY_UNINSTALL_FILENAME.getValue());
    }

    public void setDeployUninstallFileName(String uninstallFileName)
    {
        if (uninstallFileName != null) {
            getProperties().put(PackageAppProperties.APP_PROPERTY_DEPLOY_UNINSTALL_FILENAME.getValue(), uninstallFileName);
        }
    }

}
