package org.eclipse.kapua.service.device.management.packages;

import java.util.List;

public interface DevicePackageBundleInfos
{
    public <B extends DevicePackageBundleInfo> List<B> getBundlesInfos();
}
