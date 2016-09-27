package org.eclipse.kapua.service.device.management.bundle.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;

@XmlRootElement(name = "bundle")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceBundleImpl implements DeviceBundle
{
    @XmlElement(name = "id")
    public long id;

    @XmlElement(name = "name")
    public String name;

    @XmlElement(name = "version")
    public String version;

    @XmlElement(name = "state")
    public String state;

    public DeviceBundleImpl()
    {}

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

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

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }
}
