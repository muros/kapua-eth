package org.eclipse.kapua.service.device.management.packages.internal;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.device.management.packages.DevicePackage;

@XmlRootElement(name = "package")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "version", "bundleInfos", "installDate" })
public class DevicePackageImpl implements DevicePackage
{
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "version")
    private String version;

    @XmlElement(name = "bundlesInfos")
    private DevicePackageBundleInfosImpl bundleInfos;

    @XmlElement(name = "installDate")
    private Date installDate;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    @SuppressWarnings("unchecked")
    public DevicePackageBundleInfosImpl getBundleInfos()
    {
        if (bundleInfos == null) {
            bundleInfos = new DevicePackageBundleInfosImpl();
        }
        return bundleInfos;
    }

    public Date getInstallDate()
    {
        return installDate;
    }

    public void setInstallDate(Date installDate)
    {
        this.installDate = installDate;
    }
}
