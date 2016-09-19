package org.eclipse.kapua.service.device.management.packages;

import org.eclipse.kapua.model.KapuaObjectFactory;

public interface DevicePackageFactory extends KapuaObjectFactory
{
    public DevicePackages newDeviceDeploymentPackages();

    public DevicePackage newDeviceDeploymentPackage();

    public DevicePackageBundleInfo newDevicePackageBundleInfo();
}
