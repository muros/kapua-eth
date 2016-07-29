package org.eclipse.kapua.service.device.call.kura.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="packages")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlDeploymentPackages
{
    @XmlElement(name="package")
    public XmlDeploymentPackage[] deploymentPackages;

    public XmlDeploymentPackage[] getDeploymentPackages() {
        return deploymentPackages;
    }

    public void setDeploymentPackages(XmlDeploymentPackage[] deploymentPackages) {
        this.deploymentPackages = deploymentPackages;
    }
}
