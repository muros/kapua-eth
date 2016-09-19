package org.eclipse.kapua.service.device.management.packages.internal;

import org.eclipse.kapua.service.device.management.packages.DevicePackageFactory;
import org.eclipse.kapua.service.device.management.packages.DevicePackage;
import org.eclipse.kapua.service.device.management.packages.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.DevicePackageBundleInfo;

public class DevicePackageFactoryImpl implements DevicePackageFactory
{

    @Override
    public DevicePackages newDeviceDeploymentPackages()
    {
        return new DevicePackagesImpl();
    }

    @Override
    public DevicePackage newDeviceDeploymentPackage()
    {
        return new DevicePackageImpl();
    }

    @Override
    public DevicePackageBundleInfo newDevicePackageBundleInfo()
    {
        return new DevicePackageBundleInfoImpl();
    }

}
