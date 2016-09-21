package org.eclipse.kapua.service.device.management.packages;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackage;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageBundleInfo;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOperation;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;

public interface DevicePackageFactory extends KapuaObjectFactory
{
    public DevicePackages newDeviceDeploymentPackages();

    public DevicePackage newDeviceDeploymentPackage();

    public DevicePackageBundleInfo newDevicePackageBundleInfo();

    //
    // Download operation
    //
    public DevicePackageDownloadRequest newPackageDownloadRequest();

    public DevicePackageDownloadOperation newPackageDownloadOperation();
}
