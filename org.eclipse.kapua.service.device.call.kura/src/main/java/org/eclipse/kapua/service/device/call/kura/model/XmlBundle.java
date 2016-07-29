package org.eclipse.kapua.service.device.call.kura.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="bundle")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder= {"name","version","id","state"})
public class XmlBundle
{
    @XmlElement(name="name")
    public String name;

    @XmlElement(name="version")
    public String version;

    @XmlElement(name="id")
    public long id;

    @XmlElement(name="state")
    public String state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
