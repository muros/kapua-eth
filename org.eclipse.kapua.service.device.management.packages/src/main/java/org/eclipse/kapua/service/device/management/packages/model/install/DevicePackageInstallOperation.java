package org.eclipse.kapua.service.device.management.packages.model.install;

public interface DevicePackageInstallOperation
{
    public String getName();

    public void setPName(String packageName);

    public String getVersion();

    public void setVersion(String version);

    public DevicePackageInstallStatus getStatus();

    public void setStatus(DevicePackageInstallStatus status);
}
