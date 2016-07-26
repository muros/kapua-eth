package org.eclipse.kapua.service.device.management.configuration;

import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.kapua.model.config.metatype.Tocd;

@XmlRootElement(name="configuration")
public interface DeviceComponentConfiguration
{
    public String getComponentId();

    @XmlAttribute(name="pid")
    public void setComponentId(String componentId);

    public String getComponentName();

    @XmlTransient
    public void setComponentName(String unescapedComponentName);

    public void setDefinition(Tocd definition);
    public Tocd getDefinition();

    public Map<String, Object> getProperties();

    @XmlElement(name="properties")
    public void setProperties(Map<String, Object> properties);

}
