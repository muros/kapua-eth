package org.eclipse.kapua.commons.configuration;

import java.util.Properties;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;

public interface ServiceConfigCreator extends KapuaUpdatableEntityCreator<ServiceConfig>
{
    public String getPid();

    public void setPid(String pid);

    public Properties getConfigurations() throws KapuaException;

    public void setConfigurations(Properties configurations) throws KapuaException;
}
