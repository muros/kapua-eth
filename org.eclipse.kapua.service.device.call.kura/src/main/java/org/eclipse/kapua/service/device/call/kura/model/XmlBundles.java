package org.eclipse.kapua.service.device.call.kura.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="bundles")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlBundles
{
    @XmlElement(name="bundle")
    public XmlBundle[] bundles;

    public XmlBundle[] getBundles() {
        return bundles;
    }

    public void setBundles(XmlBundle[] bundles) {
        this.bundles = bundles;
    }
}
