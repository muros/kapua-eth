package org.eclipse.kapua.service.device.management.configuration;

import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.model.config.metatype.KapuaTocd;

@XmlRootElement(name = "configuration")
public interface DeviceComponentConfiguration
{
    @XmlAttribute(name = "pid")
    public void setId(String Id);

    public String getId();

    @XmlAttribute(name = "name")
    public void setName(String unescapedComponentName);

    public String getName();

    @XmlAttribute(name = "definition")
    public KapuaTocd getDefinition();

    @XmlElement(name = "properties")
    public void setProperties(Map<String, Object> properties);

    public Map<String, Object> getProperties();
}
