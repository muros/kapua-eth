package org.eclipse.kapua.service.device.management.deploy;

import org.eclipse.kapua.model.KapuaObjectFactory;

public interface DeviceDeploymentFactory extends KapuaObjectFactory
{
    public DeviceDeploymentPackageListResult newDeviceDeploymentPackageListResultInstance();

    public DeviceDeploymentPackage newDeviceDeploymentPackageInstance();

    public DevicePackageBundleInfoListResult newDevicePackageBundleInfoListResultInstance();

    public DevicePackageBundleInfo newDevicePackageBundleInfoInstance();
}