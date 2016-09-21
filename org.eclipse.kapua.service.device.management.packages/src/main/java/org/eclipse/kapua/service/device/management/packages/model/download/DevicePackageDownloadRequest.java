package org.eclipse.kapua.service.device.management.packages.model.download;

import java.net.URI;

public interface DevicePackageDownloadRequest
{
    public URI getURI();

    public void setURI(URI uri);

    public String getName();

    public void setName(String name);

    public String getVersion();

    public void setVersion(String version);

    public Boolean isInstall();

    public void setInstall(Boolean install);

    public Boolean isReboot();

    public void setReboot(Boolean reboot);

    public Integer getRebootDelay();

    public void setRebootDelay(Integer rebootDelay);
}
