package org.eclipse.kapua.service.device.management.bundle.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;

@XmlRootElement(name = "bundles")
public class DeviceBundlesImpl implements DeviceBundles
{
    @XmlElement(name = "bundle")
    private List<DeviceBundleImpl> bundles;

    @Override
    public List<DeviceBundleImpl> getBundles()
    {
        if (bundles == null) {
            bundles = new ArrayList<>();
        }

        return bundles;
    }
}
