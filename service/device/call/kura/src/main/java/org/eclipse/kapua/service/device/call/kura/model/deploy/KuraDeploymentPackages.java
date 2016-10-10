package org.eclipse.kapua.service.device.call.kura.model.deploy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "packages")
@XmlAccessorType(XmlAccessType.FIELD)
public class KuraDeploymentPackages
{
    @XmlElement(name = "package")
    public KuraDeploymentPackage[] deploymentPackages;

    public KuraDeploymentPackage[] getDeploymentPackages()
    {
        return deploymentPackages;
    }

    public void setDeploymentPackages(KuraDeploymentPackage[] deploymentPackages)
    {
        this.deploymentPackages = deploymentPackages;
    }
}
