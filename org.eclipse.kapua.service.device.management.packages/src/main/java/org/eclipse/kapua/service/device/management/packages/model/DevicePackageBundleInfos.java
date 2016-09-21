package org.eclipse.kapua.service.device.management.packages.model;

import java.util.List;

public interface DevicePackageBundleInfos
{
    public <B extends DevicePackageBundleInfo> List<B> getBundlesInfos();
}
