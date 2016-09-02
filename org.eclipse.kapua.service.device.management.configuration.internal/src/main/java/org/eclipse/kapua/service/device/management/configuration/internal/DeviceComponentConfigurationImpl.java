package org.eclipse.kapua.service.device.management.configuration.internal;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.commons.configuration.metatype.XmlConfigPropertiesAdapter;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceComponentConfigurationImpl implements DeviceComponentConfiguration
{
    @XmlElement(name = "id")
    private String              id;

    @XmlElement(name = "name")
    private String              name;

    @XmlElementRef
    private KapuaTocd           definition;

    @XmlElement(name = "properties")
    @XmlJavaTypeAdapter(XmlConfigPropertiesAdapter.class)
    private Map<String, Object> properties;

    public DeviceComponentConfigurationImpl()
    {
    }

    public DeviceComponentConfigurationImpl(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    // @Override
    public void setDefinition(KapuaTocd definition)
    {
        this.definition = definition;
    }

    @SuppressWarnings("unchecked")
    @Override
    public KapuaTocd getDefinition()
    {
        return definition;
    }

    @Override
    public Map<String, Object> getProperties()
    {
        return properties;
    }

    @Override
    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }
}
